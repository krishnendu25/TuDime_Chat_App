package obj.quickblox.sample.chat.java.ui.fragments;

import android.app.Activity;
import android.content.Context;
import android.database.Cursor;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.SystemClock;
import android.os.Vibrator;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import com.quickblox.chat.QBChatService;
import com.quickblox.core.QBEntityCallback;
import com.quickblox.core.exception.QBResponseException;
import com.quickblox.core.request.GenericQueryRule;
import com.quickblox.core.request.QBPagedRequestBuilder;
import obj.quickblox.sample.chat.java.App;
import obj.quickblox.sample.chat.java.R;
import obj.quickblox.sample.chat.java.db.QbUsersDbManager;
import obj.quickblox.sample.chat.java.ui.Model.Call_model;
import obj.quickblox.sample.chat.java.utils.CollectionsUtils;
import obj.quickblox.sample.chat.java.utils.RingtonePlayer;
import obj.quickblox.sample.chat.java.utils.SharedPrefsHelper;
import obj.quickblox.sample.chat.java.utils.UiUtils;
import obj.quickblox.sample.chat.java.utils.UsersUtils;
import obj.quickblox.sample.chat.java.utils.WebRtcSessionManager;
import obj.quickblox.sample.chat.java.utils.qb.QbUsersHolder;

import com.quickblox.users.model.QBUser;
import com.quickblox.videochat.webrtc.QBRTCSession;
import com.quickblox.videochat.webrtc.QBRTCTypes;
import com.squareup.picasso.Picasso;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * QuickBlox team
 */
public class IncomeCallFragment extends BaseFragment implements Serializable, View.OnClickListener {
    private static final String TAG = IncomeCallFragment.class.getSimpleName();

    private static final int PER_PAGE_SIZE_100 = 100;
    private static final String ORDER_RULE = "order";
    private static final String ORDER_DESC_UPDATED = "desc string updated_at";

    private static final long CLICK_DELAY = TimeUnit.SECONDS.toMillis(2);
    private TextView callTypeTextView;
    private ImageButton rejectButton;
    private ImageButton takeButton;
    private TextView callerNameTextView;
    private ProgressBar progressUserName;
    ImageView callerAvatarImageView;
    private List<Integer> opponentsIds;
    private Vibrator vibrator;
    private QBRTCTypes.QBConferenceType conferenceType;
    private long lastClickTime = 0l;
    private RingtonePlayer ringtonePlayer;
    private IncomeCallFragmentCallbackListener incomeCallFragmentCallbackListener;
    private QBRTCSession currentSession;
    private QbUsersDbManager qbUserDbManager;
    private TextView alsoOnCallText;
    private QbUsersDbManager dbManager=null;
    private int Counter=0;
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        try {
            incomeCallFragmentCallbackListener = (IncomeCallFragmentCallbackListener) activity;
        } catch (Exception e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnCallEventsController");
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        setRetainInstance(true);

        Log.d(TAG, "onCreate() from IncomeCallFragment");
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_income_call, container, false);

        initFields();
        hideToolBar();

        initUI(view);
        setDisplayedTypeCall(conferenceType);
        initButtonsListener();
        try{
            ringtonePlayer = new RingtonePlayer(getActivity());
        }catch (Exception e)
        {

        }


