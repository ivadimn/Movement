package ru.ivadimn.movement;

import android.content.SharedPreferences;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.ToggleButton;

import java.util.Timer;
import java.util.TimerTask;

import ru.ivadimn.movement.interfaces.Dlgable;

public class MainActivity extends AppCompatActivity implements Dlgable {

    public static final String TAG = "ru.ivadimn.movement";
    public static final String TELEPHONE = "+79852396274";
    public static final String TEXT_MESSAGE = "Машина двинулась";


    public static final String TAG_PHONE_NUMBER = "PHONE_NUMBER";
    public static final String TAG_LOGGING = "LOGGING";
    public static final String TAG_TURN = "TURN";
    private final int TIMEOUT = 800;

    private String phone_number;
    private boolean logging = false;
    private boolean turn = false;
    private String valuesInfo;
    private TextView tvXAxis;
    private TextView tvYAxis;
    private TextView tvZAxis;
    private ToggleButton btnTurn;
    private SensorManager sensorManager;
    private Sensor sensorLinAccel;
    float[] valuesLinAccel = new float[3];
    Timer timer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        tvXAxis = (TextView) findViewById(R.id.tv_x_axis_id);
        tvYAxis = (TextView) findViewById(R.id.tv_y_axis_id);
        tvZAxis = (TextView) findViewById(R.id.tv_z_axis_id);
        btnTurn = (ToggleButton) findViewById(R.id.btn_turn_id);
        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        sensorLinAccel = sensorManager.getDefaultSensor(Sensor.TYPE_LINEAR_ACCELERATION);
        Log.d(TAG, "onCreate was worked");
    }
    @Override
    protected void onResume() {
        super.onResume();
        restoreData();
        if (turn)
            turnOn();
    }
    @Override
    protected void onPause() {
        super.onPause();
        if (turn)
            turnOff();
        saveData();
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch(item.getItemId()) {
            case R.id.mi_phone_number_id:
                changeNumberDlg();
                break;
            case R.id.mi_logging_id :
                logging = logging ? false : true;
                item.setChecked(logging);
                break;
        }
        return true;
    }

    String format(float values[]) {
        return String.format("%1$.1f\t\t%2$.1f\t\t%3$.1f", values[0], values[1],
                values[2]);
    }
    private void showInfo() {
        tvXAxis.setText(String.format("%1$.1f", valuesLinAccel[0]));
        tvYAxis.setText(String.format("%1$.1f", valuesLinAccel[1]));
        tvZAxis.setText(String.format("%1$.1f", valuesLinAccel[2]));
    }
    private void clearText() {
        tvXAxis.setText("");
        tvYAxis.setText("");
        tvZAxis.setText("");
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
        smsManager.sendTextMessage(phone_number, null, TEXT_MESSAGE, null, null);
    }
    public void reaction() {
        if (Math.abs(valuesLinAccel[0]) > 1.5 || Math.abs(valuesLinAccel[1]) > 1.5 ||
                Math.abs(valuesLinAccel[2]) > 1.5) {
            showInfo();
            sendSms();
        }
    }
    private void restoreData() {
        SharedPreferences preferences = getSharedPreferences(TAG, MODE_PRIVATE);
        phone_number = preferences.getString(TAG_PHONE_NUMBER, TELEPHONE);
        logging = preferences.getBoolean(TAG_LOGGING, false);
        turn = preferences.getBoolean(TAG_TURN, false);
    }

    private void saveData() {
        SharedPreferences preferences = getSharedPreferences(TAG, MODE_PRIVATE);
        SharedPreferences.Editor  editor = preferences.edit();
        editor.putString(TAG_PHONE_NUMBER, phone_number).apply();
        editor.putBoolean(TAG_LOGGING, logging).apply();
        editor.putBoolean(TAG_TURN, turn).apply();
    }

    private void turnOn() {
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
        timer.schedule(task, 0, TIMEOUT);

    }
    private void turnOff() {
        sensorManager.unregisterListener(listener);
        timer.cancel();
    }

    @Override
    public void onOkClick(final String s) {
        phone_number = s;
    }

    @Override
    public void onCancelClick() {

    }

    private void changeNumberDlg() {
        Bundle bundle = new Bundle();
        bundle.putString(TAG_PHONE_NUMBER, phone_number);
        PhoneDlg dlg = new PhoneDlg();
        dlg.setArguments(bundle);
        dlg.show(getSupportFragmentManager(), PhoneDlg.TAG);
    }

    public void onTurnClick(View view) {
        if (btnTurn.isChecked()) {
            turnOn();
            turn = true;
        }
        else {
            if (turn)
                turnOff();
            turn = false;
            clearText();
        }
    }
}
