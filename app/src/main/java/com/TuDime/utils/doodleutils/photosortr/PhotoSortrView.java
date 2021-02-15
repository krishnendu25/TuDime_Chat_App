/**
 * Created by Donny Dominic on 27-01-2016.
 */
package com.TuDime.utils.doodleutils.photosortr;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.Path;
import android.graphics.Point;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.DragEvent;
import android.view.MotionEvent;
import android.view.View;
import com.TuDime.doodle.utils.preferences.DoodlePreferences;

import java.util.ArrayList;
import java.util.HashMap;
import com.TuDime.R;
import com.TuDime.utils.doodleutils.controller.MultiTouchController;
import com.TuDime.utils.views.DoodleActivity;


public class PhotoSortrView extends View implements MultiTouchController.MultiTouchObjectCanvas<PhotoSortrView.Img> {
    public static ArrayList<Img> DOODLE_IMAGE = new ArrayList<>();
    public static int[] IMAGES = new int[0];
    private static final int UI_MODE_ANISOTROPIC_SCALE = 2;
    private static final int UI_MODE_ROTATE = 1;
    public static String color = "#00000000";
    public static ArrayList<Bitmap> listBitmap = new ArrayList<>();
    public static ArrayList<Integer> listImages = new ArrayList<>();
    public static Bitmap mBitmap;
    public static Canvas mCanvas;
    /* access modifiers changed from: private */
    public static int mUIMode = 1;
    private updateTabBarIcon UpdateTabBarIcon;
    private Bitmap bitmap;
    private Bitmap bitmapBackup;
    private Canvas bitmapBackupCanvas;
    private Canvas bitmapCanvas;
    private MultiTouchController.PointInfo currTouchPoint;
    private float density;
    private int eraserSize;

    /* renamed from: h */
    private int f274h;
    private Img imgObject;
    public boolean isPointerDown;
    public boolean isPointerUp;
    private Paint mBitmapPaint;
    private Paint mLinePaintTouchPointCircle;
    private Paint mPaintBrush;
    private Paint mPaintBrushErase;
    private boolean mShowDebugInfo;
    private DoodleActivity mainActivity;
    public DRAW_MODE mode;
    private MultiTouchController<Img> multiTouchController;
    private Path path;
    private HashMap<Integer, Path> pathMap;
    private int pensilSize;
    private HashMap<Integer, Point> previousPointMap;

    /* renamed from: w */
    private int f275w;

    /* renamed from: x */
    private float f276x;

    /* renamed from: y */
    private float f277y;

    public enum DRAW_MODE {
        DRAW,
        ERASE
    }

    public static class Img {
        private static final float SCREEN_MARGIN = 100.0f;
        private float angle;
        private float centerX;
        private float centerY;
        private int displayHeight;
        private int displayWidth;
        private Drawable drawable;
        private boolean firstLoad;
        private int height;
        private boolean isEditable = false;
        private float maxX;
        private float maxY;
        private float minX;
        private float minY;
        private int resId;
        private float scaleX;
        private float scaleY;
        private int width;

        public Img(int resId2, Resources res, boolean isEditable2) {
            this.resId = resId2;
            this.firstLoad = true;
            this.isEditable = isEditable2;
            getMetrics(res);
        }

        public Img(Drawable drawable2, Resources res, boolean isEditable2) {
            this.drawable = drawable2;
            this.firstLoad = true;
            this.isEditable = isEditable2;
            getMetrics(res);
        }

        private void getMetrics(Resources res) {
            int min;
            int max;
            DisplayMetrics metrics = res.getDisplayMetrics();
            if (res.getConfiguration().orientation == 2) {
                min = Math.max(metrics.widthPixels, metrics.heightPixels);
            } else {
                min = Math.min(metrics.widthPixels, metrics.heightPixels);
            }
            this.displayWidth = min;
            if (res.getConfiguration().orientation == 2) {
                max = Math.min(metrics.widthPixels, metrics.heightPixels);
            } else {
                max = Math.max(metrics.widthPixels, metrics.heightPixels);
            }
            this.displayHeight = max;
        }

