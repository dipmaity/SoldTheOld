package com.quantumwebgarden.soldold.activities

import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.View
import android.widget.Toast
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.Task
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import com.quantumwebgarden.soldold.MainActivity
import com.quantumwebgarden.soldold.R
import com.quantumwebgarden.soldold.databinding.ActivityLoginBinding
import com.quantumwebgarden.soldold.firestore.FirestoreClass
import com.quantumwebgarden.soldold.models.User

class LoginActivity : StartActivity(), View.OnClickListener {
    private lateinit var binding : ActivityLoginBinding
    private val auth = FirebaseAuth.getInstance()
    // For google signin
    val Req_Code: Int = 123
    private lateinit var mGoogleSignInClient: GoogleSignInClient
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)
        FirebaseApp.initializeApp(this@LoginActivity)
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()
        mGoogleSignInClient = GoogleSignIn.getClient(this@LoginActivity, gso)
        binding.laTvForgetPassword.setOnClickListener(this)
        binding.laBtLogin.setOnClickListener(this)
        binding.laTvRegister.setOnClickListener(this)
        binding.laIvGoogle.setOnClickListener(this)
    }

    override fun onClick(v: View?) {

        if (v != null) {
            when (v.id) {
                R.id.la_tv_forget_password -> {
                    // Launch the forgot password screen when the user clicks on the forgot password text.
                    val intent = Intent(this@LoginActivity, ForgotPasswordActivity::class.java)
                    startActivity(intent)
                }

                R.id.la_bt_login -> {
                    logInRegisteredUser()
                }

                R.id.la_tv_register -> {
                    // Launch the register screen when the user clicks on the text.
                    val intent = Intent(this@LoginActivity, RegisterActivity::class.java)
                    startActivity(intent)
                }

                R.id.la_iv_google -> {
                    signInGoogle()
                }
            }
        }
    }

    private fun signInGoogle() {
        val signInIntent: Intent = mGoogleSignInClient.signInIntent
        startActivityForResult(signInIntent, Req_Code)
    }

    private fun logInRegisteredUser() {
        if (validateLoginDetails()) {

            // Show the progress dialog.
            showProgressDialoge(resources.getString(R.string.please_wait))

            // Get the text from editText and trim the space
            val email = binding.laEtEmail.text.toString().trim { it <= ' ' }
            val password = binding.laEtPassword.text.toString().trim { it <= ' ' }

            // Log-In using FirebaseAuth
            FirebaseAuth.getInstance().signInWithEmailAndPassword(email, password)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        FirestoreClass().getUserDetails(this@LoginActivity)
                    } else {
                        // Hide the progress dialog
                        hideProgressDialog()
                        showErrorSnackBar(task.exception!!.message.toString(), true)
                    }
                }
        }
    }

    private fun validateLoginDetails(): Boolean {
        return when {
            TextUtils.isEmpty(binding.laEtEmail.text.toString().trim { it <= ' ' }) -> {
                showErrorSnackBar(resources.getString(R.string.err_msg_enter_email), true)
                false
            }
            TextUtils.isEmpty(binding.laEtPassword.text.toString().trim { it <= ' ' }) -> {
                showErrorSnackBar(resources.getString(R.string.err_msg_enter_password), true)
                false
            }
            else -> {
                true
            }
        }
    }
    fun userLoggedInSuccess() {
        // Hide the progress dialog.
        hideProgressDialog()
        // Redirect the user to Dashboard Screen after log in.
        val intent = Intent(this@LoginActivity, MainActivity::class.java)
        startActivity(intent)
        finish()
    }

    // onActivityResult() function : this is where
    // we provide the task and data for the Google Account
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == Req_Code) {
            val task: Task<GoogleSignInAccount> = GoogleSignIn.getSignedInAccountFromIntent(data)
            handleResult(task)
        }
    }

    private fun handleResult(completedTask: Task<GoogleSignInAccount>) {
        try {
            showProgressDialoge(resources.getString(R.string.please_wait))
            val account: GoogleSignInAccount? = completedTask.getResult(ApiException::class.java)
            if (account != null) {
                auth.fetchSignInMethodsForEmail(account.email!!)
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            val result = task.result
                            // The email is authenticated with Google Sign-In

                            if (result != null && result.signInMethods != null && result.signInMethods!!.isNotEmpty()) {

                                val credential = GoogleAuthProvider.getCredential(account.idToken, null)
                                auth.signInWithCredential(credential).addOnCompleteListener { task ->
                                    if (task.isSuccessful) {
                                        // Firebase registered user
                                        val firebaseUser: FirebaseUser = task.result!!.user!!
                                        FirestoreClass().getUserDetailsWithId(this@LoginActivity, firebaseUser.uid)
                                    }
                                }
                            } else {
                                // The email is not authenticated with Google Sign-In
                                UpdateUI(account)
                            }
                        } else {
                            // An error occurred while checking the email authentication status
                            hideProgressDialog()
                            Toast.makeText(this, "Technical Fault Try Later", Toast.LENGTH_SHORT).show()
                        }

                    }
            }
        } catch (e: ApiException) {
            hideProgressDialog()
            Toast.makeText(this, e.toString(), Toast.LENGTH_SHORT).show()
        }
    }

    // this is where we update the UI after Google signin takes place
    private fun UpdateUI(account: GoogleSignInAccount) {
        val credential = GoogleAuthProvider.getCredential(account.idToken, null)
        auth.signInWithCredential(credential).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                // Firebase registered user
                val firebaseUser: FirebaseUser = task.result!!.user!!
                val user = User(firebaseUser.uid,
                    account.displayName.toString(),
                    account.email.toString(),
                    account.photoUrl.toString()
                )
                FirestoreClass().registerUser(this@LoginActivity, user)
            }
        }
    }

}