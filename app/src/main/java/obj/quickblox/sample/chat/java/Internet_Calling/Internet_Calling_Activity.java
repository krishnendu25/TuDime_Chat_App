package obj.quickblox.sample.chat.java.Internet_Calling;

import android.Manifest;
import android.app.AlertDialog;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.ColorStateList;
import android.media.AudioManager;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Chronometer;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.android.volley.Request;
import com.android.volley.VolleyError;
import com.github.clans.fab.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.twilio.audioswitch.selection.AudioDeviceSelector;
import com.twilio.voice.Call;
import com.twilio.voice.CallException;
import com.twilio.voice.CallInvite;
import com.twilio.voice.ConnectOptions;
import com.twilio.voice.RegistrationException;
import com.twilio.voice.RegistrationListener;
import com.twilio.voice.Voice;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Locale;
import java.util.Set;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.internal.Constants;
import obj.quickblox.sample.chat.java.NetworkOperation.JSONRequestResponse;
import obj.quickblox.sample.chat.java.NetworkOperation.MyVolley;
import obj.quickblox.sample.chat.java.R;
import obj.quickblox.sample.chat.java.ui.activity.BaseActivity;

import static android.Manifest.permission.RECORD_AUDIO;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;
import static obj.quickblox.sample.chat.java.constants.ApiConstants.accessToken_url;
import static obj.quickblox.sample.chat.java.constants.ApiConstants.get_user_profile_qb_reference;

