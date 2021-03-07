package com.TuDime.ui.activity

import `in`.myinnos.awesomeimagepicker.activities.AlbumSelectActivity
import `in`.myinnos.awesomeimagepicker.helpers.ConstantsCustomGallery
import `in`.myinnos.awesomeimagepicker.models.Image
import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.text.Html
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import com.TuDime.NetworkOperation.IJSONParseListener
import com.TuDime.Prefrences.SharedPrefsHelper
import com.TuDime.R
import com.TuDime.Retrofit.RetrofitCallback
import com.TuDime.Retrofit.RetrofitClient
import com.TuDime.ui.Model.GetMyPhotoModel
import com.TuDime.ui.Model.GetMyPhotoModelFetch
import com.TuDime.ui.activity.New.ImageCropActivity
import com.TuDime.utils.ToastUtils
import com.android.volley.VolleyError
import com.squareup.picasso.MemoryPolicy
import com.squareup.picasso.Picasso
import com.squareup.picasso.Picasso.LoadedFrom
import com.squareup.picasso.Target
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.ResponseBody
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.IOException


class MyPhotoLibery : BaseActivity(), IJSONParseListener, View.OnClickListener {
    private var bitmap_profile_dp:Bitmap?=null
    var mContext: Context? = null
    var mActivity: Activity? = null
    var image1: ImageView? = null
    var image2: ImageView? = null
    var image3: ImageView? = null
    var image4: ImageView? = null
    var image5: ImageView? = null
    var image6: ImageView? = null
    var image7: ImageView? = null
    var image8: ImageView? = null
    var image9: ImageView? = null
    var image10: ImageView? = null
    var pickThePicture: LinearLayout? = null
    var photoList: ArrayList<GetMyPhotoModel>? = null
    var APIphotoList: ArrayList<GetMyPhotoModelFetch>? = null
    var retrofitCallback: RetrofitCallback? = null
    var totalImage = 0
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_my_photo_libery)
        iniView()

    }

    private fun iniView() {
        hideActionbar()
        mContext = applicationContext
        mActivity = this
        retrofitCallback = RetrofitClient.getRetrofitClient().create(RetrofitCallback::class.java)
        photoList = ArrayList<GetMyPhotoModel>()
        image1 = findViewById(R.id.image1)
        image1!!.setOnClickListener(this)
        image2 = findViewById(R.id.image2)
        image2!!.setOnClickListener(this)
        image3 = findViewById(R.id.image3)
        image3!!.setOnClickListener(this)
        image4 = findViewById(R.id.image4)
        image4!!.setOnClickListener(this)
        image5 = findViewById(R.id.image5)
        image5!!.setOnClickListener(this)
        image6 = findViewById(R.id.image6)
        image6!!.setOnClickListener(this)
        image7 = findViewById(R.id.image7)
        image7!!.setOnClickListener(this)
        image8 = findViewById(R.id.image8)
        image8!!.setOnClickListener(this)
        image9 = findViewById(R.id.image9)
        image9!!.setOnClickListener(this)
        image10 = findViewById(R.id.image10)
        image10!!.setOnClickListener(this)
        pickThePicture = findViewById(R.id.pickThePicture)
        pickThePicture!!.setOnClickListener(this)
        APIphotoList = ArrayList<GetMyPhotoModelFetch>()
        photoList = ArrayList<GetMyPhotoModel>()
    }

    override fun ErrorResponse(
        error: VolleyError?,
        requestCode: Int,
        networkresponse: JSONObject?
    ) {
        hideProgressDialog()
        ToastUtils.shortToast("API or Network related Error")
    }

    override fun SuccessResponse(response: JSONObject?, requestCode: Int) {
        hideProgressDialog()
        try {
            if (requestCode == 579) {
                APIphotoList!!.clear()
                hideProgressDialog()
                if (response?.getString("status").equals("success")) {
                    var imageid = response!!.getJSONArray("imageid")
                    var profileimage = response.getJSONArray("profileimage")
                    var baseUrl = response.getString("data")
                    try {
                        for (i in 0..profileimage.length() - 1) {
                            var obj: GetMyPhotoModelFetch = GetMyPhotoModelFetch()
                            obj.photoID = imageid.getString(i)
                            obj.photoUrl = baseUrl + profileimage.getString(i)
                            obj.photoName = profileimage.getString(i)
                            APIphotoList!!.add(obj)
                        }
                    } catch (e: Exception) {
                    }
                    setImageView(APIphotoList!!)
                } else {
                    APIphotoList!!.clear()
                    setImageView(APIphotoList!!)
                }
            } else if (requestCode == 579) {
                hideProgressDialog()
                if (response?.getString("status").equals("Success")) {
                    fetchAllthePhotos(SharedPrefsHelper.getInstance().userid)
                    ToastUtils.longToast("Image file delete success")
                }

            } else if (requestCode == 248) {
                hideProgressDialog()
                try {
                    if (response?.getString("status").equals("success", ignoreCase = true)) {
                        ToastUtils.shortToast("Picture Changed Successfully")
                        Fetch_Profile_Update(SharedPrefsHelper.getInstance().userid)
                    }
                } catch (e: JSONException) {
                    hideProgressDialog()
                }
            } else if (requestCode == 519) {
                hideProgressDialog()
                try {
                    if (response?.getString("status").equals("success", ignoreCase = true)) {
                        ToastUtils.shortToast("Image file delete success")
                        val intent = intent
                        overridePendingTransition(0, 0)
                        intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION)
                        finish()
                        overridePendingTransition(0, 0)
                        startActivity(intent)
                    }
                } catch (e: JSONException) {
                    hideProgressDialog()
                }
            }

        } catch (e: Exception) {
        }

    }

    private fun setImageView(apIphotoList: ArrayList<GetMyPhotoModelFetch>) {
        if (apIphotoList.size > 0) {
            for (i in 0..apIphotoList.size - 1) {

                doImageWork(i, apIphotoList.get(i).photoID!!, apIphotoList.get(i).photoUrl!!, true)
            }
        } else {
            for (i in 0..9) {
                totalImage--
                doImageWork(i, "", "", false)
            }
        }

    }

    override fun SuccessResponseArray(response: JSONArray?, requestCode: Int) {

    }

    override fun SuccessResponseRaw(SDS: String?, requestCode: Int) {
        var response: JSONObject = JSONObject(SDS)
        hideProgressDialog()
        try {
            if (requestCode == 579) {
                APIphotoList!!.clear()
                hideProgressDialog()
                if (response.getString("status").equals("success", ignoreCase = true)) {
                    var imageid = response.getJSONArray("imageid")
                    var profileimage = response.getJSONArray("profileimage")
                    var baseUrl = response.getString("data")
                    try {
                        for (i in 0..profileimage.length() - 1) {
                            var obj: GetMyPhotoModelFetch = GetMyPhotoModelFetch()
                            obj.photoID = imageid.getString(i)
                            obj.photoUrl = baseUrl + profileimage.getString(i)
                            obj.photoName = profileimage.getString(i)
                            APIphotoList!!.add(obj)
                        }
                    } catch (e: Exception) {
                    }
                    setImageView(APIphotoList!!)
                } else {
                    APIphotoList!!.clear()
                    setImageView(APIphotoList!!)
                }
            } else if (requestCode == 579) {
                hideProgressDialog()
                if (response.getString("status").equals("Success", ignoreCase = true)) {
                    fetchAllthePhotos(SharedPrefsHelper.getInstance().userid)
                    ToastUtils.longToast("Image file delete success")
                }

            } else if (requestCode == 248) {
                hideProgressDialog()
                try {
                    if (response.getString("status").equals("success", ignoreCase = true)) {
                        ToastUtils.shortToast("Picture Changed Successfully")
                        Fetch_Profile_Update(SharedPrefsHelper.getInstance().userid)
                    }
                } catch (e: JSONException) {
                    hideProgressDialog()
                }
            } else if (requestCode == 519) {
                hideProgressDialog()
                try {
                    if (response.getString("status").equals("success", ignoreCase = true)) {
                        ToastUtils.shortToast("Image file delete success")
                        val intent = intent
                        overridePendingTransition(0, 0)
                        intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION)
                        finish()
                        overridePendingTransition(0, 0)
                        startActivity(intent)
                    }
                } catch (e: JSONException) {
                    hideProgressDialog()
                }
            }


        } catch (e: Exception) {
        }


    }

    override fun onClick(v: View?) {
        when (v!!.id) {
            R.id.image1 -> {
                if (image1?.tag.toString().isNullOrBlank()) {
                    clickOnImages(0, image1?.tag.toString())
                } else {
                    val intent = Intent(this, AlbumSelectActivity::class.java)
                    intent.putExtra(ConstantsCustomGallery.INTENT_EXTRA_LIMIT, 1)
                    startActivityForResult(intent, ConstantsCustomGallery.REQUEST_CODE)
                }
            }
            R.id.image2 -> {
                if (image2?.tag.toString().isNullOrBlank()) {
                    clickOnImages(1, image2?.tag.toString())
                } else {
                    val intent = Intent(this, AlbumSelectActivity::class.java)
                    intent.putExtra(ConstantsCustomGallery.INTENT_EXTRA_LIMIT, 1)
                    startActivityForResult(intent, ConstantsCustomGallery.REQUEST_CODE)
                }
            }
            R.id.image3 -> {
                if (image3?.tag.toString().isNullOrBlank()) {
                    clickOnImages(2, image3?.tag.toString())
                } else {
                    val intent = Intent(this, AlbumSelectActivity::class.java)
                    intent.putExtra(ConstantsCustomGallery.INTENT_EXTRA_LIMIT, 1)
                    startActivityForResult(intent, ConstantsCustomGallery.REQUEST_CODE)
                }
            }
            R.id.image4 -> {
                if (image4?.tag.toString().isNullOrBlank()) {
                    clickOnImages(3, image4?.tag.toString())
                } else {
                    val intent = Intent(this, AlbumSelectActivity::class.java)
                    intent.putExtra(ConstantsCustomGallery.INTENT_EXTRA_LIMIT, 1)
                    startActivityForResult(intent, ConstantsCustomGallery.REQUEST_CODE)
                }
            }
            R.id.image5 -> {
                if (image5?.tag.toString().isNullOrBlank()) {
                    clickOnImages(4, image5?.tag.toString())
                } else {
                    val intent = Intent(this, AlbumSelectActivity::class.java)
                    intent.putExtra(ConstantsCustomGallery.INTENT_EXTRA_LIMIT, 1)
                    startActivityForResult(intent, ConstantsCustomGallery.REQUEST_CODE)
                }
            }
            R.id.image6 -> {
                if (image6?.tag.toString().isNullOrBlank()) {
                    clickOnImages(5, image6?.tag.toString())
                } else {
                    val intent = Intent(this, AlbumSelectActivity::class.java)
                    intent.putExtra(ConstantsCustomGallery.INTENT_EXTRA_LIMIT, 1)
                    startActivityForResult(intent, ConstantsCustomGallery.REQUEST_CODE)
                }
            }
            R.id.image7 -> {
                if (image7?.tag.toString().isNullOrBlank()) {
                    clickOnImages(6, image7?.tag.toString())
                } else {
                    val intent = Intent(this, AlbumSelectActivity::class.java)
                    intent.putExtra(ConstantsCustomGallery.INTENT_EXTRA_LIMIT, 1)
                    startActivityForResult(intent, ConstantsCustomGallery.REQUEST_CODE)
                }
            }
            R.id.image8 -> {
                if (image8?.tag.toString().isNullOrBlank()) {
                    clickOnImages(7, image8?.tag.toString())
                } else {
                    val intent = Intent(this, AlbumSelectActivity::class.java)
                    intent.putExtra(ConstantsCustomGallery.INTENT_EXTRA_LIMIT, 1)
                    startActivityForResult(intent, ConstantsCustomGallery.REQUEST_CODE)
                }
            }
            R.id.image9 -> {
                if (image9?.tag.toString().isNullOrBlank()) {
                    clickOnImages(8, image9?.tag.toString())
                } else {
                    val intent = Intent(this, AlbumSelectActivity::class.java)
                    intent.putExtra(ConstantsCustomGallery.INTENT_EXTRA_LIMIT, 1)
                    startActivityForResult(intent, ConstantsCustomGallery.REQUEST_CODE)
                }
            }
            R.id.image10 -> {
                if (image10?.tag.toString().isNullOrBlank()) {
                    clickOnImages(9, image10?.tag.toString())
                } else {
                    val intent = Intent(this, AlbumSelectActivity::class.java)
                    intent.putExtra(ConstantsCustomGallery.INTENT_EXTRA_LIMIT, 1)
                    startActivityForResult(intent, ConstantsCustomGallery.REQUEST_CODE)
                }
            }
            R.id.pickThePicture -> {
                try {
                    if (totalImage < 10) {
                        val intent = Intent(this, AlbumSelectActivity::class.java)
                        intent.putExtra(ConstantsCustomGallery.INTENT_EXTRA_LIMIT, 10 - totalImage)
                        startActivityForResult(intent, ConstantsCustomGallery.REQUEST_CODE)
                    } else {
                        ToastUtils.longToast("You Add Maximum 10 Pictures.")
                    }
                } catch (e: Exception) {
                }
            }
        }
    }

    fun setThisAsProfile(photoUrl: String?) {
        showProgressDialog(R.string.load)
        Picasso.get()
            .load(photoUrl)
            .into(object : Target {
                override fun onBitmapLoaded(bitmap: Bitmap, from: LoadedFrom) {
                    hitProfilePicApi(bitmap)
                    /*try {
                        hitQuickblox(Constant.SaveImagetoSDcard(Constant.GET_timeStamp(), bitmap, this@MyPhotoLibery))
                    } catch (e: Exception) {
                    }*/
                }

                override fun onBitmapFailed(e: java.lang.Exception, errorDrawable: Drawable) {}
                override fun onPrepareLoad(placeHolderDrawable: Drawable) {}
            })
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == RESULT_OK) {
            when (requestCode) {
                ConstantsCustomGallery.REQUEST_CODE -> {
                    if (data != null) {
                        photoList!!.clear()
                        val images: ArrayList<Image> =
                            data.getParcelableArrayListExtra(ConstantsCustomGallery.INTENT_EXTRA_IMAGES)
                        for (i in 0..images.size - 1) {
                            var obj: GetMyPhotoModel = GetMyPhotoModel()
                            try {
                                obj.pathName = images.get(i).path
                            } catch (e: Exception) {
                            }
                            try {
                                obj.photoName = images.get(i).name
                            } catch (e: Exception) {
                            }
                            try {
                                obj.bitmap = BitmapFactory.decodeFile(images.get(i).path)
                            } catch (e: Exception) {
                            }
                            photoList!!.add(obj)
                        }
                        var intent = Intent(this@MyPhotoLibery, ImageCropActivity::class.java)
                        intent.putExtra("Pathurl", getImageUri(photoList!!.get(0).bitmap!!,this@MyPhotoLibery).toString())
                        startActivityForResult(intent, 986)

                    }


                }
                986 -> {
                    val uri = data!!.getStringExtra("Croped")
                    try {
                        bitmap_profile_dp = MediaStore.Images.Media.getBitmap(this.contentResolver, Uri.parse(uri))
                        photoList!!.get(0).bitmap=bitmap_profile_dp
                        hitUploadMultiplePhoto(photoList)
                    } catch (e: IOException) {
                    }
                }
            }
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
    private fun hitUploadMultiplePhoto(photoList: ArrayList<GetMyPhotoModel>?) {
        showProgressDialog(R.string.dlg_loading)
        val builder = MultipartBody.Builder()
        builder.setType(MultipartBody.FORM)
        builder.addFormDataPart("UserID", SharedPrefsHelper.getInstance().userid)
        for (i in photoList!!.indices) {
            val file: File = File(photoList.get(i).pathName)
            builder.addFormDataPart(
                "profile_image[]",
                file.name,
                RequestBody.create(MediaType.parse("multipart/form-data"), file)
            )
        }
        val requestBody = builder.build()
        val call: Call<ResponseBody> = retrofitCallback!!.uploadMultiFile(requestBody)
        call.enqueue(object : Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                if (response.isSuccessful) { hideProgressDialog()
                    var jsonObject: JSONObject? = null
                    try {
                        var output = Html.fromHtml(response.body()!!.string()).toString()
                        output = output.substring(output.indexOf("{"), output.lastIndexOf("}") + 1)
                        jsonObject = JSONObject(output)
                        if (jsonObject.getString("status").equals("Success", ignoreCase = true)) {
                            hideProgressDialog()
                            ToastUtils.shortToast("Media Upload successfully")
                            try {
                                finish()
                                startActivity(intent)
                            } catch (e: Exception) {

                            }

                        } else { hideProgressDialog()
                            ToastUtils.shortToast("Media Upload Failed")
                        }
                    } catch (e: IOException) { hideProgressDialog()
                        ToastUtils.shortToast("Failed")
                    } catch (e: JSONException) { hideProgressDialog()
                        ToastUtils.shortToast("Failed")
                    }
                }
            }

            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                hideProgressDialog()
                ToastUtils.shortToast("Failed")
            }
        })
    }


    fun doImageWork(id: Int, photoID: String, url: String, setDelete: Boolean) {
        if (setDelete) {
            when (id) {
                1 -> {
                    totalImage++
                    if (image1?.tag == null) {
                        image1?.tag = photoID
                        try {
                            Picasso.get().load(url).memoryPolicy(MemoryPolicy.NO_CACHE)
                                .placeholder(R.drawable.progress_animation).into(image1)
                        } catch (e: Exception) {
                        }
                    }

                }
                2 -> {
                    totalImage++
                    if (image2?.tag == null) {
                        image2?.tag = photoID
                        try {
                            Picasso.get().load(url).memoryPolicy(MemoryPolicy.NO_CACHE)
                                .placeholder(R.drawable.progress_animation).into(image2)
                        } catch (e: Exception) {
                        }
                    }
                }
                3 -> {
                    totalImage++
                    if (image3?.tag == null) {
                        image3?.tag = photoID
                        try {
                            Picasso.get().load(url).into(image3)
                        } catch (e: Exception) {
                        }
                    }
                }
                4 -> {
                    totalImage++
                    if (image4?.tag == null) {
                        image4?.tag = photoID
                        try {
                            Picasso.get().load(url).memoryPolicy(MemoryPolicy.NO_CACHE)
                                .placeholder(R.drawable.progress_animation).into(image4)
                        } catch (e: Exception) {
                        }
                    }
                }
                5 -> {
                    totalImage++
                    if (image5?.tag == null) {
                        image5?.tag = photoID
                        try {
                            Picasso.get().load(url).memoryPolicy(MemoryPolicy.NO_CACHE)
                                .placeholder(R.drawable.progress_animation).into(image5)
                        } catch (e: Exception) {
                        }
                    }
                }
                6 -> {
                    totalImage++
                    if (image6?.tag == null) {
                        image6?.tag = photoID
                        try {
                            Picasso.get().load(url).memoryPolicy(MemoryPolicy.NO_CACHE)
                                .placeholder(R.drawable.progress_animation).into(image6)
                        } catch (e: Exception) {
                        }
                    }
                }
                7 -> {
                    totalImage++
                    if (image7?.tag == null) {
                        image7?.tag = photoID
                        try {
                            Picasso.get().load(url).memoryPolicy(MemoryPolicy.NO_CACHE)
                                .placeholder(R.drawable.progress_animation).into(image7)
                        } catch (e: Exception) {
                        }
                    }
                }
                8 -> {
                    totalImage++
                    if (image8?.tag == null) {
                        image8?.tag = photoID
                        try {
                            Picasso.get().load(url).memoryPolicy(MemoryPolicy.NO_CACHE)
                                .placeholder(R.drawable.progress_animation).into(image8)
                        } catch (e: Exception) {
                        }
                    }
                }
                9 -> {
                    totalImage++
                    if (image9?.tag == null) {
                        image9?.tag = photoID
                        try {
                            Picasso.get().load(url).memoryPolicy(MemoryPolicy.NO_CACHE)
                                .placeholder(R.drawable.progress_animation).into(image9)
                        } catch (e: Exception) {
                        }
                    }
                }
                10 -> {
                    totalImage++
                    if (image10?.tag == null) {
                        image10?.tag = photoID
                        try {
                            Picasso.get().load(url).memoryPolicy(MemoryPolicy.NO_CACHE)
                                .placeholder(R.drawable.progress_animation).into(image10)
                        } catch (e: Exception) {
                        }
                    }
                }

            }
        } else {
            when (id) {
                1 -> {
                    totalImage--
                    image1?.tag = null
                    image1?.setImageResource(0)
                }
                2 -> {
                    totalImage--
                    image2?.tag = null
                    image2?.setImageResource(0)
                }
                3 -> {
                    totalImage--
                    image3?.tag = null
                    image3?.setImageResource(0)
                }
                4 -> {
                    totalImage--
                    image4?.tag = null
                    image4?.setImageResource(0)
                }
                5 -> {
                    totalImage--
                    image5?.tag = null
                    image5?.setImageResource(0)
                }
                6 -> {
                    totalImage--
                    image6?.tag = null
                    image6?.setImageResource(0)
                }
                7 -> {
                    totalImage--
                    image7?.tag = null
                    image7?.setImageResource(0)
                }
                8 -> {
                    totalImage--
                    image8?.tag = null
                    image8?.setImageResource(0)
                }
                9 -> {
                    totalImage--
                    image9?.tag = null
                    image9?.setImageResource(0)
                }
                10 -> {
                    totalImage--
                    image10?.tag = null
                    image10?.setImageResource(0)
                }

            }
        }

    }

    fun clickOnImages(i: Int, picId: String?) {
        val builder1: AlertDialog.Builder = AlertDialog.Builder(this@MyPhotoLibery)
        builder1.setTitle("Select One Option")
        builder1.setCancelable(true)
        builder1.setPositiveButton(
            "Set As Profile Picture",
            DialogInterface.OnClickListener { dialog, id ->

                try {
                    try {
                        dialog.dismiss()
                        setThisAsProfile(APIphotoList?.get(i)?.photoUrl)
                    } catch (e: Exception) {
                    }

                } catch (e: Exception) {
                }
            })

        builder1.setNegativeButton(
            "Delete",
            DialogInterface.OnClickListener { dialog, id ->

                try {
                    try {
                        try {

                            doImageWork(i, "", "", false)
                            dialog.dismiss()
                        } catch (e: Exception) {

                        }
                        hitDeleteApi(SharedPrefsHelper.getInstance().userid, picId)
                    } catch (e: Exception) {
                    }
                } catch (e: Exception) {
                }
            })

        val alert11: AlertDialog = builder1.create()
        alert11.show()
    }


    override fun onResume() {
        super.onResume()
        fetchAllthePhotos(SharedPrefsHelper.getInstance().userid)
    }

}