        public void loadDrawable(Resources res) {
            float cx;
            float cy;
            float sx;
            float sy;
            getMetrics(res);
            this.drawable = this.drawable;
            this.width = this.drawable.getIntrinsicWidth();
            this.height = this.drawable.getIntrinsicHeight();
            if (this.firstLoad) {
                cx = SCREEN_MARGIN + ((float) (((double) (((float) this.displayWidth) - 200.0f)) * 0.52d));
                cy = SCREEN_MARGIN + ((float) (((double) (((float) this.displayHeight) - 200.0f)) * 0.22d));
                float sc = (float) ((((double) (((float) Math.max(this.displayWidth, this.displayHeight)) / ((float) Math.max(this.width, this.height)))) * 0.3d) + 0.2d);
                sy = sc;
                sx = sc;
                this.firstLoad = true;
            } else {
                cx = this.centerX;
                cy = this.centerY;
                sx = this.scaleX;
                sy = this.scaleY;
                if (this.maxX < SCREEN_MARGIN) {
                    cx = SCREEN_MARGIN;
                } else if (this.minX > ((float) this.displayWidth) - SCREEN_MARGIN) {
                    cx = ((float) this.displayWidth) - SCREEN_MARGIN;
                }
                if (this.maxY > SCREEN_MARGIN) {
                    cy = SCREEN_MARGIN;
                } else if (this.minY > ((float) this.displayHeight) - SCREEN_MARGIN) {
                    cy = ((float) this.displayHeight) - SCREEN_MARGIN;
                }
            }
            setPos(cx, cy, sx, sy, 0.0f);
        }

        public void load(Resources res) {
            float cx;
            float cy;
            float sx;
            float sy;
            getMetrics(res);
            this.drawable = res.getDrawable(this.resId);
            this.width = this.drawable.getIntrinsicWidth();
            this.height = this.drawable.getIntrinsicHeight();
            if (this.firstLoad) {
                cx = SCREEN_MARGIN + ((float) (((double) (((float) this.displayWidth) - 200.0f)) * 0.52d));
                cy = SCREEN_MARGIN + ((float) (((double) (((float) this.displayHeight) - 200.0f)) * 0.22d));
                float sc = (float) ((((double) (((float) Math.max(this.displayWidth, this.displayHeight)) / ((float) Math.max(this.width, this.height)))) * 0.3d) + 0.2d);
                sy = sc;
                sx = sc;
                this.firstLoad = true;
            } else {
                cx = this.centerX;
                cy = this.centerY;
                sx = this.scaleX;
                sy = this.scaleY;
                if (this.maxX < SCREEN_MARGIN) {
                    cx = SCREEN_MARGIN;
                } else if (this.minX > ((float) this.displayWidth) - SCREEN_MARGIN) {
                    cx = ((float) this.displayWidth) - SCREEN_MARGIN;
                }
                if (this.maxY > SCREEN_MARGIN) {
                    cy = SCREEN_MARGIN;
                } else if (this.minY > ((float) this.displayHeight) - SCREEN_MARGIN) {
                    cy = ((float) this.displayHeight) - SCREEN_MARGIN;
                }
            }
            setPos(cx, cy, sx, sy, 0.0f);
        }

        public void unload() {
            this.drawable = null;
        }

        public boolean setPos(MultiTouchController.PositionAndScale newImgPosAndScale) {
            float scale;
            float scale2;
            float xOff = newImgPosAndScale.getXOff();
            float yOff = newImgPosAndScale.getYOff();
            if ((PhotoSortrView.mUIMode & 2) != 0) {
                scale = newImgPosAndScale.getScaleX();
            } else {
                scale = newImgPosAndScale.getScale();
            }
            if ((PhotoSortrView.mUIMode & 2) != 0) {
                scale2 = newImgPosAndScale.getScaleY();
            } else {
                scale2 = newImgPosAndScale.getScale();
            }
            return setPos(xOff, yOff, scale, scale2, newImgPosAndScale.getAngle());
        }

