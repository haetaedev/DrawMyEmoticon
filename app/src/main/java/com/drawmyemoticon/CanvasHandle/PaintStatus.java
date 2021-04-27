package com.drawmyemoticon.CanvasHandle;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.view.View;

public class PaintStatus extends View {
    private Paint paint = new Paint();
    private Path path = new Path();

    public PaintStatus(Context context, float thickness, int color) {
        super(context);
        init(thickness, color);
    }

    private void init(float thickness, int color){
        paint.setStrokeWidth(thickness);
        paint.setColor(color);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeJoin(Paint.Join.ROUND);
    }

    public void savePath(Path path){
        this.path = path;
    }

    public Path getPath(){
        return path;
    }

    public Paint getPaint(){
        return paint;
    }

    public void setColor(int color){
        paint.setColor(color);
    }
}
