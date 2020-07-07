package obj.quickblox.sample.chat.java.ui.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.quickblox.chat.model.QBChatDialog;
import com.quickblox.chat.model.QBDialogType;
import com.quickblox.core.QBEntityCallback;
import com.quickblox.core.exception.QBResponseException;
import com.quickblox.users.model.QBUser;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import obj.quickblox.sample.chat.java.R;
import obj.quickblox.sample.chat.java.db.QbUsersDbManager;
import obj.quickblox.sample.chat.java.ui.activity.Group_Details_View;
import obj.quickblox.sample.chat.java.utils.ResourceUtils;
import obj.quickblox.sample.chat.java.utils.qb.QbDialogUtils;
import obj.quickblox.sample.chat.java.utils.qb.QbUsersHolder;

/**
 * Created by KRISHNENDU MANNA on 27,May,2020
 */
public class Groupe_Participants_Adapter extends BaseAdapter {
    private Context context;
    private List<Integer> dialogs;
    public Groupe_Participants_Adapter(Context scontext, List<Integer> occupants)
    {
        this.dialogs = occupants;
        this.context=scontext;
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.child_group_chat_people, parent, false);

            holder = new ViewHolder();
            holder.Perticipants_name = (TextView) convertView.findViewById(R.id.Perticipants_name);
            holder.my_profile_iv = (ImageView) convertView.findViewById(R.id.my_profile_iv);

            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        QBUser participant = QbUsersHolder.getInstance().getUserById(Integer.valueOf(dialogs.get(position)));
        try{
             String Name = participant.getFullName();
            holder.Perticipants_name.setText(Name);
        }catch (Exception e)
        {

        }


        try
        {
            Picasso.get().load(participant.getWebsite()).placeholder(R.drawable.default_user_image).into( holder.my_profile_iv);
        }catch (Exception e)
        {
        }

        return convertView;
    }

    @Override
    public Integer getItem(int position) {
        return dialogs.get(position);
    }

    @Override
    public long getItemId(int position) {
        return dialogs.get(position).longValue();
    }


    @Override
    public int getCount() { return dialogs.size(); }


    private static class ViewHolder {
        ImageView my_profile_iv;
        TextView Perticipants_name;
    }
}