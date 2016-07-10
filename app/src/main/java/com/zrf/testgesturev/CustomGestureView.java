package com.zrf.testgesturev;

import android.content.Context;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.MotionEvent;
import android.view.ViewConfiguration;
import android.view.WindowManager;
import android.widget.LinearLayout;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;

public class CustomGestureView  extends LinearLayout{

    private float downX, downY;
    private int screenWidth;
    private int screenHeight;
    private int touchSlop;
    private long currentDownTime;
    private final static String TAG="CustomGestureView";
    private List<MoveInfo> moveList = new ArrayList<MoveInfo>();
    private boolean isGestureChange=false; //暂时没用
    private static final int LONG_PRESS_TIME=500;

    public CustomGestureView(Context context) {
        super(context );
        getScreenInfoAndTouchSlop();
    }


    public CustomGestureView(Context context, AttributeSet attrs) {
        super(context, attrs);
        getScreenInfoAndTouchSlop();
    }

    public CustomGestureView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        getScreenInfoAndTouchSlop();
    }

    /**
     * 左滑  右滑 单指点击  v   ^
     */
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction() ) {

        case MotionEvent.ACTION_DOWN:
            downX = event.getX();
            downY = event.getY();
            currentDownTime=Calendar.getInstance().getTimeInMillis();
            Log.d(TAG, "ACTION_DOWN pointerCount : "+event.getPointerCount());
            break;

        case MotionEvent.ACTION_MOVE:
            moveList.add(new MoveInfo(event.getX(), event.getY()));
            break;

        case MotionEvent.ACTION_UP:
            float upX = event.getX();
            float upY = event.getY();
            Log.d(TAG, "moveList.size:" + moveList.size());

            // 滑动 
            if((upX-downX)>touchSlop || (upX-downX)<-touchSlop ){

                //向左
                if(upX-downX<0){
                    if(upX-downX<-(screenWidth/2)){
                        onGestureChangeListener.processGestrue(GestureType.GEST_LEFT_SLIDE);
                        isGestureChange=true;
                        return true;
                    }
                 //向右
                }else{

                    Collections.sort(moveList); // 根据 moveY 排序 从小到大
                    double lessEnterAngle=0;
                    double lessOutAngle=0;
                    double peakEnterAngle=0;
                    double peakOutAngle=0;

                    /**
                     * ^ 拐点 x 轴 y 轴
                     */
                    float lessX = moveList.get(0).getMoveX();
                    float lessY = moveList.get(0).getMoveY();

                    /**
                     * v 拐点 x轴 y轴
                     */
                    float peakX = moveList.get(moveList.size() - 1).getMoveX();
                    float peakY = moveList.get(moveList.size() - 1).getMoveY();

                    moveList.clear();

                    Log.d(TAG, "downX:" + downX + "    downY:" + downY);
                    Log.d(TAG, "upX:" + upX + "    upY:" + upY);
                    Log.d(TAG, "lessX:" + lessX + "    lessY:" + lessY);
                    Log.d(TAG, "peakX:" + peakX + "    peakY:" + peakY);

                    if((lessX!=downX) && (lessX!=upX)){
                        // ^ 入角（与 x 轴 夹角）
                        lessEnterAngle = Math.atan(Math.abs(lessY - downY)
                                / Math.abs(lessX - downX))
                                * (180 / Math.PI);
                        Log.d(TAG, " ^  enter 角度：" + lessEnterAngle);
                        // ^ 出角 （与x 轴 夹角）
                        lessOutAngle = Math.atan(Math.abs(upY - lessY)
                                / Math.abs(upX - lessX))
                                * (180 / Math.PI);
                        Log.d(TAG, " ^  out 角度：" + lessOutAngle);

                     // ^ 手势识别
                        if (upX > lessX && lessX > downX && lessY < upY
                                && lessY < downY && lessEnterAngle > 45
                                && lessOutAngle > 45 &&(downY-lessY>screenHeight/4 || (upY-lessY)>screenHeight/4)) {
                            Log.d(TAG, " ^  OK");
                            onGestureChangeListener.processGestrue(GestureType.GEST_V_OPPOSITE);
                            isGestureChange=true;
                            return true;
                        }
                    }

                    if((peakX != downX) && (peakX != upX)){
                        // v 入角（与 x 轴 夹角）
                        peakEnterAngle = Math.atan(Math.abs(peakY - downY)
                                / Math.abs(peakX - downX))
                                * (180 / Math.PI);
                        Log.d(TAG, " v   enter 角度：" + peakEnterAngle);
                        // v 出角 （与x 轴 夹角）
                        peakOutAngle = Math.atan(Math.abs(upY - peakY)
                                / Math.abs(upX - peakX))
                                * (180 / Math.PI);
                        Log.d(TAG, "  v  out 角度：" + peakOutAngle);

                        // v 手势识别
                        if (upX > peakX && peakX > downX && peakY > upY
                                && peakY > downY && peakEnterAngle > 45
                                && peakOutAngle > 45 &&(peakY-downY>screenHeight/4 || (peakY-downY)>screenHeight/4)) {
                            Log.d(TAG, "v  OK");
                            onGestureChangeListener.processGestrue(GestureType.GEST_V);
                            isGestureChange=true;
                            return true;
                        }
                    }

                        // 为了 避免 用户 画 手势 v ^ 失败（角度小于 45） 而出现 右滑的糟糕体验  
                        // 所以加了额外条件 
                        if(upX-downX>(screenWidth/2) && ((lessEnterAngle<20 && lessOutAngle < 20) 
                                ||( peakEnterAngle <20 && peakOutAngle <20)  ||(lessX==downX) ||
                                (lessX==upX)||(peakX==downX) || (peakX==upX)) ){
                            onGestureChangeListener.processGestrue(GestureType.GEST_RIGHT_SLIDE);
                            isGestureChange=true;
                            return true;
                        }
                }
            }else{

                if (Calendar.getInstance().getTimeInMillis() - currentDownTime <= LONG_PRESS_TIME) {
                    onGestureChangeListener.processGestrue(GestureType.GEST_SINGLE_TAP);
                    isGestureChange = true;
                    return true;
                }else{
                    onGestureChangeListener.processGestrue(GestureType.GEST_LONG_PRESS);
                    isGestureChange=true;
                    return true;
                }
                
//                if(event.getPointerCount()==1){
//                    IwdsLog.d(TAG, "单指点击 OK");
//                    onGestureChangeListener.processGestrue(GestureType.GEST_SINGLE_TAP);
//                    isGestureChange=true;
//                    return true;
//                }
            }
            break;
        }
        return true;
    }

private void getScreenInfoAndTouchSlop() {
    WindowManager manager=(WindowManager) getContext().getSystemService(Context.WINDOW_SERVICE);
    DisplayMetrics dm=new DisplayMetrics();
    manager.getDefaultDisplay().getMetrics(dm);
    screenWidth=dm.widthPixels;
    screenHeight=dm.heightPixels;
    touchSlop=ViewConfiguration.get(getContext()).getScaledTouchSlop();
    }

private OnGestureChangeListener onGestureChangeListener;
public void setOnGestureChangeListener(OnGestureChangeListener onGestureChangeListener){
    this.onGestureChangeListener=onGestureChangeListener;
}
}
