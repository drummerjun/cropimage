package com.scribble.cropimage

import android.content.Context
import android.graphics.drawable.BitmapDrawable
import com.bumptech.glide.Glide
import com.bumptech.glide.Registry
import com.bumptech.glide.annotation.GlideModule
import com.bumptech.glide.module.AppGlideModule

@GlideModule
class GlideCropperGlideModule : AppGlideModule() {
    override fun registerComponents(context: Context, glide: Glide, registry: Registry) {
        registry.prepend(CroppedImage::class.java,
            CroppedImageDecoderInput::class.java,
            CroppedImageModelLoaderFactory(context.resources))
            .prepend(CroppedImageDecoderInput::class.java,
                BitmapDrawable::class.java,
                CroppedBitmapDecoder(context.resources))
    }
}