public class Internet_Calling_Activity extends BaseActivity {
    private static final String TAG = "VoiceActivity";
    private static final String identity = "alice";
    private static final String TWILIO_ACCESS_TOKEN_SERVER_URL = "TWILIO_ACCESS_TOKEN_SERVER_URL";
    private static final int MIC_PERMISSION_REQUEST_CODE = 1;
    @BindView(R.id.txvCallernumber)
    TextView txvCallernumber;
    @BindView(R.id.connectionStatus)
    TextView connectionStatus;
    @BindView(R.id.txvRejectCall)
    TextView txvRejectCall;
    @BindView(R.id.linear_buttons)
    RelativeLayout linearButtons;
    @BindView(R.id.dynamicToggleVideoCall)
    ImageView dynamicToggleVideoCall;
    @BindView(R.id.dynamicToggleMuteCall)
    ImageView dynamicToggleMuteCall;
    @BindView(R.id.linear_mute)
    LinearLayout linearMute;
    @BindView(R.id.actionVideoButtons)
    LinearLayout actionVideoButtons;
    @BindView(R.id.timer_call)
    TextView timerCall;
    @BindView(R.id.imageView3)
    ImageView imageView3;
    @BindView(R.id.chrnTimer)
    Chronometer chrnTimer;
    PaymentTable table;
    // Empty HashMap, never populated for the Quickstart
    HashMap<String, String> params = new HashMap<>();
    LinearLayout coordinatorLayout;
    RegistrationListener registrationListener = registrationListener();
    private String phoneNum;
    private String accessToken;
    /*
     * Audio device management
     */
    private AudioDeviceSelector audioDeviceSelector;
    private int savedVolumeControlStream;
    private boolean isReceiverRegistered = false;
    private NotificationManager notificationManager;
    private AlertDialog alertDialog;
    private CallInvite activeCallInvite;
    private Call activeCall;
    Call.Listener callListener = callListener();
    private int activeCallNotificationId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_internet_calling);
        ButterKnife.bind(this);
        phoneNum = getIntent().getStringExtra("number");
        table = new PaymentTable(Internet_Calling_Activity.this);
        txvCallernumber.setText(phoneNum);
        coordinatorLayout = findViewById(R.id.coordinatorLayout);
        // These flags ensure that the activity can be launched when the screen is locked.
        Window window = getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED
                | WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON
                | WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        audioDeviceSelector = new AudioDeviceSelector(getApplicationContext());
        savedVolumeControlStream = getVolumeControlStream();
        setVolumeControlStream(AudioManager.STREAM_VOICE_CALL);


        /*
         * Ensure the microphone permission is enabled
         */
        if (!checkPermissionForMicrophone()) {
            requestPermissionForMicrophone();
        } else {
            retrieveAccessToken();
        }


        // setCallUI();
    }

    private Call.Listener callListener() {
        return new Call.Listener() {
            /*
             * This callback is emitted once before the Call.Listener.onConnected() callback when
             * the callee is being alerted of a Call. The behavior of this callback is determined by
             * the answerOnBridge flag provided in the Dial verb of your TwiML application
             * associated with this client. If the answerOnBridge flag is false, which is the
             * default, the Call.Listener.onConnected() callback will be emitted immediately after
             * Call.Listener.onRinging(). If the answerOnBridge flag is true, this will cause the
             * call to emit the onConnected callback only after the call is answered.
             * See answeronbridge for more details on how to use it with the Dial TwiML verb. If the
             * twiML response contains a Say verb, then the call will emit the
             * Call.Listener.onConnected callback immediately after Call.Listener.onRinging() is
             * raised, irrespective of the value of answerOnBridge being set to true or false
             */
            @Override
            public void onRinging(@NonNull Call call) {
                Log.d(TAG, "Ringing");
                /*
                 * When [answerOnBridge](https://www.twilio.com/docs/voice/twiml/dial#answeronbridge)
                 * is enabled in the <Dial> TwiML verb, the caller will not hear the ringback while
                 * the call is ringing and awaiting to be accepted on the callee's side. The application
                 * can use the `SoundPoolManager` to play custom audio files between the
                 * `Call.Listener.onRinging()` and the `Call.Listener.onConnected()` callbacks.
                 */
                SoundPoolManager.getInstance(Internet_Calling_Activity.this).playRinging();

            }

            @Override
            public void onConnectFailure(@NonNull Call call, @NonNull CallException error) {
                audioDeviceSelector.deactivate();
                SoundPoolManager.getInstance(Internet_Calling_Activity.this).stopRinging();
                Log.d(TAG, "Connect failure");
                String message = String.format(
                        Locale.US,
                        "Call Error: %d, %s",
                        error.getErrorCode(),
                        error.getMessage());
                Log.e(TAG, message);
                Snackbar.make(coordinatorLayout, message, Snackbar.LENGTH_LONG).show();

            }

            @Override
            public void onConnected(@NonNull Call call) {
                audioDeviceSelector.activate();
                SoundPoolManager.getInstance(Internet_Calling_Activity.this).stopRinging();
                Log.d(TAG, "Connected");
                activeCall = call;
            }

            @Override
            public void onReconnecting(@NonNull Call call, @NonNull CallException callException) {
                Log.d(TAG, "onReconnecting");
            }

            @Override
            public void onReconnected(@NonNull Call call) {
                Log.d(TAG, "onReconnected");
            }

            @Override
            public void onDisconnected(@NonNull Call call, CallException error) {
                audioDeviceSelector.deactivate();
                SoundPoolManager.getInstance(Internet_Calling_Activity.this).stopRinging();
                Log.d(TAG, "Disconnected");
                if (error != null) {
                    String message = String.format(
                            Locale.US,
                            "Call Error: %d, %s",
                            error.getErrorCode(),
                            error.getMessage());
                    Log.e(TAG, message);
                    Snackbar.make(coordinatorLayout, message, Snackbar.LENGTH_LONG).show();
                }
            }

            /*
             * currentWarnings: existing quality warnings that have not been cleared yet
             * previousWarnings: last set of warnings prior to receiving this callback
             *
             * Example:
             *   - currentWarnings: { A, B }
             *   - previousWarnings: { B, C }
             *
             * Newly raised warnings = currentWarnings - intersection = { A }
             * Newly cleared warnings = previousWarnings - intersection = { C }
             */
            public void onCallQualityWarningsChanged(@NonNull Call call,
                                                     @NonNull Set<Call.CallQualityWarning> currentWarnings,
                                                     @NonNull Set<Call.CallQualityWarning> previousWarnings) {
                currentWarnings.retainAll(previousWarnings);
                previousWarnings.removeAll(currentWarnings);
                String message = String.format(
                        Locale.US,
                        "Newly raised warnings: " + currentWarnings + " Clear warnings " + previousWarnings);
                Log.e(TAG, message);
                Snackbar.make(coordinatorLayout, message, Snackbar.LENGTH_LONG).show();
            }
        };
    }

    public boolean checkPermission() {
        int result = ContextCompat.checkSelfPermission(getApplicationContext(),
                WRITE_EXTERNAL_STORAGE);
        int result1 = ContextCompat.checkSelfPermission(getApplicationContext(),
                RECORD_AUDIO);
        return result == PackageManager.PERMISSION_GRANTED &&
                result1 == PackageManager.PERMISSION_GRANTED;
    }

    private void requestPermission() {
        ActivityCompat.requestPermissions(Internet_Calling_Activity.this, new
                String[]{WRITE_EXTERNAL_STORAGE, RECORD_AUDIO}, 589);
    }

    @OnClick({R.id.txvRejectCall, R.id.dynamicToggleVideoCall, R.id.dynamicToggleMuteCall})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.txvRejectCall:
                SoundPoolManager.getInstance(this).playDisconnect();
                disconnect();
                break;
            case R.id.dynamicToggleVideoCall:

                break;
            case R.id.dynamicToggleMuteCall:
                mute();
                break;
        }
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
        if (requestCode == 998) {
            hideProgressDialog();
                    accessToken = response;
            //Place Call
            params.clear();
            params.put("to", phoneNum);
            ConnectOptions connectOptions = new ConnectOptions.Builder(accessToken)
                    .params(params)
                    .build();
            activeCall = Voice.connect(this, connectOptions, callListener);
        }

    }


    private RegistrationListener registrationListener() {
        return new RegistrationListener() {
            @Override
            public void onRegistered(@NonNull String accessToken, @NonNull String fcmToken) {
                Log.d(TAG, "Successfully registered FCM " + fcmToken);
            }

            @Override
            public void onError(@NonNull RegistrationException error,
                                @NonNull String accessToken,
                                @NonNull String fcmToken) {
                String message = String.format(
                        Locale.US,
                        "Registration Error: %d, %s",
                        error.getErrorCode(),
                        error.getMessage());
                Log.e(TAG, message);
                Snackbar.make(coordinatorLayout, message, Snackbar.LENGTH_LONG).show();
            }
        };
    }

    /*
     * The UI state when there is an active call
     */


    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    public void onDestroy() {
        /*
         * Tear down audio device management and restore previous volume stream
         */
        audioDeviceSelector.stop();
        setVolumeControlStream(savedVolumeControlStream);
        SoundPoolManager.getInstance(this).release();
        super.onDestroy();
    }

    /*
     * Disconnect from Call
     */
    private void disconnect() {
        if (activeCall != null) {
            activeCall.disconnect();
            activeCall = null;
        }
    }

    private void hold() {
        if (activeCall != null) {
            boolean hold = !activeCall.isOnHold();
            activeCall.hold(hold);
            // applyFabState(holdActionFab, hold);
        }
    }

    private void mute() {
        if (activeCall != null) {
            boolean mute = !activeCall.isMuted();
            activeCall.mute(mute);
            //  applyFabState(muteActionFab, mute);
        }
    }

    private boolean checkPermissionForMicrophone() {
        int resultMic = ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO);
        return resultMic == PackageManager.PERMISSION_GRANTED;
    }

    private void requestPermissionForMicrophone() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.RECORD_AUDIO)) {
            Snackbar.make(coordinatorLayout,
                    "Microphone permissions needed. Please allow in your application settings.",
                    Snackbar.LENGTH_LONG).show();
        } else {
            ActivityCompat.requestPermissions(
                    this,
                    new String[]{Manifest.permission.RECORD_AUDIO},
                    MIC_PERMISSION_REQUEST_CODE);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        /*
         * Check if microphone permissions is granted
         */
        if (requestCode == MIC_PERMISSION_REQUEST_CODE && permissions.length > 0) {
            if (grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                Snackbar.make(coordinatorLayout,
                        "Microphone permissions needed. Please allow in your application settings.",
                        Snackbar.LENGTH_LONG).show();
            } else {
                retrieveAccessToken();
            }
        }
    }

    private void retrieveAccessToken() {
        showProgressDialog(R.string.load);
        JSONObject Params_Object = new JSONObject();
        JSONRequestResponse mResponse = new JSONRequestResponse(this);
        Bundle parms = new Bundle();
        MyVolley.init(this);
        mResponse.getResponse(Request.Method.GET, accessToken_url,
                998, this, parms, false, false, Params_Object);
    }

}