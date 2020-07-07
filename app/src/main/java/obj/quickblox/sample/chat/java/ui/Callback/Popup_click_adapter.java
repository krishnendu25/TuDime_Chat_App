package obj.quickblox.sample.chat.java.ui.Callback;

import android.view.View;

import com.quickblox.chat.model.QBChatDialog;

public interface Popup_click_adapter {

    void onLong_Click(View view, int Position, QBChatDialog selectedDialog,View fview);
}
