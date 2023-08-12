package com.quantumwebgarden.soldold.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.quantumwebgarden.soldold.R
import com.quantumwebgarden.soldold.adapters.SoldProductsListAdapter
import com.quantumwebgarden.soldold.databinding.FragmentSoldProductsBinding
import com.quantumwebgarden.soldold.firestore.FirestoreClass
import com.quantumwebgarden.soldold.models.SoldProduct

class SoldProductsFragment : BaseFragment() {
    private lateinit var binding: FragmentSoldProductsBinding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
// Inflate the layout for this fragment
        binding = FragmentSoldProductsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onResume() {
        super.onResume()

        getSoldProductsList()
    }

    private fun getSoldProductsList() {
        // Show the progress dialog.
        showProgressDialog(resources.getString(R.string.please_wait))

        // Call the function of Firestore class.
        FirestoreClass().getSoldProductsList(this@SoldProductsFragment)

    }

    fun successSoldProductsList(soldProductsList: ArrayList<SoldProduct>) {
        // Hide Progress dialog.
        hideProgressDialog()
        if (soldProductsList.size > 0) {
            binding.rvSoldProductItems.visibility = View.VISIBLE
            binding.tvNoSoldProductsFound.visibility = View.GONE

            binding.rvSoldProductItems.layoutManager = LinearLayoutManager(activity)
            binding.rvSoldProductItems.setHasFixedSize(true)

            val soldProductsListAdapter = SoldProductsListAdapter(requireActivity(), soldProductsList)
            binding.rvSoldProductItems.adapter = soldProductsListAdapter
        } else {
            binding.rvSoldProductItems.visibility = View.GONE
            binding.tvNoSoldProductsFound.visibility = View.VISIBLE
        }
    }
}