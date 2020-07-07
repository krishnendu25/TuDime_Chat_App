package obj.quickblox.sample.chat.java.utils;


import android.annotation.SuppressLint;
import android.graphics.drawable.ColorDrawable;
import android.media.MediaRecorder;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import org.jivesoftware.smackx.chatstates.ChatState;

import java.io.File;
import java.net.URI;

import obj.quickblox.sample.chat.java.Prefrences.CiaoPrefrences;
import obj.quickblox.sample.chat.java.R;
import obj.quickblox.sample.chat.java.ui.Callback.OnAudioRecordedListener;

public class VoiceMemoDialog extends DialogFragment implements View.OnClickListener {

    private static final String TAG = VoiceMemoDialog.class.getSimpleName();
    private TextView txvTimer;
    private File audioFile;
    private MediaRecorder mRecorder;
    private ImageView imgRecorder;
    private OnAudioRecordedListener onAudioRecordedListener;
    private CountDownTimer countDownTimer;
    protected boolean mIsAudioRecorded;
    protected int timer;
    private static final long ONE_SECOND = 1000;
    private static final long ONE_MINUTE = 60 * ONE_SECOND;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // remove title and frame from dialog-fragment
        setStyle(STYLE_NO_TITLE, 0);
    }

    @Override
    public void onActivityCreated(Bundle arg0) {
        super.onActivityCreated(arg0);
        getDialog().getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;
    }

    @Override
    @Nullable
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dialog_chat_voice_memo, null);
        getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(0));
        getDialog().setCanceledOnTouchOutside(false);
        txvTimer = (TextView) view.findViewById(R.id.txvTimer);
        imgRecorder = (ImageView) view.findViewById(R.id.imgRecorder);
        imgRecorder.setOnTouchListener(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                if (mIsAudioRecorded) {
                    return false;
                }
                boolean isTouchConsumed = false;
                switch (motionEvent.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        startRecording();
                        isTouchConsumed = true;
                        break;
                    case MotionEvent.ACTION_UP:
                        stopRecording();
                        isTouchConsumed = true;
                        break;
                }
                return isTouchConsumed;
            }
        });

        view.findViewById(R.id.txvDone).setOnClickListener(this);
        view.findViewById(R.id.txvCancel).setOnClickListener(this);
        return view;
    }

    protected void startTimer() {
        countDownTimer = new CountDownTimer(ONE_MINUTE, ONE_SECOND) {

            public void onTick(long millisUntilFinished) {
                // mIsRecordingStarted = true;
                timer = (int) ((ONE_MINUTE - millisUntilFinished) / 1000);
                String displayTime = "";
                if (timer == 60) {
                    displayTime = "01:00";
                } else if (timer < 10) {
                    displayTime = "00:0" + timer;
                } else {
                    displayTime = "00:" + timer;
                }
                txvTimer.setText(displayTime);
            }

            public void onFinish() {
                txvTimer.setText("01:00");
                if (countDownTimer != null) {
                    countDownTimer.cancel();
                }
            }
        }.start();
    }

    @SuppressLint("NewApi")
    private void startRecording() {
        try {
           /* if(!CiaoPrefrences.getInstance(getActivity()).getVoiceMsg()) {
                ((ChatWindowActivity) getActivity()).updateChatState(ChatState.recording);
            }
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                imgRecorder.setBackground(getResources().getDrawable(R.drawable.bg_audio_selected));
            } else {
                imgRecorder.setBackgroundDrawable(getResources().getDrawable(R.drawable.bg_audio_selected));
            }*/
            startTimer();

            imgRecorder.setImageDrawable(getResources().getDrawable(R.drawable.microphone_white));
            String filePath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/" + System.currentTimeMillis() + ".3gp";
            audioFile = new File(new URI("file://" + filePath.replace(" ", "%20")));
            mRecorder = new MediaRecorder();
            mRecorder.setAudioSource(MediaRecorder.AudioSource.VOICE_RECOGNITION);
            mRecorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);
            mRecorder.setOutputFile(filePath);
            mRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AAC);
//			mRecorder.setAudioEncodingBitRate(16);
//			mRecorder.setAudioSamplingRate(44100);
            mRecorder.prepare();
            mRecorder.start();
        } catch (Exception e) {
            Log.e(TAG, "prepare() failed" + e.getMessage());
        }
    }

    @SuppressLint("NewApi")
    private void stopRecording() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            imgRecorder.setBackground(getResources().getDrawable(R.drawable.bg_audio_normal));
        } else {
            imgRecorder.setBackgroundDrawable(getResources().getDrawable(R.drawable.bg_audio_normal));
        }
        if (timer > 0) {
            imgRecorder.setImageDrawable(getResources().getDrawable(R.drawable.audio_recorded));
            mRecorder.stop();
            mIsAudioRecorded = true;
        } else {
            txvTimer.setText(getString(R.string.hold_amp_talk));
            mIsAudioRecorded = false;
            imgRecorder.setImageDrawable(getResources().getDrawable(R.drawable.microphone_blue));
            ToastUtils.longToast(R.string.audio_could_not_be_recorded);
        }
        mRecorder.reset();
        mRecorder.release();
        mRecorder = null;

        if (countDownTimer != null) {
            countDownTimer.cancel();
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.txvDone:
                if (!mIsAudioRecorded) {
                    ToastUtils.shortToast(getActivity().getString(R.string.record_voice_memo_to_send));
                    return;
                }
                onAudioRecordedListener.onAudioRecorded(audioFile);
                dismiss();
                break;
            case R.id.txvCancel:
                dismiss();
                break;
            default:
                break;
        }
    }

    public void setOnAudioRecordedListener(OnAudioRecordedListener onAudioRecordedListener) {
        this.onAudioRecordedListener = onAudioRecordedListener;
    }
}
