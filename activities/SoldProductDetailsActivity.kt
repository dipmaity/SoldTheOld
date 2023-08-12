package com.quantumwebgarden.soldold.activities

import android.os.Bundle
import android.view.View
import com.quantumwebgarden.soldold.databinding.ActivitySoldProductDetailsBinding
import com.quantumwebgarden.soldold.models.Constants
import com.quantumwebgarden.soldold.models.GliderLoader
import com.quantumwebgarden.soldold.models.SoldProduct
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class SoldProductDetailsActivity : StartActivity() {
    private lateinit var binding: ActivitySoldProductDetailsBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySoldProductDetailsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setUpActionBar(binding.toolbarSoldProductDetailsActivity)
        var productDetails: SoldProduct = SoldProduct()

        if (intent.hasExtra(Constants.EXTRA_SOLD_PRODUCT_DETAILS)) {
            productDetails =
                intent.getParcelableExtra<SoldProduct>(Constants.EXTRA_SOLD_PRODUCT_DETAILS)!!
        }
        setupUI(productDetails)
    }

    private fun setupUI(productDetails: SoldProduct) {

        binding.tvOrderDetailsId.text = productDetails.order_id

        // Date Format in which the date will be displayed in the UI.
        val dateFormat = "dd MMM yyyy HH:mm"
        // Create a DateFormatter object for displaying date in specified format.
        val formatter = SimpleDateFormat(dateFormat, Locale.getDefault())

        // Create a calendar object that will convert the date and time value in milliseconds to date.
        val calendar: Calendar = Calendar.getInstance()
        calendar.timeInMillis = productDetails.order_date
        binding.tvOrderDetailsDate.text = formatter.format(calendar.time)

        GliderLoader(this@SoldProductDetailsActivity).loadProductPicture(
            productDetails.image,
            binding.ivProductItemImage
        )
        binding.tvProductItemName.text = productDetails.title
        binding.tvProductItemPrice.text ="Rs ${productDetails.price}"
//        tv_sold_product_quantity.text = productDetails.sold_quantity

        binding.tvSoldDetailsAddressType.text = productDetails.address.type
        binding.tvSoldDetailsFullName.text = productDetails.address.name
        binding.tvSoldDetailsAddress.text =
            "${productDetails.address.address}, ${productDetails.address.zipCode}"
        if(productDetails.address.additionalNote != "") {
            binding.tvSoldDetailsAdditionalNote.text = productDetails.address.additionalNote
        }
        else {
            binding.tvSoldDetailsAdditionalNote.text = "N/A"
        }
        if (productDetails.address.otherDetails.isNotEmpty()) {
            binding.tvSoldDetailsOtherDetails.visibility = View.VISIBLE
            if(productDetails.address.otherDetails != "") {
                binding.tvSoldDetailsOtherDetails.text = productDetails.address.otherDetails
            }
            else {
                binding.tvSoldDetailsOtherDetails.text = "N/A"
            }

        } else {
            binding.tvSoldDetailsOtherDetails.visibility = View.GONE
        }
        binding.tvSoldDetailsMobileNumber.text = "+91 ${productDetails.address.mobileNumber}"
        binding.tvSoldProductSubTotal.text = productDetails.sub_total_amount
        binding.tvSoldProductShippingCharge.text = productDetails.shipping_charge
        binding.tvSoldProductTotalAmount.text = productDetails.total_amount
    }
}