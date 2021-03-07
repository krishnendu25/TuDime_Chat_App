package com.TuDime.ui.activity.New

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.view.View
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.TuDime.R
import com.oginotihiro.cropview.CropView
import java.io.ByteArrayOutputStream
import java.lang.String


class ImageCropActivity : AppCompatActivity() {
    var Image_pATH:Uri?=null
    var cropView: CropView?=null
    @RequiresApi(Build.VERSION_CODES.M)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_image_crop)
        Instantiation()
        try {
            Image_pATH =Uri.parse(intent.getStringExtra("Pathurl"))
        } catch (e: Exception) {
        }


        val MyVersion = Build.VERSION.SDK_INT
        if (MyVersion > Build.VERSION_CODES.LOLLIPOP_MR1) {
            checkIfAlreadyhavePermission()
        }
        try {

            cropView!!.of(Image_pATH)
                .withAspect(10, 10)
                .withOutputSize(500, 500)
                .initialize(this)
        } catch (e: Exception) {
        }

    }

    @RequiresApi(Build.VERSION_CODES.M)
    private fun checkIfAlreadyhavePermission():Boolean {
        checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)
        if (checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)
            != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this, arrayOf(
                    Manifest.permission.WRITE_EXTERNAL_STORAGE,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE
                ),
                1
            )
            return true
        }
        return false
    }

    private fun Instantiation() {
        cropView  =findViewById(R.id.cropView);
    }

    fun saveCroppedPhoto(view: View) {
        try {
            val croppedBitmap = cropView!!.output
            val intent = Intent()
            intent.putExtra("Croped", String.valueOf(getImageUri(croppedBitmap, this)))
            setResult(RESULT_OK, intent)
            finish()
        } catch (e: java.lang.Exception) {
        }
    }

    fun getImageUri(inImage: Bitmap, context: Context): Uri? {
        val bytes = ByteArrayOutputStream()
        inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes)
        val path = MediaStore.Images.Media.insertImage(
            context.getContentResolver(),
            inImage,
            System.currentTimeMillis().toString(),
            null
        )
        return Uri.parse(path)
    }

}