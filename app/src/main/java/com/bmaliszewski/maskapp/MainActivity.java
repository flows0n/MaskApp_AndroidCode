package com.bmaliszewski.maskapp;

import static java.lang.Integer.parseInt;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.opengl.Visibility;
import android.os.Bundle;
import android.content.Intent;
import android.transition.AutoTransition;
import android.transition.TransitionManager;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.IdRes;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.Group;

import java.util.List;

public class MainActivity  extends BlunoLibrary {
    private Button button;
    private ImageButton settingsButton;
    private TextView batteryValue;
    private TextView batteryHeader;
    private TextView percentSign;
    private TextView minutesValue;
    private TextView secondsValue;
    private ImageView maskImage;
    private ImageView angle;


    private CardView batteryInfoCard;
    private CardView runtimeInfoCard;
    private CardView modeCard;

    private RadioGroup modes;

    private boolean enable;
    private String runtime = "T";
    private boolean runningThread = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        request(1000, new OnPermissionsResult() {
            @Override
            public void OnSuccess() {
                Toast.makeText(MainActivity.this, R.string.permGranded, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void OnFail(List<String> noPermissions) {
                Toast.makeText(MainActivity.this, R.string.permNotGranded, Toast.LENGTH_SHORT).show();
            }
        });

        onCreateProcess();                                                      //onCreate Process by BlunoLibrary
        serialBegin(115200);                                              //set the Uart Baudrate on BLE chip to 115200

        batteryValue = (TextView) findViewById(R.id.batteryValue);
        batteryHeader = (TextView) findViewById(R.id.batteryHeader);
        percentSign = (TextView) findViewById(R.id.percentSign);
        minutesValue = (TextView) findViewById(R.id.minutesValue);
        secondsValue = (TextView) findViewById(R.id.secondsValue);
        maskImage = (ImageView) findViewById(R.id.maskImage);
        batteryInfoCard = (CardView) findViewById(R.id.batteryInfoCard);
        runtimeInfoCard = (CardView) findViewById(R.id.runtimeInfoCard);
        angle = (ImageView) findViewById(R.id.angle);
        modeCard = (CardView) findViewById(R.id.modeCard);
        modes = (RadioGroup) findViewById(R.id.modes);
        button = (Button) findViewById(R.id.scanButton);
        settingsButton = findViewById(R.id.settingsButton);

        button.setOnClickListener(v -> buttonScanOnClickProcess());

        modeCard.setOnClickListener(v -> {
            int visible = (modes.getVisibility() == View.GONE)? View.VISIBLE: View.GONE;

            TransitionManager.beginDelayedTransition((ViewGroup) modeCard, new AutoTransition());
            modes.setVisibility(visible);
            if(visible == (int)View.VISIBLE)
                angle.setImageResource(R.drawable.angle_up_enable);
            else
                angle.setImageResource(R.drawable.angle_down_enable);
        });

        settingsButton.setOnClickListener(v -> {
                startActivity(new Intent(getApplicationContext(), MainActivity.class));
                finish();
        });

        modes.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, @IdRes int i) {
                switch (i){
                    case R.id.radioButtonOn:
                    serialSend("A");
                        break;
                    case R.id.radioButtonInterval1:
                        serialSend("B");
                        break;
                    case R.id.radioButtonInterval2:
                        serialSend("C");
                        break;
                    case R.id.radioButtonInterval5:
                        serialSend("D");
                        break;
                    case R.id.radioButtonOff:
                        serialSend("E");
                        break;
                }
            }
        });
        enableElements(false);
    }

    @Override
    protected void onResume() {
        super.onResume();
        System.out.println("BlUNOActivity onResume");
        onResumeProcess();                                                        //onResume Process by BlunoLibrary
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        onActivityResultProcess(requestCode, resultCode, data);                    //onActivityResult Process by BlunoLibrary
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    protected void onPause() {
        super.onPause();
        onPauseProcess();                                                        //onPause Process by BlunoLibrary
    }

    protected void onStop() {
        super.onStop();
        onStopProcess();                                                        //onStop Process by BlunoLibrary
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        onDestroyProcess();                                                        //onDestroy Process by BlunoLibrary
    }

    @SuppressLint("ResourceAsColor")
    @Override
    public void onConectionStateChange(connectionStateEnum theConnectionState) {//Once connection state changes, this function will be called
        switch (theConnectionState) {                                            //Four connection state
            case isConnected:
                button.setText(getString(R.string.disconnect));
                enableElements(true);
                serialSend(runtime);
                break;
            case isConnecting:
                button.setText(getString(R.string.connecting));
                break;
            case isToScan:
                button.setText(getString(R.string.scan));
                break;
            case isScanning:
                button.setText(getString(R.string.scanning));
                break;
            case isDisconnecting:
                runningThread = false;
                enableElements(false);
                batteryValue.refreshDrawableState();
                button.setText(getString(R.string.disconnecting));
                break;
            default:
                break;
        }
    }

    public void enableElements(boolean status){
        batteryValue.setTextColor(getResources().getColor(R.color.textview_colors));
        percentSign.setTextColor(getResources().getColor(R.color.textview_colors));
        if(modes.getVisibility() == View.VISIBLE){
            modeCard.callOnClick();

        }
        maskImage.setEnabled(status);
        angle.setImageResource(R.drawable.angle);
        modeCard.setClickable(status);
        disableEnableControls(status,batteryInfoCard);
        disableEnableControls(status,runtimeInfoCard);
        disableEnableControls(status,modeCard);
    }

    private void disableEnableControls(boolean enable, ViewGroup vg){
        for (int i = 0; i < vg.getChildCount(); i++){
            View child = vg.getChildAt(i);
            child.setEnabled(enable);
            if (child instanceof ViewGroup){
                disableEnableControls(enable, (ViewGroup)child);
            }
        }
    }

    @Override
    public void onSerialReceived(String receivedData) {
        String data;
        int battery;
        final int[] minutes = new int[1];
        final int[] seconds = new int[1];
        switch (receivedData.charAt(0)){
            case 'C':
                data = receivedData.substring(1);
                String[] parts = data.split("-");
                battery = (int)parseInt(parts[1]);
                batteryCheck(battery);
                seconds[0] = (int)parseInt(parts[0]);
                runningThread =true;
                new Thread() {
                    public void run() {
                        while (true) {
                            if (!runningThread) {
                                return;
                            }
                            try {
                                runOnUiThread(() -> {
                                    minutes[0] = seconds[0] / 60;
                                    minutesValue.setText(""+ minutes[0]);
                                    secondsValue.setText(""+(seconds[0] - (minutes[0] * 60)));
                                });
                                Thread.sleep(1000);
                                seconds[0]++;
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                }.start();

                break;
            case 'B':
                data = receivedData.substring(1);
                battery = (int)parseInt(data);
                batteryCheck(battery);
                break;
        }

    }


    private void batteryCheck(int percentage){
        if(percentage<=100 && percentage>=81){
            batteryValue.setTextColor(getResources().getColor(R.color.b100));
            percentSign.setTextColor(getResources().getColor(R.color.b100));}
        else if(percentage<81 && percentage>=61){
            batteryValue.setTextColor(getResources().getColor(R.color.b80));
            percentSign.setTextColor(getResources().getColor(R.color.b80));}
        else if(percentage<61 && percentage>=41){
            batteryValue.setTextColor(getResources().getColor(R.color.b60));
            percentSign.setTextColor(getResources().getColor(R.color.b60));}
        else if(percentage<41 && percentage>=21){
            batteryValue.setTextColor(getResources().getColor(R.color.b40));
            percentSign.setTextColor(getResources().getColor(R.color.b40));}
        else if(percentage<21 && percentage>=11){
            batteryValue.setTextColor(getResources().getColor(R.color.b20));
            percentSign.setTextColor(getResources().getColor(R.color.b20));}
        else{
            batteryValue.setTextColor(getResources().getColor(R.color.b10));
            percentSign.setTextColor(getResources().getColor(R.color.b10));}

        batteryValue.setText(""+percentage);
    }
}