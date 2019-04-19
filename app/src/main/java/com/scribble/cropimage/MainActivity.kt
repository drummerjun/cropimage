package com.scribble.cropimage

import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val img101 = findViewById<ImageView>(R.id.image101)
        val img102 = findViewById<ImageView>(R.id.image102)
        val img103 = findViewById<ImageView>(R.id.image103)
//        Glide.with(this).load(R.drawable.waifu).into(imgView)


        val model1 = CroppedImage(
            resourceId = R.drawable.waifu,
            viewHeight = 160, viewWidth = 600, horizontalOffset = 0, verticalOffset = 90
        )
        Glide.with(this).load(model1).listener(object: RequestListener<Drawable> {
            override fun onLoadFailed(e: GlideException?, model: Any?, target: Target<Drawable>?, isFirstResource: Boolean): Boolean {
                return false
            }

            override fun onResourceReady(resource: Drawable?, model: Any?, target: Target<Drawable>?, dataSource: DataSource?, isFirstResource: Boolean): Boolean {
                Log.i("HURLEY", "resource=$resource model=$model target=$target dataSource=$dataSource isFirstResource=$isFirstResource")
                Glide.with(this@MainActivity).load(resource).into(img102)

                return false
            }
        }).into(img101)

//        val model2 = CroppedImage(
//            resourceId = R.drawable.waifu,
//            viewHeight = 160, viewWidth = 600, horizontalOffset = 0, verticalOffset = 200
//        )
//        Glide.with(this).load(model2).into(img102)

        val model3 = CroppedImage(
            resourceId = R.drawable.waifu,
            viewHeight = 160, viewWidth = 600, horizontalOffset = 0, verticalOffset = 0
        )
        Glide.with(this).load(model3).into(img103)
    }
}
