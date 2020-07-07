package obj.quickblox.sample.chat.java.ui.adapter;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import obj.quickblox.sample.chat.java.R;
import obj.quickblox.sample.chat.java.ui.Model.Calender_Model;
import obj.quickblox.sample.chat.java.ui.Model.Contact_Model;
import obj.quickblox.sample.chat.java.ui.adapter.listeners.Clickback;
import obj.quickblox.sample.chat.java.ui.adapter.listeners.SetclickCallback;

public class Event_Show_adapter extends BaseAdapter {
    ArrayList<Calender_Model> show_event_list;
    Context context;
    Clickback clickback;
    public Event_Show_adapter(Context context, ArrayList<Calender_Model> show_event_list) {
        this.context = context;
        this.show_event_list = show_event_list;
        this.clickback = (Clickback) context;
    }


    @Override
    public int getCount() {
        return show_event_list.size();
    }

    @Override
    public Object getItem(int i) {
        return null;
    }

    @Override
    public long getItemId(int id) {
        return (long) id;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {


       ViewHolder holder;
        if (convertView == null) {

            convertView = LayoutInflater.from(context).inflate(R.layout.event_show_layout, parent, false);
            holder = new ViewHolder();
            holder.mTitle = (TextView) convertView.findViewById(R.id.saved_event_title_textView);
            holder.mAbout = (TextView) convertView.findViewById(R.id.saved_event_about_textView);
            holder.mTime = (TextView) convertView.findViewById(R.id.saved_event_time_textView);
            holder.mImageView = (ImageView) convertView.findViewById(R.id.saved_event_imageView);
            holder.mDate = (TextView) convertView.findViewById(R.id.saved_event_date_textView);
            convertView.setTag(holder);
        }else {

            holder = (ViewHolder) convertView.getTag();
        }


        holder.mTitle.setText(show_event_list.get(position).getDB_EVENT_NAME());
        holder.mAbout.setText(show_event_list.get(position).getDB_EVENT_DESC());
        holder.mDate.setText(show_event_list.get(position).getDB_EVENT_DATE());
        holder.mTime.setText(show_event_list.get(position).getDB_EVENT_TIME());
        holder.mImageView.setImageDrawable(context.getResources().getDrawable(R.drawable.calendar));

        convertView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                String[] options = { context.getString(R.string.delete_event)};
                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setItems(options  , new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        clickback.delete_position(position);
                    }
                });
                builder.create().show();
                return true;
            }
        });
        return convertView;
    }

    private static class ViewHolder {
        TextView mTitle , mAbout , mTime , mDate;
        ImageView mImageView;
        View mDivider;
    }
}
