package com.ly.magiclight;

import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.ly.magiclight.view.ActionView;
import com.ly.magiclight.view.ColorfulLayout;

/**
 * @author ly
 * date 2021/3/12 13:50
 */
public class MainActivity extends AppCompatActivity {

    private ColorfulLayout colorfulLayout;
    private AlertDialog colorDialog;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);
        setFullScreen();

        colorfulLayout = findViewById(R.id.container);
        ActionView actionView = findViewById(R.id.action_view);
        actionView.setColorfulLayout(colorfulLayout);
    }

    private void setFullScreen() {
        //全屏沉浸模式
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                    | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                    | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                    | View.SYSTEM_UI_FLAG_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_IMMERSIVE);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {//让内容延伸到刘海屏区域
                WindowManager.LayoutParams lp = getWindow().getAttributes();
                lp.layoutInDisplayCutoutMode = WindowManager.LayoutParams.LAYOUT_IN_DISPLAY_CUTOUT_MODE_SHORT_EDGES;
                getWindow().setAttributes(lp);
            }
        }
    }

}
