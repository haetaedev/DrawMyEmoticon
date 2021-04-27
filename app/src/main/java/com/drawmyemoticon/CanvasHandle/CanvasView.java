package com.drawmyemoticon.CanvasHandle;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Path;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;
import android.widget.Toast;

import java.util.ArrayList;

public class CanvasView extends View {
    private Path lastPath = new Path();
    private ArrayList<PaintStatus> lines = new ArrayList<>();

    private float thickness = 15;
    private int color = Color.BLACK;
    private Bitmap bitmap;

    // Constructor as CustomView
    public CanvasView(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        init();
    }

    // Basic Constructor
    public CanvasView(Context context) {
        super(context);
    }

    private void init() {
        lines.add(new PaintStatus(getContext(), thickness, color));
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        float pointX = event.getX();
        float pointY = event.getY();

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                lastPath = new Path();
                lastPath.moveTo(pointX, pointY);
                lines.add(new PaintStatus(getContext(), thickness, color));
                lines.get(lines.size() - 1).savePath(lastPath);
                return true;
            case MotionEvent.ACTION_MOVE:
                lastPath.lineTo(pointX, pointY);
                lines.get(lines.size() - 1).savePath(lastPath);
                break;
        }
        postInvalidate();
        return super.onTouchEvent(event);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.drawColor(Color.WHITE);

        if (bitmap!=null && bitmap.getWidth() != this.getWidth()) {
            bitmap = Bitmap.createScaledBitmap(bitmap, this.getWidth(), this.getHeight(), false);
        }
        if (bitmap != null) {
            canvas.drawBitmap(bitmap, 0, 0, null);
        }
        for (PaintStatus line : lines) {
            canvas.drawPath(line.getPath(), line.getPaint());
        }
    }

    public void clearCanvas() {
        bitmap = null;
        lines.clear();
        lines.add(new PaintStatus(getContext(), thickness, color));
        postInvalidate();
    }

    public void setThickness(int thickness) {
        this.thickness = thickness;
        lines.add(new PaintStatus(getContext(), thickness, color));
    }

    public void setColor(int color) {
        this.color = color;
        lines.add(new PaintStatus(getContext(), thickness, color));
    }

    public void drawBitmap(Bitmap bitmap) {
        this.bitmap = bitmap;
        postInvalidate();
    }

    public void removeLastPath(){
        if(lines.size()!=0) {
            lines.remove(lines.size() - 1);
        }
        postInvalidate();
    }

    public void drawShadowPath() {
        for (PaintStatus line : lines) {
            line.setColor(Color.GRAY);
        }
        postInvalidate();
    }
}
