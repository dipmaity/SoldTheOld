package com.quantumwebgarden.soldold.activities

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.text.TextUtils
import android.view.View
import android.widget.Toast
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.Task
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import com.quantumwebgarden.soldold.MainActivity
import com.quantumwebgarden.soldold.R
import com.quantumwebgarden.soldold.databinding.ActivityRegisterBinding
import com.quantumwebgarden.soldold.firestore.FirestoreClass
import com.quantumwebgarden.soldold.models.User

class RegisterActivity : StartActivity(), View.OnClickListener {
    private lateinit var binding: ActivityRegisterBinding
    private val auth = FirebaseAuth.getInstance()
    // For google signin
    val Req_Code: Int = 123
    private lateinit var mGoogleSignInClient: GoogleSignInClient
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)
        FirebaseApp.initializeApp(this@RegisterActivity)
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()
        mGoogleSignInClient = GoogleSignIn.getClient(this@RegisterActivity, gso)
        setUpActionBar(binding.raToolbar)
        binding.raBtRegister.setOnClickListener(this)
        binding.raTvLogin.setOnClickListener(this)
        binding.raIvGoogle.setOnClickListener(this)
        binding.tvTermsCondition.setOnClickListener(this)
    }

    override fun onClick(v: View?) {
        if(v != null) {
            when(v.id) {
                R.id.ra_bt_register -> {
                    registerUser()
                }

                R.id.ra_tv_login -> {
                    // Here when the user click on login text we can either call the login activity or call the onBackPressed function.
                    // We will call the onBackPressed function.
                    onBackPressed()
                }

                R.id.ra_iv_google -> {
                    signInGoogle()
                }

                R.id.tv_terms_condition -> {
                    val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse("https://quantumwebgarden.github.io/qrc_privacypolicy.html"))
                    startActivity(browserIntent)
                }
            }
        }
    }

    private fun registerUser() {
        // Check with validate function if the entries are valid or not.
        if (validateRegisterDetails()) {
            // Show the progress dialog.
            showProgressDialoge(resources.getString(R.string.please_wait))
            val email: String = binding.raEtEmail.text.toString().trim { it <= ' ' }
            val password: String = binding.raEtPassword.text.toString().trim { it <= ' ' }
            auth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(
                    OnCompleteListener<AuthResult> { task ->
                        // If the registration is successfully done
                        if (task.isSuccessful) {
                            // Firebase registered user
                            val firebaseUser: FirebaseUser = task.result!!.user!!

                            // Instance of User data model class.
                            val user = User(
                                firebaseUser.uid,
                                binding.raEtName.text.toString().trim { it <= ' ' },
                                email
                            )
                            // Pass the required values in the constructor.
                            FirestoreClass().registerUser(this@RegisterActivity, user)
                        } else {
                            // Hide the progress dialog
                            hideProgressDialog()
                            // If the registering is not successful then show error message.
                            showErrorSnackBar(task.exception!!.message.toString(), true)
                        }
                    })
        }
    }

    private fun validateRegisterDetails(): Boolean {
        return when {
            TextUtils.isEmpty(binding.raEtName.text.toString().trim { it <= ' ' }) -> {
                showErrorSnackBar(resources.getString(R.string.err_msg_enter_name), true)
                false
            }

            TextUtils.isEmpty(binding.raEtEmail.text.toString().trim { it <= ' ' }) -> {
                showErrorSnackBar(resources.getString(R.string.err_msg_enter_email), true)
                false
            }

            TextUtils.isEmpty(binding.raEtPassword.text.toString().trim { it <= ' ' }) -> {
                showErrorSnackBar(resources.getString(R.string.err_msg_enter_password), true)
                false
            }

            TextUtils.isEmpty(binding.raEtRePassword.text.toString().trim { it <= ' ' }) -> {
                showErrorSnackBar(
                    resources.getString(R.string.err_msg_enter_confirm_password),
                    true
                )
                false
            }

            binding.raEtPassword.text.toString()
                .trim { it <= ' ' } != binding.raEtRePassword.text.toString()
                .trim { it <= ' ' } -> {
                showErrorSnackBar(
                    resources.getString(R.string.err_msg_password_and_confirm_password_mismatch),
                    true
                )
                false
            }

            !binding.cbTermsAndCondition.isChecked -> {
                showErrorSnackBar(
                    resources.getString(R.string.err_msg_agree_terms_and_condition),
                    true
                )
                false
            }

            else -> {
                true
            }
        }
    }

    private fun signInGoogle() {
        val signInIntent: Intent = mGoogleSignInClient.signInIntent
        startActivityForResult(signInIntent, Req_Code)
    }

    fun userRegistrationSuccess() {
        // Hide the progress dialog
        hideProgressDialog()

        Toast.makeText(
            this@RegisterActivity,
            resources.getString(R.string.register_success),
            Toast.LENGTH_SHORT
        ).show()


        /**
         * Here the new user registered is automatically signed-in so we just sign-out the user from firebase
         * and send him to Intro Screen for Sign-In
         */
        FirebaseAuth.getInstance().signOut()
        // Finish the Register Screen
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
                            if (result != null && result.signInMethods != null && result.signInMethods!!.isNotEmpty()) {
                                // The email is authenticated with Google Sign-In

                                val credential = GoogleAuthProvider.getCredential(account.idToken, null)
                                auth.signInWithCredential(credential).addOnCompleteListener { task ->
                                    if (task.isSuccessful) {
                                        // Firebase registered user
                                        val firebaseUser: FirebaseUser = task.result!!.user!!
                                        FirestoreClass().getUserDetailsWithId(this@RegisterActivity, firebaseUser.uid)
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
                FirestoreClass().registerUser(this@RegisterActivity, user)
            }
        }
    }

    fun userLoggedInSuccess() {
        // Hide the progress dialog.
        hideProgressDialog()
        // Redirect the user to Dashboard Screen after log in.
        val intent = Intent(this@RegisterActivity, MainActivity::class.java)
        startActivity(intent)
        finish()
    }
}