package obj.quickblox.sample.chat.java.EmojiKeyBoard;


import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import java.util.List;

import io.github.rockerhieu.emojicon.EmojiconTextView;
import io.github.rockerhieu.emojicon.emoji.Emojicon;
import obj.quickblox.sample.chat.java.R;

/**
 * @author Hieu Rocker (rockerhieu@gmail.com)
 */
class EmojiAdapter extends ArrayAdapter<Emojicon> {
    private boolean mUseSystemDefault = false;

    EmojiView.onEmojiClickListener onEmojiClickListener;

    public EmojiAdapter(Context context, Emojicon[] data, EmojiView.onEmojiClickListener onEmojiClickListener) {
        super(context, R.layout.emojicon_item, data);
        this.onEmojiClickListener = onEmojiClickListener;
    }

    public EmojiAdapter(Context context, List<Emojicon> data) {
        super(context, R.layout.emojicon_item, data);
        mUseSystemDefault = false;
    }

    public EmojiAdapter(Context context, List<Emojicon> data, boolean useSystemDefault) {
        super(context, R.layout.emojicon_item, data);
        mUseSystemDefault = useSystemDefault;
    }

    public EmojiAdapter(Context context, Emojicon[] data) {
        super(context, R.layout.emojicon_item, data);
        mUseSystemDefault = false;
    }

    public EmojiAdapter(Context context, Emojicon[] data, boolean useSystemDefault) {
        super(context, R.layout.emojicon_item, data);
        mUseSystemDefault = useSystemDefault;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View v = convertView;
        if (v == null) {
            v = View.inflate(getContext(), R.layout.emojicon_item, null);
            ViewHolder holder = new ViewHolder();
            holder.icon = (EmojiconTextView) v.findViewById(R.id.emojicon_icon);
            holder.icon.setUseSystemDefault(mUseSystemDefault);
            v.setTag(holder);
        }
        final Emojicon emoji = getItem(position);
        ViewHolder holder = (ViewHolder) v.getTag();
        holder.icon.setText(emoji.getEmoji());
        holder.icon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onEmojiClickListener.onEmojiSelected(emoji);
            }
        });
        return v;
    }

    class ViewHolder {
        EmojiconTextView icon;
    }
}