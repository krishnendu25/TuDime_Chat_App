package com.TuDime.ui.adapter;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import com.TuDime.R;

public class FaqAdapter extends BaseExpandableListAdapter {

    private Context _context;
    private List<String> _listDataHeader;	// header
    // titles
    // child data in format of header title, child title
    private HashMap<String, ArrayList<String>> _listDataChild;

    public FaqAdapter(Context context, List<String> listDataHeader, HashMap<String, ArrayList<String>> listChildData) {
        this._context = context;
        this._listDataHeader = listDataHeader;
        this._listDataChild = listChildData;
    }

    @Override
    public Object getChild(int groupPosition, int childPosititon) {
        return this._listDataChild.get(this._listDataHeader.get(groupPosition)).get(childPosititon);
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }

    @Override
    public View getChildView(int groupPosition, final int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {

        final String childText = (String) getChild(groupPosition, childPosition);

        if (convertView == null) {
            LayoutInflater infalInflater = (LayoutInflater) this._context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = infalInflater.inflate(R.layout.list_row_faq_child, null);
        }
        TextView tvChild = (TextView) convertView.findViewById(R.id.tv_child);
        // tvChild.setTypeface(WidgetProperties
        // .setTextTypefaceHelvetica45(_context));
        tvChild.setText(childText);

        String text = childText;
        String subText = "www.1800234ride.com";
        if (text.contains(subText)) {
            int indexOfUserAgreement = text.indexOf(subText);
            int endOfUserAgreement = subText.length() + indexOfUserAgreement;
            Spannable ss = new SpannableString(text);
            ss.setSpan(new MyClickableSpan(), indexOfUserAgreement, endOfUserAgreement, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            tvChild.setText(ss);
            tvChild.setMovementMethod(LinkMovementMethod.getInstance());
        }

        String subtext2 = "1-800-234-7433";
        if (text.contains(subtext2)) {
            int indexOfUserAgreement = text.indexOf(subtext2);
            int endOfUserAgreement = subtext2.length() + indexOfUserAgreement;
            Spannable ss = new SpannableString(text);
            ss.setSpan(new MyClickableTelephone(), indexOfUserAgreement, endOfUserAgreement, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            tvChild.setText(ss);
            tvChild.setMovementMethod(LinkMovementMethod.getInstance());
        }
        return convertView;
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        return this._listDataChild.get(this._listDataHeader.get(groupPosition)).size();
    }

    @Override
    public Object getGroup(int groupPosition) {
        return this._listDataHeader.get(groupPosition);
    }

    @Override
    public int getGroupCount() {
        return this._listDataHeader.size();
    }

    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
        String headerTitle = (String) getGroup(groupPosition);
        if (convertView == null) {
            LayoutInflater infalInflater = (LayoutInflater) this._context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = infalInflater.inflate(R.layout.list_row_faq_group, null);
        }

        TextView tvHeader = (TextView) convertView.findViewById(R.id.tv_header);
        // tvHeader.setTypeface(WidgetProperties
        // .setTextTypefaceHelvetica45(_context));
        tvHeader.setText(headerTitle);

        ImageView ivListHeader = (ImageView) convertView.findViewById(R.id.iv_list_header);

        if (isExpanded) {
            ivListHeader.setImageResource(R.drawable.faq_minus_icon);
        } else {
            ivListHeader.setImageResource(R.drawable.faq_add_icon);

        }

        return convertView;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }

    class MyClickableSpan extends ClickableSpan { // clickable span

        public void onClick(View textView) {
            Intent i = new Intent(Intent.ACTION_VIEW);
            i.setData(Uri.parse("http://www.1800234ride.com"));
            _context.startActivity(i);
        }

        @Override
        public void updateDrawState(TextPaint ds) {
            ds.setColor(_context.getResources().getColor(android.R.color.holo_blue_light));
            ds.setUnderlineText(true);
        }
    }

    class MyClickableTelephone extends ClickableSpan { // clickable span

        public void onClick(View textView) {

            AlertDialog.Builder alertDialog = new AlertDialog.Builder(_context);

            alertDialog.setTitle(""+_context.getString(R.string.call));

            alertDialog.setMessage("1-800-234-7433");

            alertDialog.setPositiveButton(""+_context.getString(R.string.call), new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    Intent callIntent = new Intent(Intent.ACTION_CALL);
                    callIntent.setData(Uri.parse("tel:18002347433"));
                    _context.startActivity(callIntent);
                }
            });

            alertDialog.setNegativeButton(""+_context.getString(R.string.cancel), new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    dialog.cancel();
                }
            });

            alertDialog.show();
        }

        @Override
        public void updateDrawState(TextPaint ds) {
            ds.setColor(_context.getResources().getColor(android.R.color.holo_blue_light));
            ds.setUnderlineText(true);
        }
    }
}
