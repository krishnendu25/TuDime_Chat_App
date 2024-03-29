package com.TuDime.Internet_Calling;

import android.content.Context;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.SystemClock;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Chronometer;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.VolleyError;
import com.sinch.android.rtc.ClientRegistration;
import com.sinch.android.rtc.PushPair;
import com.sinch.android.rtc.Sinch;
import com.sinch.android.rtc.SinchClient;
import com.sinch.android.rtc.SinchClientListener;
import com.sinch.android.rtc.SinchError;
import com.sinch.android.rtc.calling.Call;
import com.sinch.android.rtc.calling.CallListener;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import com.TuDime.NetworkOperation.JSONRequestResponse;
import com.TuDime.NetworkOperation.MyVolley;
import com.TuDime.R;
import com.TuDime.ui.activity.BaseActivity;
import com.TuDime.Prefrences.SharedPrefsHelper;
import com.TuDime.utils.ToastUtils;

import static com.TuDime.constants.ApiConstants.buy_call_balence_update;
import static com.TuDime.constants.AppConstants.applicationKey_Sinch;
import static com.TuDime.constants.AppConstants.applicationSecret_Sinch;
import static com.TuDime.constants.AppConstants.environmentHost_Sinch;

public class Internet_Calling_Activity extends BaseActivity {

