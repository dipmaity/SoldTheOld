package com.quantumwebgarden.soldold.models

import android.content.Context
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.quantumwebgarden.soldold.R
import java.io.IOException

class GliderLoader(val context:Context) {
    fun loadUserPicture(image: Any, imageView: ImageView) {
        try {
            if(image != "") {
                Glide.with(context).load(image).centerCrop().into(imageView)
            }
            else {
                Glide.with(context).load(R.drawable.ic_user_placeholder).centerCrop().into(imageView)
            }
        }
        catch(e:IOException){
            e.printStackTrace()
        }
    }

    fun loadProductPicture(image: Any, imageView: ImageView) {
        try {
            // Load the user image in the ImageView.
            if(image != "") {
                Glide
                    .with(context)
                    .load(image) // Uri or URL of the image
                    .centerCrop()// Scale type of the image.
                    .into(imageView) // the view in which the image will be loaded.
            }
            else {
                Glide.with(context).load(R.drawable.baseline_products_24).centerCrop().into(imageView)

            }
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }
}