        private boolean setPos(float centerX2, float centerY2, float scaleX2, float scaleY2, float angle2) {
            float ws = ((float) (this.width / 2)) * scaleX2;
            float hs = ((float) (this.height / 2)) * scaleY2;
            float newMinX = centerX2 - ws;
            float newMinY = centerY2 - hs;
            float newMaxX = centerX2 + ws;
            float newMaxY = centerY2 + hs;
            if (newMinX > ((float) this.displayWidth) - SCREEN_MARGIN || newMaxX < SCREEN_MARGIN || newMinY > ((float) this.displayHeight) - SCREEN_MARGIN || newMaxY < SCREEN_MARGIN) {
                return false;
            }
            this.centerX = centerX2;
            this.centerY = centerY2;
            this.scaleX = scaleX2;
            this.scaleY = scaleY2;
            this.angle = angle2;
            this.minX = newMinX;
            this.minY = newMinY;
            this.maxX = newMaxX;
            this.maxY = newMaxY;
            return true;
        }

        public boolean containsPoint(float scrnX, float scrnY) {
            return scrnX >= this.minX && scrnX <= this.maxX && scrnY >= this.minY && scrnY <= this.maxY;
        }

        public void draw(Canvas canvas) {
            canvas.save();
            float dx = (this.maxX + this.minX) / 2.0f;
            float dy = (this.maxY + this.minY) / 2.0f;
            this.drawable.setBounds((int) this.minX, (int) this.minY, (int) this.maxX, (int) this.maxY);
            canvas.translate(dx, dy);
            canvas.rotate((this.angle * 180.0f) / 3.1415927f);
            canvas.translate(-dx, -dy);
            this.drawable.draw(canvas);
            canvas.restore();
        }

        public Drawable getDrawable() {
            return this.drawable;
        }

        public int getWidth() {
            return this.width;
        }

        public int getHeight() {
            return this.height;
        }

        public float getCenterX() {
            return this.centerX;
        }

        public float getCenterY() {
            return this.centerY;
        }

        public float getScaleX() {
            return this.scaleX;
        }

        public float getScaleY() {
            return this.scaleY;
        }

        public float getAngle() {
            return this.angle;
        }

        public float getMinX() {
            return this.minX;
        }

        public float getMaxX() {
            return this.maxX;
        }

        public float getMinY() {
            return this.minY;
        }

        public float getMaxY() {
            return this.maxY;
        }

        public boolean getisEditiable() {
            return this.isEditable;
        }
    }

    public boolean getDraggableObjectStatus() {
        if (DoodleActivity.isPencilDoodle) {
            this.mode = DRAW_MODE.DRAW;
            color = DoodlePreferences.getInstance(getContext()).getDoodleDrawColor();
            initiatePensil();
            initiateEraser();
        } else {
            this.mode = DRAW_MODE.ERASE;
            color = "#00000000";
            initiatePensil();
            initiateEraser();
        }
        return DoodleActivity.isPencilDoodle;
    }

    public PhotoSortrView(Context context) {
        this(context, null);
        this.density = context.getResources().getDisplayMetrics().density;
        try{
            initiatePensil();
        }catch (Exception e)
        {
            e.printStackTrace();
        }

        initiateEraser();
    }

