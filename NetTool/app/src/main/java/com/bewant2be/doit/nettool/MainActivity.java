package com.bewant2be.doit.nettool;

import android.content.Intent;
import android.graphics.PixelFormat;
import android.os.Build;
import android.os.SystemClock;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bewant2be.doit.utilslib.ShellUtil;
import com.bewant2be.doit.utilslib.ToastUtil;

import java.io.OutputStream;
import java.util.Timer;
import java.util.TimerTask;


public class MainActivity extends AppCompatActivity {

    private int timerSeconds = 10;

    Timer timer;
    TimerTask timerTask;
    private int cnt = 0;


    private WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
    private static WindowManager windowManager;
    private static ImageView imageView;
    private static TextView textView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        String s  = "VERSION_NAME:  "+BuildConfig.VERSION_NAME + "\n" + "Rooted: " + ShellUtil.isRooted() ;
        ToastUtil.toastComptible(getApplicationContext(),  s);

        initUI();
    }

    private void initUI(){

        ((Button)findViewById(R.id.btnEnable)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ShellUtil.executeAsRoot("ifconfig eth0 up");
            }
        });

        ((Button)findViewById(R.id.btnDisable)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ShellUtil.executeAsRoot("ifconfig eth0 down");
            }
        });

        ((Button)findViewById(R.id.btnExit)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                System.exit(0);
            }
        });

        ((Button)findViewById(R.id.btnAuth)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
                    Intent myIntent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION);
                    startActivity(myIntent);
                }else{
                    ToastUtil.toastComptible(getApplicationContext(), " Build.VERSION_CODES.M above requred");
                }
            }
        });

        ((Button)findViewById(R.id.btnHide)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                moveTaskToBack(true);

                createFloatView();
            }
        });

        ((Button)findViewById(R.id.btnStartTimer)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String s = ((EditText) findViewById(R.id.editTimer)).getText().toString();
                ToastUtil.toastComptible(getApplicationContext(), "循环定时:" + s + "秒");
                timerSeconds = Integer.parseInt(s);

                if (timer != null) {
                    timer.cancel();
                }

                timer = new Timer();
                TimerTask timerTask = new TimerTask() {
                    @Override
                    public void run() {
                        /*
                        ToastUtil.toastComptible(getApplicationContext(), "ifconfig ing, cnt=" + cnt++);

                        ShellUtil.executeAsRoot("ifconfig eth0 down");
                        SystemClock.sleep(5 * 1000);
                        ShellUtil.executeAsRoot("ifconfig eth0 up");
                        */

                        ToastUtil.toastComptible(getApplicationContext(), "ren " + cnt++);
                        ShellUtil.executeAsRoot("dhcpcd -n eth0");
                    }
                };
                timer.schedule(timerTask, 5 * 1000, timerSeconds * 1000);
            }
        });

    }


    private void createFloatView()
    {
        // 1、获取系统级别的WindowManager
        windowManager = (WindowManager) getApplication().getSystemService(WINDOW_SERVICE);

        /*
        // 判断UI控件是否存在，存在则移除，确保开启任意次应用都只有一个悬浮窗
        if (imageView != null){
            windowManager.removeView(imageView);
        }
        // 2、使用Application context 创建UI控件，避免Activity销毁导致上下文出现问题
        imageView = new ImageView(getApplicationContext());
        imageView.setImageResource(R.mipmap.ic_launcher);
        */

        if (textView != null){
            windowManager.removeView(textView);
        }

        textView = new TextView(getApplicationContext());
        textView.setText(". . . .");


        // 3、设置系统级别的悬浮窗的参数，保证悬浮窗悬在手机桌面上
        // 系统级别需要指定type 属性
        // TYPE_SYSTEM_ALERT 允许接收事件
        // TYPE_SYSTEM_OVERLAY 悬浮在系统上
        // 注意清单文件添加权限

        //系统提示。它总是出现在应用程序窗口之上。
        lp.type =  WindowManager.LayoutParams.TYPE_SYSTEM_OVERLAY;

        // FLAG_NOT_TOUCH_MODAL不阻塞事件传递到后面的窗口
        // FLAG_NOT_FOCUSABLE 悬浮窗口较小时，后面的应用图标由不可长按变为可长按,不设置这个flag的话，home页的划屏会有问题
        lp.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
                |WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL;

        //悬浮窗默认显示的位置
        lp.gravity = Gravity.LEFT|Gravity.TOP;
        //显示位置与指定位置的相对位置差
        lp.x = 0;
        lp.y = 0;
        //悬浮窗的宽高
        lp.width = WindowManager.LayoutParams.WRAP_CONTENT;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;

        lp.format = PixelFormat.TRANSPARENT;
        //windowManager.addView(imageView,lp);
        windowManager.addView(textView,lp);

        /*
        //设置悬浮窗监听事件
        imageView.setOnTouchListener(new View.OnTouchListener() {
            private float lastX; //上一次位置的X.Y坐标
            private float lastY;
            private float nowX;  //当前移动位置的X.Y坐标
            private float nowY;
            private float tranX; //悬浮窗移动位置的相对值
            private float tranY;

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                boolean ret = false;
                switch (event.getAction()){
                    case MotionEvent.ACTION_DOWN:
                        // 获取按下时的X，Y坐标
                        lastX = event.getRawX();
                        lastY = event.getRawY();
                        ret = true;
                        break;
                    case MotionEvent.ACTION_MOVE:
                        // 获取移动时的X，Y坐标
                        nowX = event.getRawX();
                        nowY = event.getRawY();
                        // 计算XY坐标偏移量
                        tranX = nowX - lastX;
                        tranY = nowY - lastY;
                        // 移动悬浮窗
                        lp.x += tranX;
                        lp.y += tranY;
                        //更新悬浮窗位置
                        windowManager.updateViewLayout(imageView,lp);
                        //记录当前坐标作为下一次计算的上一次移动的位置坐标
                        lastX = nowX;
                        lastY = nowY;
                        break;
                    case MotionEvent.ACTION_UP:
                        break;
                }
                return ret;
            }
        });*/
    }


}