        return view;
    }

    private void initFields() {
        currentSession = WebRtcSessionManager.getInstance(getActivity()).getCurrentSession();
        qbUserDbManager = QbUsersDbManager.getInstance(getActivity().getApplicationContext());
        if (currentSession != null) {
            opponentsIds = currentSession.getOpponents();
            conferenceType = currentSession.getConferenceType();
            Log.d(TAG, conferenceType.toString() + "From onCreateView()");
        } else {
            getActivity().getSupportFragmentManager().popBackStack();
            Log.d(TAG, "Current session is not exist. Pop BackStack");
        }
    }

    public void hideToolBar() {
        Toolbar toolbar = getActivity().findViewById(R.id.toolbar_call);
        toolbar.setVisibility(View.GONE);
    }

    @Override
    public void onStart() {
        super.onStart();
        startCallNotification();
    }

    private void initButtonsListener() {
        rejectButton.setOnClickListener(this);
        takeButton.setOnClickListener(this);
    }

    private void initUI(View view) {
        dbManager = QbUsersDbManager.getInstance(getContext());
        callTypeTextView = view.findViewById(R.id.call_type);
        progressUserName = view.findViewById(R.id.progress_bar_opponent_name);
        callerAvatarImageView = view.findViewById(R.id.image_caller_avatar);
        callerNameTextView = view.findViewById(R.id.text_caller_name);
        TextView otherIncUsersTextView = view.findViewById(R.id.text_other_inc_users);
        alsoOnCallText = view.findViewById(R.id.text_also_on_call);
        rejectButton = view.findViewById(R.id.image_button_reject_call);
        takeButton = view.findViewById(R.id.image_button_accept_call);

        if (currentSession != null) {
            //callerAvatarImageView.setBackgroundDrawable(getBackgroundForCallerAvatar(currentSession.getCallerID()));


            try{
                QBUser user = QbUsersHolder.getInstance().getUserById(Integer.valueOf(currentSession.getCallerID()));
                Picasso.get()
                        .load(user.getWebsite())
                        .placeholder(R.drawable.call_avater_ic)
                        .into( callerAvatarImageView);
            }catch (Exception e)
            {
                callerAvatarImageView.setBackground(getResources().getDrawable(R.drawable.call_avater_ic));
            }




          //  QBUser callerUser = qbUserDbManager.getUserById(currentSession.getCallerID());
            QBUser user = QbUsersHolder.getInstance().getUserById(Integer.valueOf(currentSession.getCallerID()));
            String Name = QbUsersHolder.getInstance().getUserById(currentSession.getCallerID()).getFullName();
            if (Name.equalsIgnoreCase("")) {
                callerNameTextView.setText(Name);
            } else {
                callerNameTextView.setText(Name);
                updateLastUsersFromServer();
            }
            otherIncUsersTextView.setText(getOtherIncUsersNames());
            setVisibilityAlsoOnCallTextView();
        }
    }

    private void updateLastUsersFromServer() {
        progressUserName.setVisibility(View.VISIBLE);

        ArrayList<GenericQueryRule> rules = new ArrayList<>();
        rules.add(new GenericQueryRule(ORDER_RULE, ORDER_DESC_UPDATED));

        QBPagedRequestBuilder qbPagedRequestBuilder = new QBPagedRequestBuilder();
        qbPagedRequestBuilder.setRules(rules);
        qbPagedRequestBuilder.setPerPage(PER_PAGE_SIZE_100);

        App.getInstance().getQbResRequestExecutor().loadLastUpdatedUsers(qbPagedRequestBuilder, new QBEntityCallback<ArrayList<QBUser>>() {
            @Override
            public void onSuccess(ArrayList<QBUser> qbUsers, Bundle bundle) {
                qbUserDbManager.saveAllUsers(qbUsers, true);
                QBUser callerUser = qbUserDbManager.getUserById(currentSession.getCallerID());
                if (callerUser != null && !TextUtils.isEmpty(callerUser.getFullName())) {
                    callerNameTextView.setText(callerUser.getFullName());
                }
                progressUserName.setVisibility(View.GONE);
            }

            @Override
            public void onError(QBResponseException e) {
                progressUserName.setVisibility(View.GONE);
            }
        });
    }

    private void setVisibilityAlsoOnCallTextView() {
        if (opponentsIds.size() < 2) {
            alsoOnCallText.setVisibility(View.INVISIBLE);
        }
    }

    private Drawable getBackgroundForCallerAvatar(int callerId) {
        return UiUtils.getColorCircleDrawable(callerId);
    }

    private void startCallNotification() {
        Log.d(TAG, "startCallNotification()");

        try{
            ringtonePlayer.play(false);
        }catch (Exception e)
        {

        }


        vibrator = (Vibrator) getActivity().getSystemService(Context.VIBRATOR_SERVICE);

        long[] vibrationCycle = {0, 1000, 1000};
        if (vibrator.hasVibrator()) {
            vibrator.vibrate(vibrationCycle, 1);
        }

    }

    private void stopCallNotification() {
        Log.d(TAG, "stopCallNotification()");
        try{
            if (ringtonePlayer != null) {
                ringtonePlayer.stop();
            }
        }catch (Exception e)
        {

        }


        if (vibrator != null) {
            vibrator.cancel();
        }
    }


    private String getOtherIncUsersNames() {
        ArrayList<QBUser> usersFromDb = qbUserDbManager.getUsersByIds(opponentsIds);
        ArrayList<QBUser> opponents = new ArrayList<>();
        opponents.addAll(UsersUtils.getListAllUsersFromIds(usersFromDb, opponentsIds));

        opponents.remove(QBChatService.getInstance().getUser());
        Log.d(TAG, "opponentsIds = " + opponentsIds);
        return CollectionsUtils.makeStringFromUsersFullNames(opponents);
    }

    private void setDisplayedTypeCall(QBRTCTypes.QBConferenceType conferenceType) {
        boolean isVideoCall = conferenceType == QBRTCTypes.QBConferenceType.QB_CONFERENCE_TYPE_VIDEO;

        callTypeTextView.setText(isVideoCall ? R.string.text_incoming_video_call : R.string.text_incoming_audio_call);
        takeButton.setImageResource(isVideoCall ? R.drawable.ic_video_white : R.drawable.ic_call);
    }

    @Override
    public void onStop() {
        if (Counter==0)
        {
            Call_listing_db("MissCall");
        }
        stopCallNotification();
        super.onStop();

        Log.d(TAG, "onStop() from IncomeCallFragment");
    }

    @Override
    public void onClick(View v) {

        if ((SystemClock.uptimeMillis() - lastClickTime) < CLICK_DELAY) {
            return;
        }
        lastClickTime = SystemClock.uptimeMillis();

        switch (v.getId()) {
            case R.id.image_button_reject_call:
                reject();
                break;

            case R.id.image_button_accept_call:
                accept();
                break;

            default:
                break;
        }
    }

    private void accept() {
        Counter=Counter+1;
        enableButtons(false);
        stopCallNotification();
        Call_listing_db("InComing");
        incomeCallFragmentCallbackListener.onAcceptCurrentSession();
        Log.d(TAG, "Call is started");
    }

    private void reject() {
        enableButtons(false);
        stopCallNotification();
        Call_listing_db("Reject");
        incomeCallFragmentCallbackListener.onRejectCurrentSession();
        Log.d(TAG, "Call is rejected");
    }

    private void enableButtons(boolean enable) {
        takeButton.setEnabled(enable);
        rejectButton.setEnabled(enable);
    }



    void Call_listing_db(String call)
    {
        Cursor cursor_call = dbManager.get_CALL_BY_RECIPIENTID(String.valueOf(currentSession.getCallerID()));
        if (cursor_call.getCount()!=0)
        {
            Call_model call_model = new Call_model();
            call_model.setDB_CALL_COUNT("1");
            try{
                call_model.setDB_CALL_RECIPIENTID(String.valueOf(currentSession.getCallerID()));
                call_model.setDB_CALL_RECIPIENTNAME(QbUsersHolder.getInstance().getUserById(currentSession.getCallerID()).getFullName());
            }catch (Exception e)
            {
                e.printStackTrace();
            }
            boolean isVideoCall = conferenceType == QBRTCTypes.QBConferenceType.QB_CONFERENCE_TYPE_VIDEO;
            if (isVideoCall)
            {
                call_model.setDB_CALL_TYPE("video");
            }else
            {
                call_model.setDB_CALL_TYPE("audio");
            }
            call_model.setCall_status(call);
            call_model.setDB_CALL_START_TIME(String.valueOf(System.currentTimeMillis()));
            call_model.setDB_CALL_QBUSER(String.valueOf(SharedPrefsHelper.getInstance().getQbUser().getId()));
            dbManager.insertcall(call_model);
        }else
        {
            Call_model call_model = new Call_model();
            call_model.setDB_CALL_COUNT("1");
            call_model.setDB_CALL_RECIPIENTID(String.valueOf(currentSession.getCallerID()));
            call_model.setDB_CALL_RECIPIENTNAME(QbUsersHolder.getInstance().getUserById(currentSession.getCallerID()).getFullName());
            boolean isVideoCall = conferenceType == QBRTCTypes.QBConferenceType.QB_CONFERENCE_TYPE_VIDEO;
            if (isVideoCall)
            {
                call_model.setDB_CALL_TYPE("video");
            }else
            {
                call_model.setDB_CALL_TYPE("audio");
            }
            call_model.setCall_status(call);
            call_model.setDB_CALL_START_TIME(String.valueOf(System.currentTimeMillis()));
            call_model.setDB_CALL_QBUSER(String.valueOf(SharedPrefsHelper.getInstance().getQbUser().getId()));
            dbManager.insertcall(call_model);
        }
    }
}