package com.bewant2be.doit.nettool;

import android.os.SystemClock;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.bewant2be.doit.utilslib.ShellUtil;
import com.bewant2be.doit.utilslib.ToastUtil;

import java.io.OutputStream;
import java.util.Timer;
import java.util.TimerTask;


public class MainActivity extends AppCompatActivity {

    //EthernetManager mEthernetManager;
    private int timerSeconds = 10;

    Timer timer;
    TimerTask timerTask;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initUI();
    }


    public boolean runAsRoot(String cmd) {
        try {
            Process p = Runtime.getRuntime().exec("su");
            OutputStream out = p.getOutputStream();
            out.write((cmd + "\n").getBytes());
            out.flush();
            out.close();
            if (p.waitFor() == 0) {
                return true;
            }
            return false;
        } catch (Exception e) {
            Log.e("TAG", "Exception:  " + e.toString());
            ToastUtil.toastComptible(getApplicationContext(), "Exception:  " + e.toString());
            return false;
        }
    }


    private void initUI(){

        ((Button)findViewById(R.id.btnEnable)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                runAsRoot("ifconfig eth0 up");
            }
        });

        ((Button)findViewById(R.id.btnDisable)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                runAsRoot("ifconfig eth0 down");
            }
        });

        ((Button)findViewById(R.id.btnExit)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                System.exit(0);
            }
        });

        ((Button)findViewById(R.id.btnHide)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                moveTaskToBack(true);
            }
        });

        ((Button)findViewById(R.id.btnStartTimer)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String s =  ((EditText)findViewById(R.id.editTimer)).getText().toString();
                ToastUtil.toastComptible(getApplicationContext(), "循环定时:" + s + "秒");
                timerSeconds = Integer.parseInt(s);

                if(timer!=null){
                    timer.cancel();
                }

                timer = new Timer();
                TimerTask timerTask = new TimerTask() {
                    @Override
                    public void run() {
                        runAsRoot("ifconfig eth0 down");
                        SystemClock.sleep(5 * 1000);
                        runAsRoot("ifconfig eth0 up");
                        //ToastUtil.toastComptible(getApplicationContext(), "reset");
                    }
                };
                timer.schedule(timerTask, timerSeconds * 1000, timerSeconds * 1000);
            }
        });

    }


}
