package com.TuDime.ui.adapter;

import android.content.Context;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.quickblox.users.model.QBUser;
import com.quickblox.videochat.webrtc.QBRTCClient;
import com.quickblox.videochat.webrtc.QBRTCSession;
import com.quickblox.videochat.webrtc.QBRTCTypes;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import com.TuDime.R;
import com.TuDime.db.QbUsersDbManager;
import com.TuDime.ui.Model.Call_model;
import com.TuDime.ui.activity.CallActivity;
import com.TuDime.utils.PushNotificationSender;
import com.TuDime.utils.WebRtcSessionManager;
import com.TuDime.utils.qb.QbUsersHolder;

import static com.quickblox.videochat.webrtc.BaseClient.TAG;

public class Call_Adapter extends RecyclerView.Adapter<Call_Adapter.ViewHolder> {

    private ArrayList<Call_model> mData;
    private LayoutInflater mInflater;
    Context mContext;
    // data is passed into the constructor
    public Call_Adapter(Context context, ArrayList<Call_model> data) {
        this.mInflater = LayoutInflater.from(context);
        this.mData = data;
        this.mContext=context;
    }

    // inflates the row layout from xml when needed
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.child_call_view, parent, false);
        return new ViewHolder(view);
    }

    // binds the data to the TextView in each row
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.calling_date.setSelected(true);

      try{
          holder.caller_name.setText(mData.get(position).getDB_CALL_RECIPIENTNAME());
          holder.call_status.setText(mData.get(position).getCall_status());
          holder.calling_date.setText(convertDate(mData.get(position).getDB_CALL_START_TIME(),"dd/MM/yyyy hh:mm:ss aaa"));
          if (mData.get(position).getDB_CALL_TYPE().equalsIgnoreCase("video"))
          {
              if (mData.get(position).getCall_status().equalsIgnoreCase("Missed Call"))
              { holder.call_optiion_ic.setBackground(mContext.getResources().getDrawable(R.drawable.miss_call_video));
              }else
              {holder.call_optiion_ic.setBackground(mContext.getResources().getDrawable(R.drawable.video_icon)); }

          }else
          {
              if (mData.get(position).getCall_status().equalsIgnoreCase("Missed Call"))
              { holder.call_optiion_ic.setBackground(mContext.getResources().getDrawable(R.drawable.miss_call_audio));
              }else
              {holder.call_optiion_ic.setBackground(mContext.getResources().getDrawable(R.drawable.icon_call)); }
          }
          QBUser user = QbUsersHolder.getInstance().getUserById(Integer.valueOf(mData.get(position).getDB_CALL_RECIPIENTID()));
          try{
              Picasso.get()
                      .load(user.getWebsite())
                      .placeholder(R.drawable.default_user_image)
                      .into( holder.profile_picture);
          }catch (Exception e)
          {
              holder.profile_picture.setBackground(mContext.getResources().getDrawable(R.drawable.default_user_image));
          }
      }catch (Exception e)
      {

      }
        holder.tap_to_call.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {

                if (mData.get(position).getDB_CALL_TYPE().equalsIgnoreCase("video"))
                {  try{
                    startCall(true,position);
                }catch (Exception e){}
                }else
                { try{
                    startCall(false,position);
                }catch (Exception e){}
                }

            }
        });

    }

    private void startCall(boolean isVideoCall,int position)
    {
        QbUsersDbManager dbManager = QbUsersDbManager.getInstance(mContext);
        ArrayList<Integer> opponentsList = new ArrayList<>();
        opponentsList.add(Integer.valueOf(mData.get(position).getDB_CALL_RECIPIENTID()));
        QBRTCTypes.QBConferenceType conferenceType = isVideoCall
                ? QBRTCTypes.QBConferenceType.QB_CONFERENCE_TYPE_VIDEO
                : QBRTCTypes.QBConferenceType.QB_CONFERENCE_TYPE_AUDIO;
        Log.d(TAG, "conferenceType = " + conferenceType);
        QBRTCSession newQbRtcSession = QBRTCClient.getInstance(mContext).createNewSessionWithOpponents(opponentsList, conferenceType);
        WebRtcSessionManager.getInstance(mContext).setCurrentSession(newQbRtcSession);
        PushNotificationSender.sendPushMessage(opponentsList, mData.get(position).getDB_CALL_RECIPIENTNAME());
        CallActivity.start(mContext, false);
    }

    // total number of rows
    @Override
    public int getItemCount() {
        return mData.size();
    }
    public static String convertDate(String dateInMilliseconds,String dateFormat) {
        return DateFormat.format(dateFormat, Long.parseLong(dateInMilliseconds)).toString();
    }

    public void setFilter(ArrayList<Call_model> filteredDataList)
    {
        this.mData = filteredDataList;
        notifyDataSetChanged();
    }

    // stores and recycles views as they are scrolled off screen
    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView caller_name,calling_date,call_status;
        ImageView profile_picture,call_optiion_ic;
        LinearLayout tap_to_call;
        ViewHolder(View itemView) {
            super(itemView);
            caller_name = itemView.findViewById(R.id.caller_name);
            calling_date = itemView.findViewById(R.id.calling_date);
            call_status = itemView.findViewById(R.id.call_status);
            profile_picture = itemView.findViewById(R.id.profile_picture);
            call_optiion_ic = itemView.findViewById(R.id.call_optiion_ic);
            tap_to_call = itemView.findViewById(R.id.tap_to_call);

        }


        // convenience method for getting data at click position
        String getItem(int id) {
            return String.valueOf(mData.get(id));
        }


// parent activity will implement this method to respond to click events

    }
}