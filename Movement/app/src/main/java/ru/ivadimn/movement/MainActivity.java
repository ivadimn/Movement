package ru.ivadimn.movement;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.util.Log;
import android.widget.TextView;

import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends AppCompatActivity {

    public static final String TAG = "MOVEMENT";
    public static final String TELEPHONE = "+79992125549";
    public static final String TEXT_MESSAGE = "Машина двинулась";


    private String valuesInfo;
    private TextView tvText;
    private SensorManager sensorManager;
    private Sensor sensorLinAccel;
    float[] valuesLinAccel = new float[3];
    Timer timer;
    StringBuilder sb = new StringBuilder();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        tvText = (TextView) findViewById(R.id.tvText);
        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        sensorLinAccel = sensorManager.getDefaultSensor(Sensor.TYPE_LINEAR_ACCELERATION);
        Log.d(TAG, "onCreate was worked");

    }

    @Override
    protected void onResume() {
        super.onResume();
        sensorManager.registerListener(listener, sensorLinAccel,SensorManager.SENSOR_DELAY_NORMAL);
        timer = new Timer();
        TimerTask task = new TimerTask() {
            @Override
            public void run() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        reaction();
                    }
                });
            }
        };
        timer.schedule(task, 0, 1000);
    }
    @Override
    protected void onPause() {
        super.onPause();
        sensorManager.unregisterListener(listener);
        timer.cancel();
    }

    String format(float values[]) {
        return String.format("%1$.1f\t\t%2$.1f\t\t%3$.1f", values[0], values[1],
                values[2]);
    }

    void showInfo() {
        sb.setLength(0);
        sb.append("\nLin accel : " + format(valuesLinAccel));
        tvText.setText(sb);
        Log.d(TAG, "showInfo was worked");
    }

    SensorEventListener listener = new SensorEventListener() {
        @Override
        public void onSensorChanged(SensorEvent sensorEvent) {
            for (int i = 0; i < 3; i++) {
                valuesLinAccel[i] = sensorEvent.values[i];
            }
        }

        @Override
        public void onAccuracyChanged(Sensor sensor, int i) {

        }
    };

    public void sendSms() {
        SmsManager smsManager = SmsManager.getDefault();
        smsManager.sendTextMessage(TELEPHONE, null, TEXT_MESSAGE, null, null);
    }

    public void reaction() {
        if (Math.abs(valuesLinAccel[0]) > 2 || Math.abs(valuesLinAccel[1]) > 2 ||
                Math.abs(valuesLinAccel[2]) > 2) {
            showInfo();
            sendSms();
        }
    }
}
