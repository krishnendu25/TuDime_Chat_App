package obj.quickblox.sample.chat.java.ui.adapter;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.quickblox.chat.model.QBChatDialog;

import java.util.List;

import obj.quickblox.sample.chat.java.R;
import obj.quickblox.sample.chat.java.ui.Model.Contact_Model;

public class Show_All_Contact_Adapter extends BaseAdapter
{

    List<Contact_Model> Local_Contact;
    Context context;


    public Show_All_Contact_Adapter(Context context,List<Contact_Model> Local_Contact) {
        this.context = context;
        this.Local_Contact = Local_Contact;
    }


    @Override
    public int getCount() {
        return Local_Contact.size();
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
    public View getView(int position, View convertView, ViewGroup parent)
    {
            ViewHolder holder;
        if (convertView == null) {

            convertView = LayoutInflater.from(context).inflate(R.layout.list_of_invite_people, parent, false);
            holder = new ViewHolder();

            holder.image_user=convertView.findViewById(R.id.image_user);
            holder.rootLayout=convertView.findViewById(R.id.root_layout);
            holder.text_user_login=convertView.findViewById(R.id.text_user_login);
            holder.invite_people=convertView.findViewById(R.id.invite_people);
            convertView.setTag(holder);
        }else {

            holder = (ViewHolder) convertView.getTag();
        }


        holder.text_user_login.setText(Local_Contact.get(position).getContact_Name());

        holder.invite_people.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {

                Intent txtIntent = new Intent(android.content.Intent.ACTION_SEND);
                txtIntent .setType("text/plain");
                txtIntent .putExtra(android.content.Intent.EXTRA_SUBJECT, "TuDime So Much More Than A Regular Chat App !");
                txtIntent .putExtra(android.content.Intent.EXTRA_TEXT, "" + context.getString(R.string.check_out_tudime_for_your_smartphone) + " (https://www.TuDime.com)");
                context.startActivity(Intent.createChooser(txtIntent ,"Share"));


            }
        });

        return convertView;
    }

    public void setFilter(List<Contact_Model> filter)
    {
        this.Local_Contact =filter;
        notifyDataSetChanged();

    }

    private static class ViewHolder {
        ViewGroup rootLayout;
        ImageView image_user;
        TextView text_user_login,invite_people;
    }
}
