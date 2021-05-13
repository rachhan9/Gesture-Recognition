package ca.uwaterloo.cs349;

import android.content.Context;
import android.graphics.Canvas;
import android.view.MotionEvent;
import android.view.View;


public class AdditionGestureRecognitionView extends View {
    static  int count = 0;
    OneStroke curStroke;
    final String DEBUG_TAG = "Addition";

    public AdditionGestureRecognitionView(Context context) {
        super(context);
    }

    @Override
    public void onDraw(Canvas canvas){
        if (curStroke == null) return;
        curStroke.draw(canvas);
    }


    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int touchX = Math.round(event.getX());
        int touchY = Math.round(event.getY());
        int action = event.getAction();

        switch(action) {
            case (MotionEvent.ACTION_DOWN) :
                curStroke = new OneStroke(touchX,touchY);
                break;
            case (MotionEvent.ACTION_MOVE) :
                curStroke.addPoint(touchX,touchY);
                break;
            case (MotionEvent.ACTION_UP) :

            default :
                break;
        }

        postInvalidate();
        return true;
    }
}