    public PhotoSortrView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public PhotoSortrView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        this.mPaintBrush = new Paint();
        this.path = new Path();
        this.f276x = 0.0f;
        this.f277y = 0.0f;
        this.pensilSize = 10;
        this.eraserSize = 10;
        this.mode = DRAW_MODE.ERASE;
        this.isPointerDown = false;
        this.isPointerUp = false;
        this.multiTouchController = new MultiTouchController<>(this);
        this.currTouchPoint = new MultiTouchController.PointInfo();
        this.mShowDebugInfo = true;
        this.mLinePaintTouchPointCircle = new Paint();
        init(context);
    }

    private void init(Context context) {
        Resources res = context.getResources();
        for (int i = 0; i < listImages.size(); i++) {
            DOODLE_IMAGE.add(new Img(((Integer) listImages.get(i)).intValue(), res, false));
        }
        this.mLinePaintTouchPointCircle.setColor(0);
        this.mLinePaintTouchPointCircle.setStrokeWidth(5.0f);
        this.mLinePaintTouchPointCircle.setStyle(Style.STROKE);
        this.mLinePaintTouchPointCircle.setAntiAlias(true);
    }

    public ArrayList<Img> getmImages() {
        return DOODLE_IMAGE;
    }

    public void loadImages(Context context) {
        Resources res = context.getResources();
        int n = DOODLE_IMAGE.size();
        for (int i = 0; i < n; i++) {
            ((Img) DOODLE_IMAGE.get(i)).load(res);
        }
    }

    public void loadImageItem(Img imgObject2, Context context) {
        imgObject2.load(context.getResources());
    }

    public void loadImageDrawable(Img imgObject2, Context context) {
        imgObject2.loadDrawable(context.getResources());
    }

    public void unloadImages() {
        int n = DOODLE_IMAGE.size();
        for (int i = 0; i < n; i++) {
            ((Img) DOODLE_IMAGE.get(i)).unload();
        }
    }

    public void unloadImageItem(Img imgObject2, Context context) {
        Resources resources = context.getResources();
        imgObject2.unload();
    }

    /* access modifiers changed from: protected */
    public void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        this.f275w = w;
        this.f274h = h;
        if (mCanvas == null) {
            this.mBitmapPaint = new Paint(4);
            mBitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
            mCanvas = new Canvas(mBitmap);
        }
    }

    /* access modifiers changed from: protected */
    public void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        int n = DOODLE_IMAGE.size();
        for (int i = 0; i < n; i++) {
            ((Img) DOODLE_IMAGE.get(i)).draw(canvas);
        }
        if (!DoodleActivity.isPencilDoodle) {
            canvas.drawPath(this.path, this.mPaintBrushErase);
            canvas.drawBitmap(mBitmap, 0.0f, 0.0f, this.mBitmapPaint);
        } else {
            canvas.drawPath(this.path, this.mPaintBrushErase);
            canvas.drawBitmap(mBitmap, 0.0f, 0.0f, this.mBitmapPaint);
        }
        drawMultitouchDebugMarks(canvas);
    }

    public void trackballClicked() {
        mUIMode = (mUIMode + 1) % 3;
    }

    private void drawMultitouchDebugMarks(Canvas canvas) {
        if (this.currTouchPoint.isDown()) {
            float[] xs = this.currTouchPoint.getXs();
            float[] ys = this.currTouchPoint.getYs();
            float[] pressures = this.currTouchPoint.getPressures();
            int numPoints = Math.min(this.currTouchPoint.getNumTouchPoints(), 2);
            for (int i = 0; i < numPoints; i++) {
                canvas.drawCircle(xs[i], ys[i], (pressures[i] * 80.0f) + 50.0f, this.mLinePaintTouchPointCircle);
            }
            if (numPoints == 2) {
                canvas.drawLine(xs[0], ys[0], xs[1], ys[1], this.mLinePaintTouchPointCircle);
            }
        }
    }

    private void touchMove() {
        if (this.path != null && this.mode == DRAW_MODE.DRAW) {
            mCanvas.drawPath(this.path, this.mPaintBrush);
            mCanvas.save();
        } else if (this.path != null && this.mode == DRAW_MODE.ERASE) {
            mCanvas.drawPath(this.path, this.mPaintBrushErase);
        }
    }
    public void initiateEraser() {
        this.mPaintBrushErase = new Paint();
        this.mPaintBrushErase.setColor(getResources().getColor(R.color.transparent));
        this.mPaintBrushErase.setStyle(Style.STROKE);
        this.mPaintBrushErase.setStrokeJoin(Paint.Join.ROUND);
        this.mPaintBrushErase.setStrokeCap(Paint.Cap.ROUND);
        this.mPaintBrushErase.setStrokeWidth(((float) this.eraserSize) * this.density);
    }

    public void initiatePensil() {
        this.mPaintBrush = new Paint();
        this.mPaintBrush.setAntiAlias(true);
        this.mPaintBrush.setColor(Color.parseColor(color));
        this.mPaintBrush.setStyle(Style.STROKE);
        this.mPaintBrush.setAntiAlias(true);
        this.mPaintBrush.setXfermode(null);
        this.mPaintBrush.setStrokeCap(Paint.Cap.ROUND);
        this.mPaintBrush.setStrokeJoin(Paint.Join.ROUND);
        this.mPaintBrush.setStrokeWidth(((float) this.pensilSize) * this.density);
    }

    public boolean onTouchEvent(MotionEvent event) {
        int actionMasked = event.getActionMasked();
        this.mainActivity = (DoodleActivity) getContext();
        this.UpdateTabBarIcon = this.mainActivity;
        float x = event.getX();
        float y = event.getY();
        switch (event.getAction()) {
            case 0:
                this.path = new Path();
                this.path.moveTo(x, y);
                this.isPointerDown = true;
                return true;
            case 1:
                this.isPointerUp = true;
                break;
            case 2:
                this.path.lineTo(x, y);
                touchMove();
                break;
            case 3:
                break;
            default:
                return false;
        }
        this.imgObject = getDraggableObject(x, y);
        this.UpdateTabBarIcon.updateDeleteIcon(x, y, this.imgObject, this.isPointerDown, this.isPointerUp);
        postInvalidate();
        return this.multiTouchController.onTouchEvent(event);
    }

    public Img getDraggableObject(float x, float y) {
        int i = DOODLE_IMAGE.size() - 1;
        if (i < 0) {
            return null;
        }
        Img im = (Img) DOODLE_IMAGE.get(i);
        if (im.containsPoint(x, y)) {
            this.mode = DRAW_MODE.DRAW;
            return im;
        }
        this.mode = DRAW_MODE.DRAW;
        return null;
    }

    public Img getDraggableObjectAtPoint(MultiTouchController.PointInfo pt) {
        float x = pt.getX();
        float y = pt.getY();
        for (int i = DOODLE_IMAGE.size() - 1; i >= 0; i--) {
            Img im = (Img) DOODLE_IMAGE.get(i);
            if (im.containsPoint(x, y)) {
                return im;
            }
        }
        return null;
    }

    public boolean onDragEvent(DragEvent event) {
        return super.onDragEvent(event);
    }

    public void selectObject(Img img, MultiTouchController.PointInfo touchPoint) {
        this.currTouchPoint.set(touchPoint);
        touchPoint.getX();
        touchPoint.getY();
        if (img != null) {
            DOODLE_IMAGE.remove(img);
            DOODLE_IMAGE.add(img);
        }
    }

    public void getPositionAndScale(Img img, MultiTouchController.PositionAndScale objPosAndScaleOut) {
        boolean z;
        boolean z2;
        boolean z3 = false;
        float centerX = img.getCenterX();
        float centerY = img.getCenterY();
        if ((mUIMode & 2) == 0) {
            z = true;
        } else {
            z = false;
        }
        float scaleX = (img.getScaleX() + img.getScaleY()) / 2.0f;
        if ((mUIMode & 2) != 0) {
            z2 = true;
        } else {
            z2 = false;
        }
        float scaleX2 = img.getScaleX();
        float scaleY = img.getScaleY();
        if ((mUIMode & 1) != 0) {
            z3 = true;
        }
        objPosAndScaleOut.set(centerX, centerY, z, scaleX, z2, scaleX2, scaleY, z3, img.getAngle());
    }

    public boolean setPositionAndScale(Img img, MultiTouchController.PositionAndScale newImgPosAndScale, MultiTouchController.PointInfo touchPoint) {
        this.currTouchPoint.set(touchPoint);
        boolean ok = img.setPos(newImgPosAndScale);
        if (ok) {
            invalidate();
        }
        return ok;
    }

    public void initializeCanvasDraw(int w, int h) {
        if (mCanvas == null) {
            this.mBitmapPaint = new Paint(4);
            mBitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
            mCanvas = new Canvas(mBitmap);
        }
    }

    public void clearCanvas() {
        if (mBitmap != null) {
            mBitmap.eraseColor(0);
            invalidate();
        }
    }

    private void saveBitmapList() {
        listBitmap.add(mBitmap);
    }

    private void touchStarted() {
        this.bitmapBackupCanvas.drawBitmap(mBitmap, 0.0f, 0.0f, null);
    }
}
