package com.TuDime.ui.fragments;


import android.os.Bundle;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.ToggleButton;

import androidx.core.content.ContextCompat;

import com.quickblox.core.QBEntityCallback;
import com.quickblox.core.exception.QBResponseException;
import com.quickblox.core.request.QBPagedRequestBuilder;
import com.quickblox.users.QBUsers;
import com.quickblox.users.model.QBUser;
import com.quickblox.videochat.webrtc.AppRTCAudioManager;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import com.TuDime.R;
import com.TuDime.ui.activity.CallActivity;
import com.TuDime.utils.CollectionsUtils;
import com.TuDime.Prefrences.SharedPrefsHelper;
import com.TuDime.utils.qb.QbUsersHolder;

public class AudioConversationFragment extends BaseConversationFragment implements CallActivity.OnChangeAudioDevice {
    private static final String TAG = AudioConversationFragment.class.getSimpleName();

    public static final String SPEAKER_ENABLED = "is_speaker_enabled";

    private ToggleButton audioSwitchToggleButton;
    private TextView alsoOnCallText;
    private TextView firstOpponentNameTextView;
    private TextView otherOpponentsTextView;

    @Override
    public void onStart() {
        super.onStart();
        if (conversationFragmentCallback != null) {

            conversationFragmentCallback.addOnChangeAudioDeviceListener(this);
        }
    }

    @Override
    protected void configureOutgoingScreen() {
        outgoingOpponentsRelativeLayout.setBackgroundColor(ContextCompat.getColor(getActivity(), R.color.white));
        allOpponentsTextView.setTextColor(ContextCompat.getColor(getActivity(), R.color.text_color_outgoing_opponents_names_audio_call));
        ringingTextView.setTextColor(ContextCompat.getColor(getActivity(), R.color.text_color_call_type));
    }

    @Override
    protected void configureToolbar() {
        toolbar.setVisibility(View.GONE);
        toolbar.setBackgroundColor(ContextCompat.getColor(getActivity(), R.color.white));
        toolbar.setTitleTextColor(ContextCompat.getColor(getActivity(), R.color.toolbar_title_color));
        toolbar.setSubtitleTextColor(ContextCompat.getColor(getActivity(), R.color.toolbar_subtitle_color));
    }
    @Override
    protected void configureActionBar() {
        actionBar.setSubtitle(String.format(getString(R.string.subtitle_text_logged_in_as)+currentUser.getFullName()));
    }

    @Override
    protected void initViews(View view) {
        super.initViews(view);
        if (view == null) {
            return;
        }
        timerCallText = view.findViewById(R.id.timer_call);

        showAudioDevices();
        ImageView firstOpponentAvatarImageView = (ImageView) view.findViewById(R.id.image_caller_avatar);
       /* firstOpponentAvatarImageView.setBackgroundDrawable(UiUtils.getColorCircleDrawable(opponents.get(0).getId()));*/


        try{
            QBUser user = QbUsersHolder.getInstance().getUserById(Integer.valueOf(opponents.get(0).getId()));
            Picasso.get()
                    .load(user.getWebsite())
                    .placeholder(R.drawable.call_avater_ic)
                    .into( firstOpponentAvatarImageView);
        }catch (Exception e)
        {
            firstOpponentAvatarImageView.setBackground(getResources().getDrawable(R.drawable.call_avater_ic));
        }



        alsoOnCallText = (TextView) view.findViewById(R.id.text_also_on_call);
        setVisibilityAlsoOnCallTextView();

        firstOpponentNameTextView = (TextView) view.findViewById(R.id.text_caller_name);

        QBPagedRequestBuilder pagedRequestBuilder = new QBPagedRequestBuilder();
        pagedRequestBuilder.setPage(1);
        pagedRequestBuilder.setPerPage(50);
        ArrayList<Integer> usersIDs = new ArrayList<>();
        usersIDs.add(opponents.get(0).getId());

        QBUsers.getUsersByIDs(usersIDs, pagedRequestBuilder).performAsync(new QBEntityCallback<ArrayList<QBUser>>() {
            @Override
            public void onSuccess(ArrayList<QBUser> qbUsers, Bundle bundle) {
                firstOpponentNameTextView.setText(qbUsers.get(0).getFullName());
            }

            @Override
            public void onError(QBResponseException e) {

            }
        });



        otherOpponentsTextView = (TextView) view.findViewById(R.id.text_other_inc_users);
        otherOpponentsTextView.setText(getOtherOpponentsNames());

        audioSwitchToggleButton = (ToggleButton) view.findViewById(R.id.toggle_speaker);
        audioSwitchToggleButton.setVisibility(View.VISIBLE);
        audioSwitchToggleButton.setChecked(SharedPrefsHelper.getInstance().get(SPEAKER_ENABLED, true));




        actionButtonsEnabled(false);

        if (conversationFragmentCallback != null && conversationFragmentCallback.isCallState()) {
            onCallStarted();
        }
    }

    private void setVisibilityAlsoOnCallTextView() {
        if (opponents.size() < 2) {
            alsoOnCallText.setVisibility(View.INVISIBLE);
        }
    }
    private void showAudioDevices() {

    }
    private String getOtherOpponentsNames() {
        ArrayList<QBUser> otherOpponents = new ArrayList<>();
        otherOpponents.addAll(opponents);
        otherOpponents.remove(0);
        return CollectionsUtils.makeStringFromUsersFullNames(otherOpponents);
    }

    @Override
    public void onStop() {
        super.onStop();
        if (conversationFragmentCallback != null) {
            conversationFragmentCallback.removeOnChangeAudioDeviceListener(this);

        }
    }

    @Override
    protected void initButtonsListener() {
        super.initButtonsListener();
        audioSwitchToggleButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                SharedPrefsHelper.getInstance().save(SPEAKER_ENABLED, isChecked);
                if (conversationFragmentCallback != null) {
                    conversationFragmentCallback.onSwitchAudio();
                }
            }
        });
    }

    @Override
    protected void actionButtonsEnabled(boolean enabled) {
        super.actionButtonsEnabled(enabled);
        audioSwitchToggleButton.setActivated(enabled);
    }

    @Override
    int getFragmentLayout() {
        return R.layout.fragment_audio_conversation;
    }

    @Override
    public void onOpponentsListUpdated(ArrayList<QBUser> newUsers) {
        super.onOpponentsListUpdated(newUsers);
        firstOpponentNameTextView.setText(opponents.get(0).getFullName());
        otherOpponentsTextView.setText(getOtherOpponentsNames());
    }

    @Override
    public void onCallTimeUpdate(String time) {
        timerCallText.setText(time);
    }

    @Override
    public void audioDeviceChanged(AppRTCAudioManager.AudioDevice newAudioDevice) {
        audioSwitchToggleButton.setChecked(newAudioDevice != AppRTCAudioManager.AudioDevice.SPEAKER_PHONE);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

    }
}