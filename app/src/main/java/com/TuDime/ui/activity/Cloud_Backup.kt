package com.TuDime.ui.activity

import android.app.Activity
import android.app.DownloadManager
import android.content.Context
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.os.Handler
import android.text.Html
import android.util.Log
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import com.TuDime.Prefrences.SharedPrefsHelper
import com.TuDime.R
import com.TuDime.Retrofit.RetrofitCallback
import com.TuDime.Retrofit.RetrofitClient
import com.TuDime.db.DbHelper
import com.TuDime.ui.Model.ChatBackupModel
import com.TuDime.utils.ToastUtils
import com.ajts.androidmads.library.ExcelToSQLite
import com.ajts.androidmads.library.ExcelToSQLite.ImportListener
import com.ajts.androidmads.library.SQLiteToExcel
import com.ajts.androidmads.library.SQLiteToExcel.ExportListener
import com.android.volley.VolleyError
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


class Cloud_Backup : BaseActivity() {
    private var mActivity: Activity? = null
    private var mContext: Context? = null
    var databaseExel: String? = null
    var retrofitCallback: RetrofitCallback? = null
    var chatBackUpList = ArrayList<ChatBackupModel>()
    var timestampTV: TextView? = null
    var filenameTV: TextView? = null
    var restoreView: LinearLayout? = null
    var directory_path = Environment.getExternalStorageDirectory().path + "/Backup/"
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_cloud__backup)
        hideActionbar()
        iniView()


    }

    private fun getMyBackUp() {
        showProgressDialog(R.string.dlg_loading)
        val builder = MultipartBody.Builder()
        builder.setType(MultipartBody.FORM)
        builder.addFormDataPart("UserID", SharedPrefsHelper.getInstance().userid)
        builder.addFormDataPart("device type", "android")
        builder.addFormDataPart("task", "get_chat_backup")
        val requestBody = builder.build()
        val call: Call<ResponseBody> = retrofitCallback!!.chatBackup(requestBody)
        call.enqueue(object : Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                hideProgressDialog()
                if (response.isSuccessful) {
                    hideProgressDialog()
                    var jsonObject: JSONObject? = null
                    try {
                        var output = Html.fromHtml(response.body()!!.string()).toString()
                        output = output.substring(output.indexOf("{"), output.lastIndexOf("}") + 1)
                        jsonObject = JSONObject(output)
                        if (jsonObject.getString("status").equals("Success", true)) {
                            try {
                                chatBackUpList.clear()
                                var chatBackupModel = ChatBackupModel()
                                var dataJson = jsonObject.getJSONObject("data")
                                chatBackupModel.file_link = dataJson.getString("file_link")
                                var custom_data = JSONObject(dataJson.getString("custom_data"))
                                chatBackupModel.device_type = custom_data.getString("device_type")
                                chatBackupModel.upload_time = custom_data.getString("Backup_TimeStamp")
                                chatBackUpList.add(chatBackupModel)
                                restoreView!!.visibility = View.VISIBLE
                                timestampTV!!.text =
                                    "Last Backup Time: " + chatBackUpList.get(0).upload_time
                                filenameTV!!.text = "File Url: " + chatBackUpList.get(0).file_link
                            } catch (e: Exception) {
                            }

                        }
                    } catch (e: IOException) {
                        hideProgressDialog()
                        ToastUtils.shortToast("Failed")
                    } catch (e: JSONException) {
                        hideProgressDialog()
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

    override fun ErrorResponse(
        error: VolleyError?,
        requestCode: Int,
        networkresponse: JSONObject?
    ) {
        hideProgressDialog()
        ToastUtils.shortToast(error!!.message)
    }

    override fun SuccessResponse(response: JSONObject?, requestCode: Int) {
        if (requestCode == 826) {
            hideProgressDialog()
            if (response!!.getString("status").equals("Success", true)) {
                ToastUtils.shortToast(response.getString("success_message"))
            } else {
                ToastUtils.shortToast("Chat Backup failed")
            }
        }
    }

    override fun SuccessResponseArray(response: JSONArray?, requestCode: Int) {
        hideProgressDialog()
    }

    override fun SuccessResponseRaw(response: String?, requestCode: Int) {
        hideProgressDialog()
    }

    private fun iniView() {
        mActivity = this@Cloud_Backup
        mContext = applicationContext
        retrofitCallback = RetrofitClient.getRetrofitClient().create(RetrofitCallback::class.java)
        timestampTV =  findViewById(R.id.timestampTV);
        filenameTV =  findViewById(R.id.filenameTV);
        restoreView = findViewById(R.id.restoreView);
    }

    fun exportLocalSqliteDB(view: View) {

        var sqliteToExcel = SQLiteToExcel(this, DbHelper.DB_NAME)



        sqliteToExcel = SQLiteToExcel(applicationContext, DbHelper.DB_NAME, directory_path)
        sqliteToExcel.exportAllTables("TuDimeLocal.xls", object : ExportListener {
            override fun onStart() {}
            override fun onCompleted(filePath: String) {
                hitUploadChatBackUp(filePath)
            }

            override fun onError(e: java.lang.Exception) {
                ToastUtils.shortToast(e.message)
            }
        })


    }

    private fun hitUploadChatBackUp(filePath: String) {
        showProgressDialog(R.string.dlg_loading)
        val builder = MultipartBody.Builder()
        builder.setType(MultipartBody.FORM)
        builder.addFormDataPart("UserID", SharedPrefsHelper.getInstance().userid)
        builder.addFormDataPart("Backup_TimeStamp", System.currentTimeMillis().toString())
        builder.addFormDataPart("device type", "android")
        builder.addFormDataPart("task", "chat_backup")
        val file: File = File(filePath)
        builder.addFormDataPart(
            "fileZip",
            file.name,
            RequestBody.create(MediaType.parse("multipart/form-data"), file)
        )
        val requestBody = builder.build()
        val call: Call<ResponseBody> = retrofitCallback!!.chatBackup(requestBody)
        call.enqueue(object : Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                hideProgressDialog()
                if (response.isSuccessful) {
                    hideProgressDialog()
                    var jsonObject: JSONObject? = null
                    try {
                        var output = Html.fromHtml(response.body()!!.string()).toString()
                        output = output.substring(output.indexOf("{"), output.lastIndexOf("}") + 1)
                        jsonObject = JSONObject(output)
                        if (jsonObject.getString("status").equals("Success", true)) {
                            ToastUtils.shortToast(jsonObject.getString("success_message"))
                            getMyBackUp()
                        } else {
                            ToastUtils.shortToast("Chat Backup failed")
                        }
                    } catch (e: IOException) {
                        hideProgressDialog()
                        ToastUtils.shortToast("Failed")
                    } catch (e: JSONException) {
                        hideProgressDialog()
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

    override fun onResume() {
        super.onResume()
        getMyBackUp()
    }

    fun restoreChatFile(view: View) {
        if (chatBackUpList.size>0){
            showProgressDialog(R.string.dlg_loading)
            downloadFile(chatBackUpList.get(0).file_link!!)


            Handler().postDelayed({
                try {
                    val excelToSQLite = ExcelToSQLite(applicationContext, DbHelper.DB_NAME,true)
                    excelToSQLite.importFromFile(directory_path + "TuDimeLocal.xls", object : ImportListener {
                        override fun onStart() { showProgressDialog(R.string.dlg_loading)}
                        override fun onCompleted(dbName: String) {
                            hideProgressDialog()
                            ToastUtils.shortToast("Chat Restore successfull")
                        }
                        override fun onError(e: Exception) {
                            hideProgressDialog()
                            ToastUtils.shortToast("Chat Restore failed")}

                    })
                } catch (e: Exception) {
                    Log.e("@@@",e.message)
                }
            }, 10000)





        }
    }
    fun downloadFile(uRl: String) {
        val direct = File(directory_path)

        if (!direct.exists()) {
            direct.mkdirs()
        }

        val mgr = getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager

        val downloadUri = Uri.parse(uRl)
        val request = DownloadManager.Request(
            downloadUri
        )

        request.setAllowedNetworkTypes(
            DownloadManager.Request.NETWORK_WIFI or
                    DownloadManager.Request.NETWORK_MOBILE
        )
            .setAllowedOverRoaming(false).setTitle("TuDimeLocal.xls") //Download Manager Title
            .setDescription("Downloading...")
            .setDestinationInExternalPublicDir(
                Environment.getExternalStorageDirectory().path,
                "/Backup/"  //Your User define(Non Standard Directory name)/File Name
            )
        mgr.enqueue(request)

    }
    fun back(view: View) {finish()}
}