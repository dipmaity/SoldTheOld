package com.quantumwebgarden.soldold.activities

import android.content.Intent
import android.os.Bundle
import android.view.View
import com.google.firebase.auth.FirebaseAuth
import com.quantumwebgarden.soldold.R
import com.quantumwebgarden.soldold.databinding.ActivitySettingsBinding
import com.quantumwebgarden.soldold.firestore.FirestoreClass
import com.quantumwebgarden.soldold.models.Constants
import com.quantumwebgarden.soldold.models.GliderLoader
import com.quantumwebgarden.soldold.models.User

class SettingsActivity : StartActivity(), View.OnClickListener {
    private lateinit var mUserDetails: User
    private lateinit var binding: ActivitySettingsBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySettingsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setUpActionBar(binding.toolbarSettingsActivity)
        binding.tvTitle.text = resources.getString(R.string.title_settings)
        binding.tvEdit.setOnClickListener (this@SettingsActivity)
        binding.btnLogout.setOnClickListener (this@SettingsActivity)
        binding.llAddress.setOnClickListener (this@SettingsActivity)
    }

    override fun onClick(v: View?) {
        if(v != null) {
            when(v.id) {
                R.id.tv_edit -> {
                    val intent = Intent(this@SettingsActivity, UserProfileActivity::class.java)
                    intent.putExtra(Constants.EXTRA_USER_DETAILS, mUserDetails)
                    startActivity(intent)
                }
                R.id.btn_logout -> {
                    FirebaseAuth.getInstance().signOut()
                    val intent = Intent(this@SettingsActivity, LoginActivity::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                    startActivity(intent)
                    finish()
                }
                R.id.ll_address -> {
                    val intent = Intent(this@SettingsActivity, AddressListActivity::class.java)
                    startActivity(intent)
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        getUserDetails()
    }

    private fun getUserDetails() {
        FirestoreClass().getUserDetails(this@SettingsActivity)

    }

    fun userDetailsSuccess(user: User) {
        mUserDetails = user
        // Load the image using the Glide Loader class.
        GliderLoader(this@SettingsActivity).loadUserPicture(user.image, binding.ivUserPhoto)

        binding.tvName.text = user.name
        binding.tvEmail.text = user.email
        binding.tvMobileNumber.text = "${user.mobile}"
    }
}