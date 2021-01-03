package obj.quickblox.sample.chat.java.ui.activity

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
import android.os.Bundle
import android.text.Html
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import com.android.volley.VolleyError
import com.squareup.picasso.MemoryPolicy
import com.squareup.picasso.Picasso
import com.squareup.picasso.Picasso.LoadedFrom
import com.squareup.picasso.Target
import obj.quickblox.sample.chat.java.NetworkOperation.IJSONParseListener
import obj.quickblox.sample.chat.java.R
import obj.quickblox.sample.chat.java.Retrofit.RetrofitCallback
import obj.quickblox.sample.chat.java.Retrofit.RetrofitClient
import obj.quickblox.sample.chat.java.ui.Model.GetMyPhotoModel
import obj.quickblox.sample.chat.java.ui.Model.GetMyPhotoModelFetch
import obj.quickblox.sample.chat.java.utils.SharedPrefsHelper
import obj.quickblox.sample.chat.java.utils.ToastUtils
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
import java.io.File
import java.io.IOException


class MyPhotoLibery : BaseActivity(), IJSONParseListener, View.OnClickListener {
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
    var totalImage=0
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
        APIphotoList= ArrayList<GetMyPhotoModelFetch>()
        photoList=ArrayList<GetMyPhotoModel>()
    }

    override fun ErrorResponse(error: VolleyError?, requestCode: Int, networkresponse: JSONObject?) {
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
                    var profileimage= response.getJSONArray("profileimage")
                    var baseUrl=response.getString("data")
                    try {
                        for (i in 0..profileimage.length()-1){
                            var obj:GetMyPhotoModelFetch=GetMyPhotoModelFetch()
                            obj.photoID=imageid.getString(i)
                            obj.photoUrl=baseUrl+profileimage.getString(i)
                            obj.photoName=profileimage.getString(i)
                            APIphotoList!!.add(obj)
                        }
                    } catch (e: Exception) {
                    }
                    setImageView(APIphotoList!!)
                }else{
                    APIphotoList!!.clear()
                    setImageView(APIphotoList!!)
                }
            } else  if (requestCode == 579) {
                hideProgressDialog()
                if (response?.getString("status").equals("Success")) {
                    fetchAllthePhotos(SharedPrefsHelper.getInstance().userid)
                    ToastUtils.longToast("Image file delete success")
                }

            }else if (requestCode == 248) {
                hideProgressDialog()
                try {
                    if (response?.getString("status").equals("success", ignoreCase = true)) {
                        ToastUtils.shortToast("Picture Changed Successfully")
                        Fetch_Profile_Update(SharedPrefsHelper.getInstance().userid)
                    }
                } catch (e: JSONException) {
                    hideProgressDialog()
                }
            }else if (requestCode == 519) {
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
        if (apIphotoList.size>0){
            for (i in 0..apIphotoList.size-1){

                doImageWork(i, apIphotoList.get(i).photoID!!, apIphotoList.get(i).photoUrl!!, true)
            }
        }else{
            for (i in 0..9){
                totalImage--
                doImageWork(i, "", "", false)
            }
        }

    }

    override fun SuccessResponseArray(response: JSONArray?, requestCode: Int) {

    }

    override fun SuccessResponseRaw(SDS: String?, requestCode: Int) {
        var response: JSONObject= JSONObject(SDS)
        hideProgressDialog()
        try {
            if (requestCode == 579) {
                APIphotoList!!.clear()
                hideProgressDialog()
                if (response?.getString("status").equals("success", ignoreCase = true)) {
                    var imageid = response!!.getJSONArray("imageid")
                    var profileimage= response.getJSONArray("profileimage")
                    var baseUrl=response.getString("data")
                    try {
                        for (i in 0..profileimage.length()-1){
                            var obj:GetMyPhotoModelFetch=GetMyPhotoModelFetch()
                            obj.photoID=imageid.getString(i)
                            obj.photoUrl=baseUrl+profileimage.getString(i)
                            obj.photoName=profileimage.getString(i)
                            APIphotoList!!.add(obj)
                        }
                    } catch (e: Exception) {
                    }
                    setImageView(APIphotoList!!)
                }else{
                    APIphotoList!!.clear()
                    setImageView(APIphotoList!!)
                }
            } else  if (requestCode == 579) {
                hideProgressDialog()
                if (response?.getString("status").equals("Success", ignoreCase = true)) {
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
            }else if (requestCode == 519) {
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
                clickOnImages(0, image1?.getTag().toString())
            }
            R.id.image2 -> {
                clickOnImages(1, image2?.getTag().toString())
            }
            R.id.image3 -> {
                clickOnImages(2, image3?.getTag().toString())
            }
            R.id.image4 -> {
                clickOnImages(3, image4?.getTag().toString())
            }
            R.id.image5 -> {
                clickOnImages(4, image5?.getTag().toString())
            }
            R.id.image6 -> {
                clickOnImages(5, image6?.getTag().toString())
            }
            R.id.image7 -> {
                clickOnImages(6, image7?.getTag().toString())
            }
            R.id.image8 -> {
                clickOnImages(7, image8?.getTag().toString())
            }
            R.id.image9 -> {
                clickOnImages(8, image9?.getTag().toString())
            }
            R.id.image10 -> {
                clickOnImages(9, image10?.getTag().toString())
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
    /*private fun hitQuickblox(s: String) {
     *//*   val file = File(s)
        QBContent.uploadFileTask(file, true, null) { }.performAsync(object : QBEntityCallback<QBFile> {
            override fun onSuccess(qbFile: QBFile, bundle: Bundle) {
                val uploadedFileID = qbFile.id
                val user = sharedPrefsHelper.qbUser
                user.id = SharedPrefsHelper.getInstance().userId.toInt()
                user.fileId = uploadedFileID
                QBUsers.updateUser(user).performAsync(object : QBEntityCallback<QBUser?> {
                    override fun onSuccess(qbUser: QBUser?, bundle: Bundle) {
                        // SharedPrefsHelper.getInstance().saveQbUser(qbUser);
                    }
                    override fun onError(e: QBResponseException) {}
                })
            }
            override fun onError(e: QBResponseException) {}
        })*//*
    }*/

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == RESULT_OK) {
            when (requestCode) {
                ConstantsCustomGallery.REQUEST_CODE -> {
                    if (data != null) {
                        photoList!!.clear()
                        val images: ArrayList<Image> = data.getParcelableArrayListExtra(ConstantsCustomGallery.INTENT_EXTRA_IMAGES)
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
                        hitUploadMultiplePhoto(photoList)
                    }
                }
            }
        }
    }

    private fun hitUploadMultiplePhoto(photoList: ArrayList<GetMyPhotoModel>?) {
        showProgressDialog(R.string.dlg_loading)
        val builder = MultipartBody.Builder()
        builder.setType(MultipartBody.FORM)
        builder.addFormDataPart("UserID", SharedPrefsHelper.getInstance().getUSERID())
        for (i in photoList!!.indices) {
            val file: File = File(photoList.get(i).pathName)
            builder.addFormDataPart("profile_image[]", file.name, RequestBody.create(MediaType.parse("multipart/form-data"), file))
        }
        val requestBody = builder.build()
        val call: Call<ResponseBody> = retrofitCallback!!.uploadMultiFile(requestBody)
        call.enqueue(object : Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                hideProgressDialog()
                if (response.isSuccessful) {
                    var jsonObject: JSONObject? = null
                    try {
                        var output = Html.fromHtml(response.body()!!.string()).toString()
                        output = output.substring(output.indexOf("{"), output.lastIndexOf("}") + 1)
                        jsonObject = JSONObject(output)
                        if (jsonObject.getString("status").equals("Success", ignoreCase = true)) {
                            ToastUtils.shortToast("Media Upload successfully")
                        } else {
                            ToastUtils.shortToast("Media Upload Failed")
                        }
                    } catch (e: IOException) {
                        ToastUtils.shortToast("Failed")
                    } catch (e: JSONException) {
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
        if (setDelete){
            when (id) {
                1 -> {
                    totalImage++
                    if (image1?.getTag() == null) {
                        image1?.setTag(photoID)
                        try {
                            Picasso.get().load(url).memoryPolicy(MemoryPolicy.NO_CACHE).into(image1)
                        } catch (e: Exception) {
                        }
                    }

                }
                2 -> {
                    totalImage++
                    if (image2?.getTag() == null) {
                        image2?.setTag(photoID)
                        try {
                            Picasso.get().load(url).memoryPolicy(MemoryPolicy.NO_CACHE).into(image2)
                        } catch (e: Exception) {
                        }
                    }
                }
                3 -> {
                    totalImage++
                    if (image3?.getTag() == null) {
                        image3?.setTag(photoID)
                        try {
                            Picasso.get().load(url).into(image3)
                        } catch (e: Exception) {
                        }
                    }
                }
                4 -> {
                    totalImage++
                    if (image4?.getTag() == null) {
                        image4?.setTag(photoID)
                        try {
                            Picasso.get().load(url).memoryPolicy(MemoryPolicy.NO_CACHE).into(image4)
                        } catch (e: Exception) {
                        }
                    }
                }
                5 -> {
                    totalImage++
                    if (image5?.getTag() == null) {
                        image5?.setTag(photoID)
                        try {
                            Picasso.get().load(url).memoryPolicy(MemoryPolicy.NO_CACHE).into(image5)
                        } catch (e: Exception) {
                        }
                    }
                }
                6 -> {
                    totalImage++
                    if (image6?.getTag() == null) {
                        image6?.setTag(photoID)
                        try {
                            Picasso.get().load(url).memoryPolicy(MemoryPolicy.NO_CACHE).into(image6)
                        } catch (e: Exception) {
                        }
                    }
                }
                7 -> {
                    totalImage++
                    if (image7?.getTag() == null) {
                        image7?.setTag(photoID)
                        try {
                            Picasso.get().load(url).memoryPolicy(MemoryPolicy.NO_CACHE).into(image7)
                        } catch (e: Exception) {
                        }
                    }
                }
                8 -> {
                    totalImage++
                    if (image8?.getTag() == null) {
                        image8?.setTag(photoID)
                        try {
                            Picasso.get().load(url).memoryPolicy(MemoryPolicy.NO_CACHE).into(image8)
                        } catch (e: Exception) {
                        }
                    }
                }
                9 -> {
                    totalImage++
                    if (image9?.getTag() == null) {
                        image9?.setTag(photoID)
                        try {
                            Picasso.get().load(url).memoryPolicy(MemoryPolicy.NO_CACHE).into(image9)
                        } catch (e: Exception) {
                        }
                    }
                }
                10 -> {
                    totalImage++
                    if (image10?.getTag() == null) {
                        image10?.setTag(photoID)
                        try {
                            Picasso.get().load(url).memoryPolicy(MemoryPolicy.NO_CACHE).into(image10)
                        } catch (e: Exception) {
                        }
                    }
                }

            }
        }else{
            when (id) {
                1 -> {
                    totalImage--
                    image1?.setTag(null)
                    image1?.setImageResource(0);
                }
                2 -> {
                    totalImage--
                    image2?.setTag(null)
                    image2?.setImageResource(0);
                }
                3 -> {
                    totalImage--
                    image3?.setTag(null)
                    image3?.setImageResource(0);
                }
                4 -> {
                    totalImage--
                    image4?.setTag(null)
                    image4?.setImageResource(0);
                }
                5 -> {
                    totalImage--
                    image5?.setTag(null)
                    image5?.setImageResource(0);
                }
                6 -> {
                    totalImage--
                    image6?.setTag(null)
                    image6?.setImageResource(0);
                }
                7 -> {
                    totalImage--
                    image7?.setTag(null)
                    image7?.setImageResource(0);
                }
                8 -> {
                    totalImage--
                    image8?.setTag(null)
                    image8?.setImageResource(0);
                }
                9 -> {
                    totalImage--
                    image9?.setTag(null)
                    image9?.setImageResource(0);
                }
                10 -> {
                    totalImage--
                    image10?.setTag(null)
                    image10?.setImageResource(0);
                }

            }
        }

    }

    fun clickOnImages(i: Int, picId: String?){
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
        fetchAllthePhotos(SharedPrefsHelper.getInstance().getUSERID())
    }

}