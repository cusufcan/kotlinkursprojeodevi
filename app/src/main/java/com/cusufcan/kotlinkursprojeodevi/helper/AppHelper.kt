package com.cusufcan.kotlinkursprojeodevi.helper

import android.content.Intent
import android.graphics.Bitmap
import android.provider.MediaStore
import androidx.activity.result.ActivityResultLauncher

class AppHelper {
    companion object {
        fun imageSmaller(image: Bitmap, maxSize: Int, filter: Boolean): Bitmap {
            var width = image.width
            var height = image.height

            val bitmapRatio = width.toDouble() / height.toDouble()

            if (bitmapRatio > 1) {
                width = maxSize
                height = (width / bitmapRatio).toInt()
            } else {
                height = maxSize
                width = (height * bitmapRatio).toInt()
            }

            return Bitmap.createScaledBitmap(image, width, height, filter)
        }

        fun toGallery(launcher: ActivityResultLauncher<Intent>) {
            val mediaUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI
            val intentAction = Intent.ACTION_PICK

            val intentToGallery = Intent(intentAction, mediaUri)
            launcher.launch(intentToGallery)
        }
    }
}