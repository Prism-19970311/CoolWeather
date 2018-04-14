package com.prism.coolweather.customwidget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.widget.ProgressBar;

/**
 * Create by 棱镜  *
 **/
public class MyProgressBar extends ProgressBar {

    private String text ;
    private Paint mPaint ;


    public MyProgressBar(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
// TODO Auto-generated constructor stub
        init() ;
    }


    public MyProgressBar(Context context, AttributeSet attrs) {
        super(context, attrs);
// TODO Auto-generated constructor stub
        init() ;
    }


    public MyProgressBar(Context context) {
        super(context);
// TODO Auto-generated constructor stub
        init() ;
    }

    @Override
    protected synchronized void onDraw(Canvas canvas) {
// TODO Auto-generated method stub
        super.onDraw(canvas);
        Rect rect = new Rect() ;
        mPaint.getTextBounds(text, 0, text.length(), rect) ;
        int x = getWidth()/2 - rect.centerX() ;
        int y = getHeight()/2-rect.centerY() ;
        canvas.drawText(text, x, y, mPaint) ;
    }



    private void init(){
        mPaint = new Paint() ;
        mPaint.setColor(Color.GRAY) ;
    }

    public  void setText(String text){
        this.text = text ;
    }
}