package com.quantumwebgarden.soldold.activities

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.quantumwebgarden.soldold.MainActivity
import com.quantumwebgarden.soldold.R
import com.quantumwebgarden.soldold.adapters.CartItemsListAdapter
import com.quantumwebgarden.soldold.databinding.ActivityCheckoutBinding
import com.quantumwebgarden.soldold.firestore.FirestoreClass
import com.quantumwebgarden.soldold.models.Address
import com.quantumwebgarden.soldold.models.CartItem
import com.quantumwebgarden.soldold.models.Constants
import com.quantumwebgarden.soldold.models.Order
import com.quantumwebgarden.soldold.models.Product

class CheckoutActivity : StartActivity() {
    private lateinit var binding: ActivityCheckoutBinding
    private var mAddressDetails: Address? = null
    private lateinit var mProductsList: ArrayList<Product>
    private lateinit var mCartItemsList: ArrayList<CartItem>
    private var mSubTotal: Double = 0.0
    private lateinit var mOrderDetails: Order


    // A global variable for the Total Amount.
    private var mTotalAmount: Double = 0.0
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCheckoutBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setUpActionBar(binding.toolbarCheckoutActivity)
        if (intent.hasExtra(Constants.EXTRA_SELECTED_ADDRESS)) {
            mAddressDetails =
                intent.getParcelableExtra<Address>(Constants.EXTRA_SELECTED_ADDRESS)!!
        }
        if (mAddressDetails != null) {
            binding.tvCheckoutAddressType.text = mAddressDetails?.type
            binding.tvCheckoutFullName.text = mAddressDetails?.name
            binding.tvCheckoutAddress.text = "${mAddressDetails!!.address}, ${mAddressDetails!!.zipCode}"
            if(mAddressDetails?.additionalNote != "") {
                binding.tvCheckoutAdditionalNote.text = mAddressDetails?.additionalNote
            }
            else {
                binding.tvCheckoutAdditionalNote.text = "N/A"
            }
            if (mAddressDetails?.otherDetails!!.isNotEmpty()) {
                if(mAddressDetails?.otherDetails != "") {
                    binding.tvCheckoutOtherDetails.text = mAddressDetails?.otherDetails
                }
                else {
                    binding.tvCheckoutOtherDetails.text = "N/A"
                }
            }
            binding.tvCheckoutMobileNumber.text = mAddressDetails?.mobileNumber
        }
        binding.btnPlaceOrder.setOnClickListener {
            placeAnOrder()
        }
        getProductList()
    }

    private fun getProductList() {
        // Show the progress dialog.
        showProgressDialoge(resources.getString(R.string.please_wait))

        FirestoreClass().getAllProductsList(this@CheckoutActivity)

    }

    private fun placeAnOrder() {
        // Show the progress dialog.
        showProgressDialoge(resources.getString(R.string.please_wait))

        mOrderDetails = Order(
            FirestoreClass().getCurrentUserID(),
            mCartItemsList,
            mAddressDetails!!,
            "My order ${System.currentTimeMillis()}",
            mCartItemsList[0].image,
            mSubTotal.toString(),
            "10.0", // The Shipping Charge is fixed as $10 for now in our case.
            mTotalAmount.toString(),
            System.currentTimeMillis()
        )

        FirestoreClass().placeOrder(this@CheckoutActivity, mOrderDetails)

    }

    fun orderPlacedSuccess() {
        FirestoreClass().updateAllDetails(this@CheckoutActivity, mCartItemsList, mOrderDetails)
    }

    fun allDetailsUpdatedSuccessfully() {
        // Hide the progress dialog.
        hideProgressDialog()
        Toast.makeText(this@CheckoutActivity, "Your order placed successfully.", Toast.LENGTH_SHORT)
            .show()
        val intent = Intent(this@CheckoutActivity, MainActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        finish()
    }

    fun successCartItemsList(cartList: ArrayList<CartItem>) {
        // Hide progress dialog.
        hideProgressDialog()
        for (product in mProductsList) {
            for (cart in cartList) {
                if (product.product_id == cart.product_id) {
                    cart.stock_quantity = product.stock_quantity
                }
            }
        }
        mCartItemsList = cartList
        binding.rvCartListItems.layoutManager = LinearLayoutManager(this@CheckoutActivity)
        binding.rvCartListItems.setHasFixedSize(true)
        val cartListAdapter = CartItemsListAdapter(this@CheckoutActivity, mCartItemsList, false)
        binding.rvCartListItems.adapter = cartListAdapter
        for (item in mCartItemsList) {

            val availableQuantity = item.stock_quantity.toInt()

            if (availableQuantity > 0) {
                val price = item.price.toDouble()
                val quantity = item.cart_quantity.toInt()

                mSubTotal += (price * quantity)
            }
        }

        binding.tvCheckoutSubTotal.text = "Rs ${mSubTotal}"
        // Here we have kept Shipping Charge is fixed as $10 but in your case it may cary. Also, it depends on the location and total amount.
        binding.tvCheckoutShippingCharge.text = "Rs 10.0"

        if (mSubTotal > 0) {
            binding.llCheckoutPlaceOrder.visibility = View.VISIBLE

            mTotalAmount = mSubTotal + 10.0
            binding.tvCheckoutTotalAmount.text = "Rs ${mTotalAmount}"
        } else {
            binding.llCheckoutPlaceOrder.visibility = View.GONE
        }
    }

    fun successProductsListFromFireStore(productsList: ArrayList<Product>) {

        // TODO Step 8: Initialize the global variable of all product list.
        // START
        mProductsList = productsList
        // END

        // TODO Step 10: Call the function to get the latest cart items.
        // START
        getCartItemsList()
    }

    private fun getCartItemsList() {
        FirestoreClass().getCartList(this@CheckoutActivity)
    }
}