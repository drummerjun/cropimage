package com.scribble.cropimage

import android.content.res.Resources
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.BitmapRegionDecoder
import android.graphics.Rect
import android.graphics.drawable.BitmapDrawable
import android.support.annotation.DrawableRes
import android.support.annotation.RawRes
import com.bumptech.glide.Priority
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.Options
import com.bumptech.glide.load.ResourceDecoder
import com.bumptech.glide.load.data.DataFetcher
import com.bumptech.glide.load.engine.Resource
import com.bumptech.glide.load.model.ModelLoader
import com.bumptech.glide.load.model.ModelLoaderFactory
import com.bumptech.glide.load.model.MultiModelLoaderFactory
import com.bumptech.glide.load.resource.SimpleResource
import com.bumptech.glide.signature.ObjectKey
import java.io.InputStream

class CroppedImage(val resourceId: Int, var viewWidth: Int, var viewHeight: Int, var horizontalOffset: Int = 0, var verticalOffset: Int = 0) {
    override fun equals(other: Any?): Boolean {
        if(other is CroppedImage) {
            return resourceId == other.resourceId
                    && viewWidth == other.viewWidth
                    && viewHeight == other.viewHeight
                    && horizontalOffset == other.horizontalOffset
                    && verticalOffset == other.verticalOffset
        }
        return this == other
    }

    override fun hashCode(): Int {
        return (resourceId.toString() +
                viewWidth.toString() +
                viewHeight.toString() +
                horizontalOffset.toString() +
                verticalOffset.toString()).hashCode()
    }
}

class CroppedImageModelLoader(val resources: Resources) : ModelLoader<CroppedImage, CroppedImageDecoderInput> {
    override fun handles(model: CroppedImage) = true

    override fun buildLoadData(
        model: CroppedImage,
        width: Int,
        height: Int,
        options: Options
    ): ModelLoader.LoadData<CroppedImageDecoderInput>? {
        return ModelLoader.LoadData<CroppedImageDecoderInput>(
            ObjectKey(model),
            CroppedImageDataFetcher(resources, model))
    }
}

class CroppedImageModelLoaderFactory(private val resources: Resources) :
    ModelLoaderFactory<CroppedImage, CroppedImageDecoderInput> {

    override fun build(multiFactory: MultiModelLoaderFactory):
            ModelLoader<CroppedImage, CroppedImageDecoderInput> = CroppedImageModelLoader(resources)

    override fun teardown() { }
}

class CroppedImageDataFetcher(
    val resources: Resources,
    val model: CroppedImage) : DataFetcher<CroppedImageDecoderInput> {

    override fun getDataClass(): Class<CroppedImageDecoderInput> = CroppedImageDecoderInput::class.java

    override fun getDataSource(): DataSource = DataSource.LOCAL

    override fun loadData(priority: Priority, callback: DataFetcher.DataCallback<in CroppedImageDecoderInput>) {
        val intermediate = CroppedImageDecoderInput(
            resId = model.resourceId,
            viewHeight = model.viewHeight,
            viewWidth = model.viewWidth,
            verticalOffset = model.verticalOffset,
            horizontalOffset = model.horizontalOffset
        )
        callback.onDataReady(intermediate)
    }

    override fun cancel() { }

    override fun cleanup() { }
}

class CroppedBitmapDecoder(val resources: Resources): ResourceDecoder<CroppedImageDecoderInput, BitmapDrawable> {
    override fun handles(source: CroppedImageDecoderInput, options: Options): Boolean {
        return true
    }

    override fun decode(source: CroppedImageDecoderInput, width: Int, height: Int, options: Options): Resource<BitmapDrawable>? {
        val bitmap: Bitmap
        var decoder: BitmapRegionDecoder? = null
        var inputStream: InputStream? = null

        val bitmapFactoryOptions = BitmapFactory.Options().apply {
            // Decode image dimensions only, not content
            inJustDecodeBounds = true
        }

        // Determine the image's height and width
        BitmapFactory.decodeResource(resources, source.resId, bitmapFactoryOptions)
        val imageHeight = bitmapFactoryOptions.outHeight
        val imageWidth = bitmapFactoryOptions.outWidth

        try {
            inputStream = resources.openRawResource(source.resId)
            decoder = BitmapRegionDecoder.newInstance(inputStream, false)

            // Ensure the cropping and translation region doesn't exceed the image dimensions
            val region = Rect(source.horizontalOffset,
                source.verticalOffset,
                Math.min(source.viewWidth + source.horizontalOffset, imageWidth),
                Math.min(source.viewHeight + source.verticalOffset, imageHeight))

            // Decode image content within the cropping region
            bitmapFactoryOptions.inJustDecodeBounds = false
            bitmap = decoder!!.decodeRegion(region, bitmapFactoryOptions)
        } finally {
            inputStream?.close()
            decoder?.recycle()
        }

        val drawable = BitmapDrawable(resources, bitmap)
        return SimpleResource<BitmapDrawable>(drawable)
    }
}

class CroppedImageDecoderInput(
    @RawRes @DrawableRes val resId: Int,
    val viewWidth: Int,
    val viewHeight: Int,
    val horizontalOffset: Int = 0,
    val verticalOffset: Int = 0
)

