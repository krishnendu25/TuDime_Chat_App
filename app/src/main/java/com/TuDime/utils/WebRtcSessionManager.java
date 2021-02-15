package com.TuDime.utils;

import android.content.Context;

import com.TuDime.ui.activity.CallActivity;
import com.quickblox.videochat.webrtc.QBRTCSession;
import com.quickblox.videochat.webrtc.callbacks.QBRTCClientSessionCallbacksImpl;


public class WebRtcSessionManager extends QBRTCClientSessionCallbacksImpl {
    private static final String TAG = WebRtcSessionManager.class.getSimpleName();

    private static WebRtcSessionManager instance;
    private Context context;

    private static QBRTCSession currentSession;

    private WebRtcSessionManager(Context context) {
        this.context = context;
    }

    public static WebRtcSessionManager getInstance(Context context) {
        try {
            if (instance == null) {
                instance = new WebRtcSessionManager(context);
            }
        }catch (Exception e){

        }


        return instance;
    }

    public QBRTCSession getCurrentSession() {
        return currentSession;
    }

    public void setCurrentSession(QBRTCSession qbCurrentSession) {
        currentSession = qbCurrentSession;
    }

    @Override
    public void onReceiveNewSession(QBRTCSession session) {
        try{
            if (currentSession == null && session != null) {
                setCurrentSession(session);
                CallActivity.start(context, true);
            }
        }catch (Exception e){}

    }

    @Override
    public void onSessionClosed(QBRTCSession session) {
        try{
            if (session.equals(getCurrentSession())) {
                setCurrentSession(null);
            }
        }catch (Exception e){}


    }
}