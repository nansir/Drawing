package com.sir.library.drawing;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.RectF;
import android.os.Build;
import android.os.Parcelable;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;

import com.sir.library.drawing.draw.CanvasDrawer;
import com.sir.library.drawing.draw.PathDrawer;
import com.sir.library.drawing.draw.SerializablePath;
import com.sir.library.drawing.gestures.creator.GestureCreator;
import com.sir.library.drawing.gestures.creator.GestureCreatorListener;
import com.sir.library.drawing.gestures.scale.GestureScaleListener;
import com.sir.library.drawing.gestures.scale.GestureScaler;
import com.sir.library.drawing.gestures.scale.ScalerListener;
import com.sir.library.drawing.gestures.scroller.GestureScrollListener;
import com.sir.library.drawing.gestures.scroller.GestureScroller;
import com.sir.library.drawing.gestures.scroller.ScrollerListener;

import java.util.ArrayList;

/**
 * 电子签名
 */
public class DrawableView extends View implements View.OnTouchListener, ScrollerListener, GestureCreatorListener, ScalerListener {

    private final ArrayList<SerializablePath> paths = new ArrayList<>();

    private GestureScroller gestureScroller;
    private GestureScaler gestureScaler;
    private GestureCreator gestureCreator;
    private int canvasHeight;
    private int canvasWidth;
    private GestureDetector gestureDetector;
    private ScaleGestureDetector scaleGestureDetector;
    private PathDrawer pathDrawer;
    private CanvasDrawer canvasDrawer;
    private SerializablePath currentDrawingPath;

    public DrawableView(Context context) {
        super(context);
        init();
    }

    private void init() {
        gestureScroller = new GestureScroller(this);
        gestureDetector = new GestureDetector(getContext(), new GestureScrollListener(gestureScroller));
        gestureScaler = new GestureScaler(this);
        scaleGestureDetector = new ScaleGestureDetector(getContext(), new GestureScaleListener(gestureScaler));
        gestureCreator = new GestureCreator(this);
        pathDrawer = new PathDrawer();
        canvasDrawer = new CanvasDrawer();
        setOnTouchListener(this);
    }

    public DrawableView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public DrawableView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public DrawableView(Context context, AttributeSet attrs,
                        int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init();
    }

    public void setConfig(DrawableViewConfig config) {
        if (config == null) {
            throw new IllegalArgumentException("Paint configuration cannot be null");
        }
        canvasWidth = config.getCanvasWidth();
        canvasHeight = config.getCanvasHeight();
        gestureCreator.setConfig(config);
        gestureScaler.setZooms(config.getMinZoom(), config.getMaxZoom());
        gestureScroller.setCanvasBounds(canvasWidth, canvasHeight);
        canvasDrawer.setConfig(config);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        //gestureScroller.setViewBounds(w, h);
        gestureScroller.setCanvasBounds(w, h);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvasDrawer.onDraw(canvas);
        pathDrawer.onDraw(canvas, currentDrawingPath, paths);
    }

    @Override
    protected Parcelable onSaveInstanceState() {
        DrawableViewSaveState state = new DrawableViewSaveState(super.onSaveInstanceState());
        state.setPaths(paths);
        return state;
    }

    @Override
    protected void onRestoreInstanceState(Parcelable state) {
        if (!(state instanceof DrawableViewSaveState)) {
            super.onRestoreInstanceState(state);
        } else {
            DrawableViewSaveState ss = (DrawableViewSaveState) state;
            super.onRestoreInstanceState(ss.getSuperState());
            paths.addAll(ss.getPaths());
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        canvasWidth = resolveSize(canvasWidth, widthMeasureSpec);
        canvasHeight = resolveSize(canvasHeight, heightMeasureSpec);
        setMeasuredDimension(canvasWidth, canvasHeight);
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        scaleGestureDetector.onTouchEvent(event);
        gestureDetector.onTouchEvent(event);
        gestureCreator.onTouchEvent(event);
        invalidate();
        return true;
    }

    public void undo() {
        if (paths.size() > 0) {
            paths.remove(paths.size() - 1);
            invalidate();
        }
    }

    public boolean existValidDrawing() {
        return paths.size() > 0;
    }


    public void clear() {
        paths.clear();
        invalidate();
    }

    public Bitmap obtainBitmap() {
        return obtainBitmap(Bitmap.createBitmap(canvasWidth, canvasHeight, Bitmap.Config.ARGB_8888));
    }

    public Bitmap obtainBitmap(Bitmap createdBitmap) {
        return pathDrawer.obtainBitmap(createdBitmap, paths);
    }

    @Override
    public void onViewPortChange(RectF currentViewport) {
        gestureCreator.onViewPortChange(currentViewport);
        canvasDrawer.onViewPortChange(currentViewport);
    }

    @Override
    public void onCanvasChanged(RectF canvasRect) {
        gestureCreator.onCanvasChanged(canvasRect);
        canvasDrawer.onCanvasChanged(canvasRect);
    }

    @Override
    public void onGestureCreated(SerializablePath serializablePath) {
        paths.add(serializablePath);
    }

    @Override
    public void onCurrentGestureChanged(SerializablePath currentDrawingPath) {
        this.currentDrawingPath = currentDrawingPath;
    }

    @Override
    public void onScaleChange(float scaleFactor) {
        gestureScroller.onScaleChange(scaleFactor);
        gestureCreator.onScaleChange(scaleFactor);
        canvasDrawer.onScaleChange(scaleFactor);
    }
}
