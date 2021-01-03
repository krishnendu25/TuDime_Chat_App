package obj.quickblox.sample.chat.java.ui.activity.New


import android.app.DownloadManager
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.MediaController
import android.widget.ProgressBar
import android.widget.RelativeLayout
import android.widget.VideoView
import com.android.volley.VolleyError
import obj.quickblox.sample.chat.java.R
import obj.quickblox.sample.chat.java.ui.activity.BaseActivity
import org.json.JSONArray
import org.json.JSONObject
import java.io.File


class AttachmentVideoActivity : BaseActivity() {
    private var rootLayout: RelativeLayout? = null
    private var videoView: VideoView? = null
    private var progressBar: ProgressBar? = null
    private var mediaController: MediaController? = null
    private val file: File? = null
    protected override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_attachment_video)
        initUI()
        loadVideo()
    }

    private fun initUI() {
        if (getSupportActionBar() != null) {
            getSupportActionBar()!!.setDisplayHomeAsUpEnabled(true)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                getSupportActionBar()!!.setBackgroundDrawable(getDrawable(R.drawable.toolbar_video_player_background))
            }
            getSupportActionBar()!!.setTitle(getIntent().getStringExtra(EXTRA_FILE_NAME))
            getSupportActionBar()!!.setElevation(0f)
        }
        rootLayout = findViewById(R.id.layout_root)
        videoView = findViewById(R.id.vv_full_view)
        progressBar = findViewById(R.id.progress_show_video)
        rootLayout!!.setOnClickListener { mediaController!!.show(2000) }
    }

    private fun loadVideo() {
        progressBar!!.visibility = View.VISIBLE
        val filename: String = getIntent().getStringExtra(EXTRA_FILE_NAME)
        val file: File = File(getApplication().getFilesDir(), filename)
        if (file != null) {
            mediaController = MediaController(this)
            mediaController!!.setAnchorView(videoView)
            videoView!!.setMediaController(mediaController)
            videoView!!.setVideoPath(file.path)
            videoView!!.start()
        }
        videoView!!.setOnPreparedListener {
            progressBar!!.visibility = View.GONE
            mediaController!!.show(2000)
        }
        videoView!!.setOnErrorListener { mp, what, extra ->
            progressBar!!.visibility = View.GONE
            mediaController!!.hide()
            showErrorSnackbar(R.string.error, null, View.OnClickListener { loadVideo() })
            true
        }
    }

   override fun onBackPressed() {
        finish()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        getMenuInflater().inflate(R.menu.menu_activity_video_player, menu)
        return true
    }

    override  fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.menu_player_save -> {
                saveFileToGallery()
                true
            }
            R.id.home -> {
                onBackPressed()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun ErrorResponse(error: VolleyError?, requestCode: Int, networkresponse: JSONObject?) {
        TODO("Not yet implemented")
    }

    override fun SuccessResponse(response: JSONObject?, requestCode: Int) {
        TODO("Not yet implemented")
    }

    override fun SuccessResponseArray(response: JSONArray?, requestCode: Int) {
        TODO("Not yet implemented")
    }

    override fun SuccessResponseRaw(response: String?, requestCode: Int) {
        TODO("Not yet implemented")
    }

    private fun saveFileToGallery() {
        if (file != null) {
            try {
                val url: String = getIntent().getStringExtra(EXTRA_FILE_URL)
                val request = DownloadManager.Request(Uri.parse(url))
                request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, file.name)
                request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
                request.allowScanningByMediaScanner()
                val manager = getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager?
                manager?.enqueue(request)
            } catch (e: SecurityException) {
                Log.d("Security Exception", e.message)
            }
        }
    }

    companion object {
        private const val EXTRA_FILE_NAME = "video_file_name"
        private const val EXTRA_FILE_URL = "video_file_URL"
        fun start(context: Context, attachmentName: String?, url: String?) {
            val intent = Intent(context, AttachmentVideoActivity::class.java)
            intent.putExtra(EXTRA_FILE_URL, url)
            intent.putExtra(EXTRA_FILE_NAME, attachmentName)
            context.startActivity(intent)
        }
    }
}