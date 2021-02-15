package com.TuDime.ui.activity;

import androidx.annotation.Nullable;
import androidx.core.view.ViewCompat;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.android.volley.VolleyError;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import com.TuDime.R;

/* renamed from: com.tudime.ui.fragment.AddSignatureEcards */
public class AddSignatureEcards extends BaseActivity implements View.OnClickListener {
    private ImageView mClear;
    /* access modifiers changed from: private */
    public ImageView mSave;
    /* access modifiers changed from: private */
    public LinearLayout my_signature;
    Signature signature;

    @Override
    public void ErrorResponse(VolleyError error, int requestCode, JSONObject networkresponse) {

    }

    @Override
    public void SuccessResponse(JSONObject response, int requestCode) {

    }

    @Override
    public void SuccessResponseArray(JSONArray response, int requestCode) {

    }

    @Override
    public void SuccessResponseRaw(String response, int requestCode) {

    }

    /* renamed from: com.tudime.ui.fragment.AddSignatureEcards$Signature */
    public class Signature extends View {
        static final float HALF_STROKE_WIDTH = 8.5f;
        static final float STROKE_WIDTH = 17.0f;
        final RectF dirtyRect = new RectF();
        float lastTouchX;
        float lastTouchY;
        Paint paint = new Paint();
        Path path = new Path();

        public Signature(Context context, AttributeSet attrs) {
            super(context, attrs);
            this.paint.setAntiAlias(true);
            this.paint.setColor(ViewCompat.MEASURED_STATE_MASK);
            this.paint.setStyle(Paint.Style.STROKE);
            this.paint.setStrokeJoin(Paint.Join.ROUND);
            this.paint.setStrokeWidth(STROKE_WIDTH);
        }

        public void clear() {
            this.path.reset();
            invalidate();
            AddSignatureEcards.this.mSave.setEnabled(false);
        }

        public void save() {
            Bitmap returnedBitmap = Bitmap.createBitmap(AddSignatureEcards.this.my_signature.getWidth(), AddSignatureEcards.this.my_signature.getHeight(), Bitmap.Config.ARGB_8888);
            Canvas canvas = new Canvas(returnedBitmap);
            Drawable bgDrawable = AddSignatureEcards.this.my_signature.getBackground();
            if (bgDrawable != null) {
                bgDrawable.draw(canvas);
            } else {
                canvas.drawColor(getResources().getColor(R.color.trans_ecards_sig));
            }
            AddSignatureEcards.this.my_signature.draw(canvas);
            ByteArrayOutputStream bs = new ByteArrayOutputStream();
            returnedBitmap.compress(Bitmap.CompressFormat.PNG, 50, bs);
            Intent intent = new Intent();
            intent.putExtra("byteArray", bs.toByteArray());
            AddSignatureEcards.this.setResult(-1, intent);
            AddSignatureEcards.this.finish();
        }

        /* access modifiers changed from: protected */
        public void onDraw(Canvas canvas) {
            canvas.drawPath(this.path, this.paint);
        }

        public boolean onTouchEvent(MotionEvent event) {
            float eventX = event.getX();
            float eventY = event.getY();
            AddSignatureEcards.this.mSave.setEnabled(true);
            switch (event.getAction()) {
                case 0:
                    this.path.moveTo(eventX, eventY);
                    this.lastTouchX = eventX;
                    this.lastTouchY = eventY;
                    return true;
                case 1:
                case 2:
                    resetDirtyRect(eventX, eventY);
                    int historySize = event.getHistorySize();
                    for (int i = 0; i < historySize; i++) {
                        this.path.lineTo(event.getHistoricalX(i), event.getHistoricalY(i));
                    }
                    this.path.lineTo(eventX, eventY);
                    break;
            }
            invalidate((int) (this.dirtyRect.left - HALF_STROKE_WIDTH), (int) (this.dirtyRect.top - HALF_STROKE_WIDTH), (int) (this.dirtyRect.right + HALF_STROKE_WIDTH), (int) (this.dirtyRect.bottom + HALF_STROKE_WIDTH));
            this.lastTouchX = eventX;
            this.lastTouchY = eventY;
            return true;
        }

        private void resetDirtyRect(float eventX, float eventY) {
            this.dirtyRect.left = Math.min(this.lastTouchX, eventX);
            this.dirtyRect.right = Math.max(this.lastTouchX, eventX);
            this.dirtyRect.top = Math.min(this.lastTouchY, eventY);
            this.dirtyRect.bottom = Math.max(this.lastTouchY, eventY);
        }
    }

    /* access modifiers changed from: protected */
    @Nullable
    public void onCreate(Bundle arg0) {
        super.onCreate(arg0);
        setContentView((int) R.layout.activity_add_signature_ecards);
        hideActionbar();
        this.mSave = (ImageView) findViewById(R.id.save);
        this.mClear = (ImageView) findViewById(R.id.clean);
        this.mSave.setEnabled(false);
        this.my_signature = (LinearLayout) findViewById(R.id.my_signature);
        this.signature = new Signature(this, null);
        this.my_signature.addView(this.signature);
        findViewById(R.id.back).setOnClickListener(this);
        findViewById(R.id.clean).setOnClickListener(this);
        findViewById(R.id.save).setOnClickListener(this);
    }

    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.back /*2131624579*/:
                finish();
                return;
            case R.id.clean /*2131624580*/:
                this.signature.clear();
                return;
            case R.id.save /*2131624581*/:
                this.signature.save();
                return;
            default:
                return;
        }
    }
}