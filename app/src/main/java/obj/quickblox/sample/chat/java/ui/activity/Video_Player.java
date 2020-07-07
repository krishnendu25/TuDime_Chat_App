package obj.quickblox.sample.chat.java.ui.activity;

import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.view.WindowManager;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.VolleyError;

import org.json.JSONArray;
import org.json.JSONObject;

import butterknife.BindView;
import butterknife.ButterKnife;
import fm.jiecao.jcvideoplayer_lib.JCVideoPlayerStandard;
import obj.quickblox.sample.chat.java.R;

import static obj.quickblox.sample.chat.java.constants.AppConstants.PASS_URL;
import static obj.quickblox.sample.chat.java.constants.AppConstants.VIDEO_URL;

public class Video_Player extends BaseActivity {

    @BindView(R.id.videoplayer)
    JCVideoPlayerStandard videoplayer;
    String video_url="";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video__player);
        ButterKnife.bind(this);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        hideActionbar();
        try{
            video_url=getIntent().getStringExtra(VIDEO_URL);
        }catch (Exception e)
        {
            video_url="";
        }
        videoplayer.setUp(video_url, JCVideoPlayerStandard.SCREEN_LAYOUT_NORMAL, "PLAY");


    }
    @Override
    public void onBackPressed() {
        if (videoplayer.backPress()) {
            return;
        }
        super.onBackPressed();
    }
    @Override
    protected void onPause() {
        super.onPause();
        videoplayer.releaseAllVideos();
    }

    @Override
    public void ErrorResponse(VolleyError error, int requestCode, JSONObject networkresponse) {

    }

    @Override
    public void SuccessResponse(JSONObject response, int requestCode) {

    }

    @Override
    public void SuccessResponseArray(JSONArray response, int requestCode) {

    }

    @Override
    public void SuccessResponseRaw(String response, int requestCode) {

    }
}