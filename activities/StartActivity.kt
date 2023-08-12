package com.quantumwebgarden.soldold.activities

import android.app.Dialog
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import com.google.android.material.snackbar.Snackbar
import com.quantumwebgarden.soldold.R
import com.quantumwebgarden.soldold.databinding.ActivityStartBinding

open class StartActivity : AppCompatActivity() {
    private lateinit var bining:ActivityStartBinding
    private lateinit var mProgressDialog: Dialog
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        bining = ActivityStartBinding.inflate(layoutInflater)
        setContentView(bining.root)
    }

    fun setUpActionBar(toolbar: Toolbar) {
        setSupportActionBar(toolbar)
        val actionBar = supportActionBar
        if(actionBar != null) {
            actionBar.setDisplayShowTitleEnabled(false)
            actionBar.setDisplayHomeAsUpEnabled(true)
            actionBar.setHomeAsUpIndicator(R.drawable.ic_back_toolbar_24)
        }
        toolbar.setNavigationOnClickListener { onBackPressed() }
    }

    fun showErrorSnackBar(message: String, errorMessage: Boolean) {
        val snackBar = Snackbar.make(findViewById(android.R.id.content), message, Snackbar.LENGTH_LONG)
        val snackBarView = snackBar.view
        if(errorMessage) {
            snackBarView.setBackgroundColor(ContextCompat.getColor(this@StartActivity, R.color.red))
        }
        else {
            snackBarView.setBackgroundColor(ContextCompat.getColor(this@StartActivity, R.color.green))
        }
        snackBar.show()
    }

    fun showProgressDialoge(text:String) {
        mProgressDialog = Dialog(this)
        /*Set the screen content from a layout resource.
        The resource will be inflated, adding all top-level views to the screen.*/
        mProgressDialog.setContentView(R.layout.progress_dialoge)

        mProgressDialog.findViewById<TextView>(R.id.pd_tv_wait).text = text

        mProgressDialog.setCancelable(false)
        mProgressDialog.setCanceledOnTouchOutside(false)

        //Start the dialog and display it on screen.
        mProgressDialog.show()
    }

    fun hideProgressDialog() {
        mProgressDialog.dismiss()
    }
}