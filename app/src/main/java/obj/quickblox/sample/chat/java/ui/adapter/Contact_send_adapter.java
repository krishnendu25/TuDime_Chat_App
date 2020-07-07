package obj.quickblox.sample.chat.java.ui.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.List;

import obj.quickblox.sample.chat.java.R;
import obj.quickblox.sample.chat.java.ui.Callback.send_contact;
import obj.quickblox.sample.chat.java.ui.Model.Contact_Model;

public class Contact_send_adapter extends BaseAdapter
{

    List<Contact_Model> Local_Contact;
    Context context;
    send_contact send_contact;

    public Contact_send_adapter(Context context,List<Contact_Model> Local_Contact) {
        this.context = context;
        this.Local_Contact = Local_Contact;
        send_contact = (send_contact) context;
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

            convertView = LayoutInflater.from(context).inflate(R.layout.child_sent_contact, parent, false);
            holder = new ViewHolder();


            holder.Select_Contact_view=convertView.findViewById(R.id.Select_Contact_view);
            holder.Contact_name=convertView.findViewById(R.id.Contact_name);
            holder.Contact_no=convertView.findViewById(R.id.Contact_no);
            convertView.setTag(holder);
        }else {

            holder = (ViewHolder) convertView.getTag();
        }

        holder.Contact_name.setText(Local_Contact.get(position).getContact_Name());
        holder.Contact_no.setText(Local_Contact.get(position).getContact_Number());
        holder.Select_Contact_view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                send_contact.Send_Contact(Local_Contact.get(position).getContact_Name(),Local_Contact.get(position).getContact_Number());
            }
        });

        return convertView;
    }
    private static class ViewHolder {
        ViewGroup rootLayout;
        LinearLayout Select_Contact_view;
        TextView Contact_name,Contact_no;
    }
}
