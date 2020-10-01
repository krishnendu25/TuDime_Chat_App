package obj.quickblox.sample.chat.java.ui.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.quickblox.chat.QBChatService;

import obj.quickblox.sample.chat.java.R;
import obj.quickblox.sample.chat.java.ui.Callback.Go_to_Chat;
import obj.quickblox.sample.chat.java.utils.ResourceUtils;

import com.quickblox.users.model.QBUser;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class UsersAdapter extends BaseAdapter {
    public static final String EXTRA_QB_USERS = "qb_users";
    public static final String EXTRA_CHAT_NAME = "chat_name";
    public static List<QBUser> selectedUsers;
    protected List<QBUser> userList;
    protected QBUser currentUser;
    Go_to_Chat go_to_chat;
    private Context context;
    private SelectedItemsCountChangedListener selectedItemsCountChangedListener;

    public UsersAdapter(Context context, List<QBUser> users) {
        this.context = context;
        currentUser = QBChatService.getInstance().getUser();
        userList = users;
        this.selectedUsers = new ArrayList<>();
       /* go_to_chat = (Go_to_Chat) context;*/
        addCurrentUserToUserList();
    }

    private void addCurrentUserToUserList() {
        if (currentUser != null) {
            if (!userList.contains(currentUser)) {
                userList.add(currentUser);
            }
        }
    }

    public void addUserToUserList(QBUser user) {
        if (!userList.contains(user)) {
            userList.add(user);
        }
    }

    public void setSelectedItemsCountsChangedListener(SelectedItemsCountChangedListener selectedItemsCountChanged) {
        if (selectedItemsCountChanged != null) {
            this.selectedItemsCountChangedListener = selectedItemsCountChanged;
        }
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        QBUser user = getItem(position);

        ViewHolder holder;
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.list_item_user, parent, false);
            holder = new ViewHolder();
            holder.rootLayout = convertView.findViewById(R.id.root_layout);
            holder.userImageView = (ImageView) convertView.findViewById(R.id.image_user);
            holder.loginTextView = (TextView) convertView.findViewById(R.id.text_user_login);
            holder.text_loginid= (TextView) convertView.findViewById(R.id.text_loginid);
            holder.userCheckBox = (CheckBox) convertView.findViewById(R.id.checkbox_user);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        holder.loginTextView.setSelected(true);
        if (isUserMe(user)) {
          holder.loginTextView.setText(user.getFullName()+"   "+context.getString(R.string.placeholder_username_you));
            holder.text_loginid.setText(user.getLogin());
        } else {
            holder.loginTextView.setText(user.getFullName());
            holder.text_loginid.setText(user.getLogin());
        }

        if (isAvailableForSelection(user)) {
            holder.loginTextView.setTextColor(ResourceUtils.getColor(R.color.text_color_black));
        } else {
            holder.loginTextView.setTextColor(ResourceUtils.getColor(R.color.text_color_dark_grey));
            holder.text_loginid.setTextColor(ResourceUtils.getColor(R.color.text_color_dark_grey));
        }

        try{
            Picasso.get().load(user.getWebsite()).placeholder(R.drawable.default_user_image).into(holder.userImageView);
        }catch (Exception e)
        {
            holder.userImageView.setBackgroundDrawable(context.getResources().getDrawable(R.drawable.default_user_image));
        }





        holder.userCheckBox.setVisibility(View.GONE);

        holder.rootLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
              /*  Intent result = new Intent();
                ArrayList<QBUser> selectedUsers = new ArrayList<>(getSelectedUsers());
                result.putExtra(EXTRA_QB_USERS, selectedUsers);
                ((Activity) context).setResult(RESULT_OK, result);
                ArrayList<QBUser> selectedUsers = new ArrayList<>(getSelectedUsers());
                go_to_chat.passtochat(selectedUsers);*/
            }
        });

        return convertView;
    }

    public Set<QBUser> getSelectedUsers() {
        return (Set<QBUser>) selectedUsers;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getCount() {
        return userList.size();
    }

    @Override
    public QBUser getItem(int position) {
        return userList.get(position);
    }

    private boolean isUserMe(QBUser user) {
        return currentUser != null && currentUser.getId().equals(user.getId());
    }

    protected boolean isAvailableForSelection(QBUser user) {
        return currentUser == null || !currentUser.getId().equals(user.getId());
    }

    public void updateUsersList(List<QBUser> usersList) {
        this.selectedUsers = usersList;
        notifyDataSetChanged();
    }

    public void setFilter(ArrayList<QBUser> filter)
    {
        userList = filter;
        notifyDataSetChanged();
    }

    /* private void toggleSelection(QBUser qbUser) {
         if (selectedUsers.contains(qbUser)){
             selectedUsers.remove(qbUser);
         } else {
             selectedUsers.add(qbUser);
         }
         notifyDataSetChanged();
     }*/
    public interface SelectedItemsCountChangedListener {
        void onCountSelectedItemsChanged(Integer count);
    }

    protected static class ViewHolder {
        ImageView userImageView;
        TextView loginTextView,text_loginid;
        CheckBox userCheckBox;
        LinearLayout rootLayout;
    }

}