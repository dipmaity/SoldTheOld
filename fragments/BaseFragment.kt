package com.quantumwebgarden.soldold.fragments

import android.app.Dialog
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.quantumwebgarden.soldold.R
import com.quantumwebgarden.soldold.databinding.FragmentBaseBinding

open class BaseFragment : Fragment() {
    private lateinit var binding: FragmentBaseBinding
    private lateinit var mProgressDialog: Dialog
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentBaseBinding.inflate(inflater, container, false)
        // Inflate the layout for this fragment
        return binding.root
    }

    fun showProgressDialog(text: String) {
        mProgressDialog = Dialog(requireActivity())

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