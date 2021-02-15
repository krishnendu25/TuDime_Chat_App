package com.TuDime.util;


import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.DialogFragment;
import com.TuDime.R;
import com.TuDime.ui.Callback.OnButtonClicked;

public class ImageOnlyOptionsDialog extends DialogFragment implements View.OnClickListener {

    private OnButtonClicked onButtonClicked;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // remove title and frame from dialog-fragment
        setStyle(STYLE_NO_TITLE, 0);
    }

    public void onActivityCreated(Bundle arg0) {
        super.onActivityCreated(arg0);
        getDialog().getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dialog_photo_edit_options, container, false);
        getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(0));

        view.findViewById(R.id.btnCamera).setOnClickListener(this);
        view.findViewById(R.id.btnGallery).setOnClickListener(this);
        view.findViewById(R.id.btnCancel).setOnClickListener(this);

        view.findViewById(R.id.btnGalleryVideo).setVisibility(View.GONE);
        view.findViewById(R.id.btnCamcorder).setVisibility(View.GONE);
        return view;
    }

    @Override
    public void onClick(View pClickSource) {
        switch (pClickSource.getId()) {
            case R.id.btnGallery:
                onButtonClicked.onButtonCLick(R.id.btnGallery);
                dismiss();
                break;
            case R.id.btnCamera:
                onButtonClicked.onButtonCLick(R.id.btnCamera);
                dismiss();
                break;
            case R.id.btnGalleryVideo:
                onButtonClicked.onButtonCLick(R.id.btnGalleryVideo);
                dismiss();
                break;
            case R.id.btnCamcorder:
                onButtonClicked.onButtonCLick(R.id.btnCamcorder);
                dismiss();
                break;
            case R.id.btnCancel:
                dismiss();
                break;
        }
    }

    public void setonButtonClickListener(OnButtonClicked onButtonClicked) {
        this.onButtonClicked = onButtonClicked;
    }

}