    @BindView(R.id.txvCallernumber)
    TextView txvCallernumber;
    @BindView(R.id.chrnTimer)
    Chronometer chrnTimer;
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
    @BindView(R.id.timer_call)
    TextView timerCall;
    String CountryName = "";
    String myBlance = "";
    private String phoneNum;
    private PaymentTable table;
    private SinchClient sinchClient;
    private Call call;
    private MediaPlayer playr;
    private AudioManager audioManager;
    private Handler handler;
    private double callChargePerMinute=0.005;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_internet_calling);
        ButterKnife.bind(this);
        iniView();

        sinchClient = Sinch.getSinchClientBuilder().context(Internet_Calling_Activity.this)
                .applicationKey(applicationKey_Sinch)
                .applicationSecret(applicationSecret_Sinch)
                .environmentHost(environmentHost_Sinch)
                .userId(SharedPrefsHelper.getInstance().getUSERID())
                .build();

        sinchClient.setSupportMessaging(true);
        sinchClient.setSupportCalling(true);
        sinchClient.setSupportActiveConnectionInBackground(true);
        sinchClient.startListeningOnActiveConnection();
        sinchClient.start();

        sinchClient.addSinchClientListener(new SinchClientListener() {

            public void onClientStarted(SinchClient client) {
                showLog("@@@@", "onClientStarted");
            }

            public void onClientStopped(SinchClient client) {
                showLog("@@@@", "onClientStopped");
            }

            public void onClientFailed(SinchClient client, SinchError error) {
                showLog("@@@@", "onClientFailed");
            }

            public void onRegistrationCredentialsRequired(SinchClient client, ClientRegistration registrationCallback) {
                showLog("@@@@", "onRegistrationCredentialsRequired");
            }

            public void onLogMessage(int level, String area, String message) {
                showLog("@@@@", "onLogMessage");
            }
        });


        try {
            handler = new Handler(Looper.getMainLooper());
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    try {
                        if (call == null) {
                            sinchClient.getCallClient().callPhoneNumber(phoneNum).addCallListener(new CallListener() {
                                @Override
                                public void onCallProgressing(Call call) {
                                    try {
                                        sinchClient.getAudioController().disableSpeaker();
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                    connectionStatus.setText("Call Progressing");
                                    try {
                                        playr.stop();
                                        playr = MediaPlayer.create(Internet_Calling_Activity.this, R.raw.phone_ring);
                                        playr.start();
                                    } catch (IllegalStateException e) {
                                        e.printStackTrace();
                                    }
                                }

                                @Override
                                public void onCallEstablished(Call call) {
                                    try {
                                        chrnTimer.setBase(SystemClock.elapsedRealtime());
                                        chrnTimer.start();
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                    connectionStatus.setText("Call Established");
                                    if (playr != null)
                                        playr.stop();
                                }

                                @Override
                                public void onCallEnded(Call call) {
                                    setVolumeControlStream(AudioManager.USE_DEFAULT_STREAM_TYPE);
                                    connectionStatus.setText("Call Ended");
                                    debitMoney(chrnTimer.getText().toString().trim());
                                    playr = MediaPlayer.create(Internet_Calling_Activity.this, R.raw.beep26);
                                    if (playr != null)
                                        playr.start();

                                    final Handler handler = new Handler(Looper.getMainLooper());
                                    handler.postDelayed(new Runnable() {
                                        @Override
                                        public void run() {
                                            if (playr != null)
                                                playr.stop();
                                        }
                                    }, 2000);
                                    finish();
                                }

                                @Override
                                public void onShouldSendPushNotification(Call call, List<PushPair> list) {

                                }
                            });
                        }

                    } catch (Exception e) {
                        debitMoney(chrnTimer.getText().toString().trim());
                        connectionStatus.setText("Call Ended");
                        try {
                            if (call != null) {
                                call.hangup();

                            }
                        } catch (Exception de) {
                            de.printStackTrace();
                        }
                        ToastUtils.longToast("Call End");
                        finish();

                    }

                }
            }, 5000);
        } catch (Exception e) {
            e.printStackTrace();
        }


    }

    private void iniView() {
        myBlance = getIntent().getStringExtra("myBlance");
        phoneNum = getIntent().getStringExtra("PHONE_NO");
        new normalizePhoneNumberTask().execute(phoneNum);
        txvCallernumber.setText(phoneNum);
        chrnTimer.setText("-----");
        getCountryNameFromNumber(phoneNum);
        table = new PaymentTable(Internet_Calling_Activity.this);

        audioManager = (AudioManager) getApplicationContext().getSystemService(Context.AUDIO_SERVICE);
        audioManager.setMode(AudioManager.MODE_IN_CALL);
        audioManager.setSpeakerphoneOn(false);
        playr = MediaPlayer.create(Internet_Calling_Activity.this, R.raw.beep26);
        if (playr != null) {
            playr.start();
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

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (sinchClient != null) {
            sinchClient.stopListeningOnActiveConnection();
            sinchClient.terminate();
        }
        if (call != null) {
            call.hangup();
        }
    }

    void showLog(String tag, String sms) {
        Log.e(tag, sms);
    }

    @OnClick({R.id.txvRejectCall, R.id.linear_buttons, R.id.dynamicToggleVideoCall, R.id.dynamicToggleMuteCall})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.txvRejectCall:
                try {
                    debitMoney(chrnTimer.getText().toString().trim());
                    if (call != null) {
                        call.hangup();

                    }
                    if (playr != null)
                        playr.stop();

                } catch (Exception e) {
                    e.printStackTrace();
                }
                ToastUtils.longToast("Call End");
                finish();
                break;
            case R.id.linear_buttons:
                try {
                    if (call != null) {
                        call.hangup();

                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                ToastUtils.longToast("Call End");
                finish();
                break;
            case R.id.dynamicToggleVideoCall:
                if (!audioManager.isSpeakerphoneOn()) {
                    audioManager.setSpeakerphoneOn(true);
                    ToastUtils.longToast("Loudspeaker On");
                    try {
                        sinchClient.getAudioController().enableSpeaker();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    dynamicToggleVideoCall.setBackground(getResources().getDrawable(R.drawable.ic_volume_low_white_24dp));
                } else {
                    audioManager.setSpeakerphoneOn(false);
                    ToastUtils.longToast("Loudspeaker Off");
                    try {
                        sinchClient.getAudioController().disableSpeaker();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    dynamicToggleVideoCall.setBackground(getResources().getDrawable(R.drawable.ic_volume_high_white_24dp));
                }
                break;
            case R.id.dynamicToggleMuteCall:
                if (!audioManager.isMicrophoneMute()) {
                    audioManager.setMicrophoneMute(true);
                    try {
                        sinchClient.getAudioController().mute();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    ToastUtils.longToast("Call Mute");
                    dynamicToggleMuteCall.setBackground(getResources().getDrawable(R.drawable.ic_microphone_white_24dp));
                } else {
                    audioManager.setMicrophoneMute(false);
                    ToastUtils.longToast("Call Unmute");
                    try {
                        sinchClient.getAudioController().unmute();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    dynamicToggleMuteCall.setBackground(getResources().getDrawable(R.drawable.selector_toggle_mic));
                }
                break;
        }
    }

    @Override
    public void onBackPressed() {
        try {
            if (sinchClient != null) {
                sinchClient.stopListeningOnActiveConnection();
                sinchClient.terminate();
            }
            try {
                if (call != null) {
                    call.hangup();

                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            if (playr != null)
                playr.stop();
        } catch (IllegalStateException e) {
            e.printStackTrace();
        }
        ToastUtils.longToast("Call End");
        finish();
    }


    private void getCountryNameFromNumber(String Number) {


        CountryName = "";
    }

    private void debitMoney(String time) {
        if (!time.equalsIgnoreCase("-----")) {
            String TotalBlance = String.valueOf(Double.valueOf(myBlance)*100);//in cent
            String finalTime = time.replaceAll(":", ".");
            String totalCost = String.valueOf(Double.valueOf(finalTime)*Double.valueOf(callChargePerMinute));
            String current_blance = String.valueOf((Double.valueOf(TotalBlance)-Double.valueOf(totalCost))/100);
            hit_Call_Balance(current_blance);
        }
    }

    private void hit_Call_Balance(String price) {
        JSONObject Params_Object = new JSONObject();
        JSONRequestResponse mResponse = new JSONRequestResponse(this);
        Bundle parms = new Bundle();
        parms.putString("useid", SharedPrefsHelper.getInstance().getUSERID());
        parms.putString("plan_name", "Credit DebitMoney");
        parms.putString("plan_price", String.valueOf(price));
        parms.putString("Payment_Referance_no", "debitMoney");
        MyVolley.init(this);
        mResponse.getResponse(Request.Method.POST, buy_call_balence_update, 892, this, parms, false, false, Params_Object);
    }



    class normalizePhoneNumberTask extends AsyncTask<String, Void, String> {
        private String appKey = "e33bc940-7df1-461d-9096-455d78ce3927";
        private String appSecret = "2aIOtRyFM02z3CKQwS4uPg==";

        //takes phone number string as an argument
        @Override
        protected String doInBackground(String... params) {
            String normalizedPhoneNumber = "";
            try {
                //get ready to make a get request to normalize the phone number
                HttpClient httpclient = new DefaultHttpClient();
                HttpGet httpGet = new HttpGet("https://callingapi.sinch.com/v1/calling/query/number/" + params[0].replaceAll("\\s+", ""));
                //sinch uses basic authentication
                String usernamePassword = "application:" + appKey + ":" + appSecret;
                String encoded = Base64.encodeToString(usernamePassword.getBytes(), Base64.NO_WRAP);
                httpGet.addHeader("Authorization", "Basic " + encoded);
                //handle the response
                HttpResponse response = httpclient.execute(httpGet);
                ResponseHandler<String> handler = new BasicResponseHandler();
                //parse JSON response from Sinch to get phone number
                normalizedPhoneNumber = parseJSONResponse(handler.handleResponse(response));
            } catch (ClientProtocolException e) {
                Log.d("ClientProtocolException", e.getMessage());
            } catch (IOException e) {
                Log.d("IOException", e.getMessage());
            }
            return normalizedPhoneNumber;
        }
        //once the asynctask is complete, display a toast message with the normalized phone number
        @Override
        protected void onPostExecute(String normalizedPhoneNumber) {
            callChargePerMinute = Float.parseFloat(normalizedPhoneNumber);

        }
    }


    private String parseJSONResponse(String jsonString) {
        String returnString = "";
        try {
            JSONObject jsonObject = new JSONObject(jsonString);
            returnString = jsonObject.getJSONObject("number").getJSONObject("rate").getString("amount");
        } catch (JSONException e) {
            Log.d("JSONException", e.getMessage());
        }
        return returnString;
    }


}