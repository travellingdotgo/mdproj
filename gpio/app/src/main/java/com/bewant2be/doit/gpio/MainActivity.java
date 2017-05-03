package com.bewant2be.doit.gpio;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.bewant2be.doit.utilslib.ToastUtil;
import com.mediatek.engineermode.io.EmGpio;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        boolean ret = EmGpio.turnSwitch(61, 0);
        ToastUtil.toastComptible(getApplicationContext(), "turnSwitch: " + ret);
    }
}
