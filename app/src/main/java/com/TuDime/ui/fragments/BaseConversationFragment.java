package com.TuDime.ui.fragments;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.ToggleButton;

import androidx.annotation.Nullable;

import com.quickblox.chat.QBChatService;
import com.TuDime.R;
import com.TuDime.db.QbUsersDbManager;
import com.TuDime.services.CallService;
import com.TuDime.ui.activity.CallActivity;
import com.TuDime.constants.Consts;
import com.TuDime.Prefrences.SharedPrefsHelper;
import com.TuDime.utils.UsersUtils;
import com.TuDime.utils.WebRtcSessionManager;

import com.quickblox.core.QBEntityCallback;
import com.quickblox.core.exception.QBResponseException;
import com.quickblox.core.request.QBPagedRequestBuilder;
import com.quickblox.users.QBUsers;
import com.quickblox.users.model.QBUser;
import com.quickblox.videochat.webrtc.QBRTCTypes;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public abstract class BaseConversationFragment extends BaseToolBarFragment implements CallActivity.CurrentCallStateCallback {
    private static final String TAG = BaseConversationFragment.class.getSimpleName();

    public static final String MIC_ENABLED = "is_microphone_enabled";
    protected QbUsersDbManager dbManager;
    protected WebRtcSessionManager sessionManager;

    private boolean isIncomingCall;
    protected TextView timerCallText;
    protected ConversationFragmentCallback conversationFragmentCallback;
    protected QBUser currentUser;
    protected ArrayList<QBUser> opponents;
    protected boolean isStarted;

    private ToggleButton micToggleVideoCall;
    private ImageButton handUpVideoCall;
    protected View outgoingOpponentsRelativeLayout;
    protected TextView allOpponentsTextView;
    protected TextView ringingTextView;

    public static BaseConversationFragment newInstance(BaseConversationFragment baseConversationFragment, boolean isIncomingCall) {
        Log.d(TAG, "isIncomingCall =  " + isIncomingCall);
        Bundle args = new Bundle();
        args.putBoolean(Consts.EXTRA_IS_INCOMING_CALL, isIncomingCall);
        baseConversationFragment.setArguments(args);
        return baseConversationFragment;
    }


    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            conversationFragmentCallback = (ConversationFragmentCallback) getContext();
        } catch (Exception e) {
            throw new ClassCastException(activity.toString()
                    + " must implement ConversationFragmentCallback");
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (conversationFragmentCallback != null) {
            conversationFragmentCallback.addCurrentCallStateListener(this);
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = super.onCreateView(inflater, container, savedInstanceState);

        initFields();
        initViews(view);
        initActionBar();
        initButtonsListener();
        prepareAndShowOutgoingScreen();

        return view;
    }

    private void initActionBar() {
        configureToolbar();
        configureActionBar();
    }

    private void prepareAndShowOutgoingScreen() {
        configureOutgoingScreen();
       // allOpponentsTextView.setText(CollectionsUtils.makeStringFromUsersFullNames(opponents));
        QBPagedRequestBuilder pagedRequestBuilder = new QBPagedRequestBuilder();
        pagedRequestBuilder.setPage(1);
        pagedRequestBuilder.setPerPage(50);

        ArrayList<Integer> usersIDs = new ArrayList<>();
        usersIDs.add(opponents.get(0).getId());

        QBUsers.getUsersByIDs(usersIDs, pagedRequestBuilder).performAsync(new QBEntityCallback<ArrayList<QBUser>>() {
            @Override
            public void onSuccess(ArrayList<QBUser> qbUsers, Bundle bundle) {
                allOpponentsTextView.setText(qbUsers.get(0).getFullName());
            }

            @Override
            public void onError(QBResponseException e) {

            }
        });

    }

    protected abstract void configureOutgoingScreen();

    protected abstract void configureActionBar();

    protected abstract void configureToolbar();

    protected void initFields() {
        dbManager = QbUsersDbManager.getInstance(getActivity().getApplicationContext());
        sessionManager = WebRtcSessionManager.getInstance(getActivity());
        currentUser = QBChatService.getInstance().getUser();
        if (currentUser == null) {
            currentUser = SharedPrefsHelper.getInstance().getQbUser();
        }

        if (getArguments() != null) {
            isIncomingCall = getArguments().getBoolean(Consts.EXTRA_IS_INCOMING_CALL, false);
        }
        initOpponentsList();
        Log.d(TAG, "Opponents: " + opponents.toString());
    }

    @Override
    public void onStart() {
        super.onStart();
        if (conversationFragmentCallback != null) {
            if (isIncomingCall) {
                conversationFragmentCallback.acceptCall(new HashMap<>());
            } else {
                conversationFragmentCallback.startCall(new HashMap<>());
            }
        }
    }

    @Override
    public void onDestroy() {
        if (conversationFragmentCallback != null) {
            conversationFragmentCallback.removeCurrentCallStateListener(this);
        }
        super.onDestroy();
    }

    protected void initViews(View view) {
        micToggleVideoCall = (ToggleButton) view.findViewById(R.id.toggle_mic);
        micToggleVideoCall.setChecked(SharedPrefsHelper.getInstance().get(MIC_ENABLED, true));
        handUpVideoCall = (ImageButton) view.findViewById(R.id.button_hangup_call);
        outgoingOpponentsRelativeLayout = view.findViewById(R.id.layout_background_outgoing_screen);
        allOpponentsTextView = (TextView) view.findViewById(R.id.text_outgoing_opponents_names);
        ringingTextView = (TextView) view.findViewById(R.id.text_ringing);

        if (isIncomingCall) {
            hideOutgoingScreen();
        }
    }

    protected void initButtonsListener() {
        micToggleVideoCall.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                SharedPrefsHelper.getInstance().save(MIC_ENABLED, isChecked);
                if (conversationFragmentCallback != null) {
                    conversationFragmentCallback.onSetAudioEnabled(isChecked);
                }
            }
        });

        handUpVideoCall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                actionButtonsEnabled(false);
                handUpVideoCall.setEnabled(false);
                handUpVideoCall.setActivated(false);
                try{
                    CallService.stop(getContext());
                }catch (Exception e){

                }


                if (conversationFragmentCallback != null) {
                    conversationFragmentCallback.onHangUpCurrentSession();
                }
                Log.d(TAG, "Call is Stopped");
            }
        });
    }

    private void clearButtonsState() {
        SharedPrefsHelper.getInstance().delete(MIC_ENABLED);
        SharedPrefsHelper.getInstance().delete(AudioConversationFragment.SPEAKER_ENABLED);
        SharedPrefsHelper.getInstance().delete(VideoConversationFragment.CAMERA_ENABLED);
    }

    protected void actionButtonsEnabled(boolean enabled) {
        micToggleVideoCall.setEnabled(enabled);
        micToggleVideoCall.setActivated(enabled);
    }

    private void startTimer() {
        if (!isStarted) {
            timerCallText.setVisibility(View.VISIBLE);
            isStarted = true;
        }
    }

    private void hideOutgoingScreen() {
        outgoingOpponentsRelativeLayout.setVisibility(View.GONE);
    }

    @Override
    public void onCallStarted() {
        hideOutgoingScreen();
        startTimer();
        actionButtonsEnabled(true);
    }

    @Override
    public void onCallStopped() {
        CallService.stop(getContext());
        isStarted = false;
        clearButtonsState();
        actionButtonsEnabled(false);
    }

    @Override
    public void onOpponentsListUpdated(ArrayList<QBUser> newUsers) {
        initOpponentsList();
    }

    private void initOpponentsList() {
        Log.v(TAG, "initOpponentsList()");
        if (conversationFragmentCallback != null) {
            List<Integer> opponentsIds = conversationFragmentCallback.getOpponents();

            if (opponentsIds != null) {
                ArrayList<QBUser> usersFromDb = dbManager.getUsersByIds(opponentsIds);
                opponents = UsersUtils.getListAllUsersFromIds(usersFromDb, opponentsIds);
            }

            QBUser caller = dbManager.getUserById(conversationFragmentCallback.getCallerId());
            if (caller == null) {
                caller = new QBUser(conversationFragmentCallback.getCallerId());
                caller.setFullName(String.valueOf(conversationFragmentCallback.getCallerId()));
            }

            if (isIncomingCall) {
                opponents.add(caller);
                opponents.remove(QBChatService.getInstance().getUser());
            }
        }
    }

    public QBRTCTypes.QBRTCConnectionState getConnectionState(Integer userID) {
        QBRTCTypes.QBRTCConnectionState result = null;
        if (conversationFragmentCallback != null) {
            result = conversationFragmentCallback.getPeerChannel(userID);
        }
        return result;
    }
}