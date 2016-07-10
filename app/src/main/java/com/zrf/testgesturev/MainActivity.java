package com.zrf.testgesturev;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Intent;
import android.os.Bundle;
import android.view.Window;
import android.widget.Toast;

public class MainActivity extends Activity  implements OnGestureChangeListener{

    // private RightScrollView mRSView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // mRSView = getRightScrollView();
        // mRSView.disableRightScroll(); // 初始化 设为 不可滑动
        // mRSView.setContentView(R.layout.activity_main);
        setContentView(R.layout.activity_main);
        CustomGestureView gestureView=(CustomGestureView) findViewById(R.id.cgv_gesture_view);
        gestureView.setOnGestureChangeListener(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        // if(mRSView!=null){
        // mRSView.enableRightScroll();
        // }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    @Override
    public void processGestrue(int gestureType) {
        switch (gestureType) {
        case GestureType.GEST_LEFT_SLIDE:
            Toast.makeText(this, "水平左滑  OK", Toast.LENGTH_SHORT).show();
            break;
        case GestureType.GEST_RIGHT_SLIDE:
            Toast.makeText(this, "水平 右滑 OK", Toast.LENGTH_SHORT).show();
            break;
        case GestureType.GEST_V:
            Toast.makeText(this, " v OK", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent("com.ingenic.settings.personalise.LauncherGestureToAppActivity.action.START"); 
            ComponentName componentName = new ComponentName("com.ingenic.settings",
                    "com.ingenic.settings.personalise.LauncherGestureToAppActivity");
            intent.addCategory(Intent.CATEGORY_DEFAULT); 
            intent.putExtra("gestrue_type", 5);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.setComponent(componentName); 
            startActivity(intent);
            break;
        case GestureType.GEST_V_OPPOSITE:
            Toast.makeText(this, "^ OK", Toast.LENGTH_SHORT).show();
            break;
        case GestureType.GEST_SINGLE_TAP:
            Toast.makeText(this, "单指点击 OK", Toast.LENGTH_SHORT).show();
            break;
        case GestureType.GEST_LONG_PRESS:
            Toast.makeText(this, "长按  OK", Toast.LENGTH_SHORT).show();
        default:
            break;
        }
    }
}
