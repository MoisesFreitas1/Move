package com.a2stars.move;

import android.content.Context;
import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;

import java.util.ArrayList;

public class GyroscopeActivity extends AppCompatActivity implements SensorEventListener {
    private double[] GyroscopeX;
    private double[] GyroscopeY;
    private double[] GyroscopeZ;

    private double[] EstimationX;
    private double[] EstimationY;
    private double[] EstimationZ;

    private double x,y,z;
    private double xKF,yKF,zKF;

    private double qx = 1d;
    private double rx = 1d;
    private double px = 1d;
    private double kx = 1d;

    private double qy = 1d;
    private double ry = 1d;
    private double py = 1d;
    private double ky = 1d;

    private double qz = 1d;
    private double rz = 1d;
    private double pz = 1d;
    private double kz = 1d;

    private boolean startStop;

    private int win = 300; //número de medidas que serão apresentadas nos gráficos
    private SensorManager mSensorManager;
    private Sensor mGyroscope;

    private LineChart mChart1,mChart2,mChart3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gyroscope);

        startStop = false;
        final Button StartStop = findViewById(R.id.StartStop);
        StartStop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(startStop == false){
                    startStop = true;
                    StartStop.setText("STOP");
                    StartStop.setBackgroundColor(Color.parseColor("#ff4000"));
                } else {
                    startStop = false;
                    StartStop.setText("START");
                    StartStop.setBackgroundColor(Color.parseColor("#80ff00"));
                }
            }
        });

        final EditText windows = findViewById(R.id.windows);
        windows.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(s.length() != 0){
                    win = Integer.parseInt(windows.getText().toString());
                    GyroscopeX = new double[win];
                    GyroscopeY = new double[win];
                    GyroscopeZ = new double[win];
                    EstimationX = new double[win];
                    EstimationY = new double[win];
                    EstimationZ = new double[win];
                    for (int i = 0; i < win; i++) {
                        GyroscopeX[i] = 0;
                        GyroscopeY[i] = 0;
                        GyroscopeZ[i] = 0;
                        EstimationX[i] = 0;
                        EstimationY[i] = 0;
                        EstimationZ[i] = 0;
                    }
                } else{
                    win = 300;
                    GyroscopeX = new double[win];
                    GyroscopeY = new double[win];
                    GyroscopeZ = new double[win];
                    EstimationX = new double[win];
                    EstimationY = new double[win];
                    EstimationZ = new double[win];
                    for (int i = 0; i < win; i++) {
                        GyroscopeX[i] = 0;
                        GyroscopeY[i] = 0;
                        GyroscopeZ[i] = 0;
                        EstimationX[i] = 0;
                        EstimationY[i] = 0;
                        EstimationZ[i] = 0;
                    }
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        mChart1 = (LineChart) findViewById(R.id.chart1);
        mChart1.setDragEnabled(true);
        mChart1.setScaleEnabled(true);
        mChart1.getAxisLeft().setEnabled(false);

        XAxis xAxis = mChart1.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);

        YAxis yAxis = mChart1.getAxisLeft();
        yAxis.setPosition(YAxis.YAxisLabelPosition.OUTSIDE_CHART);

        mChart2 = (LineChart) findViewById(R.id.chart2);
        mChart2.setDragEnabled(true);
        mChart2.setScaleEnabled(true);
        mChart2.getAxisLeft().setEnabled(false);

        XAxis xAxis2 = mChart2.getXAxis();
        xAxis2.setPosition(XAxis.XAxisPosition.BOTTOM);

        YAxis yAxis2 = mChart2.getAxisLeft();
        yAxis2.setPosition(YAxis.YAxisLabelPosition.OUTSIDE_CHART);

        mChart3 = (LineChart) findViewById(R.id.chart3);
        mChart3.setDragEnabled(true);
        mChart3.setScaleEnabled(true);
        mChart3.getAxisLeft().setEnabled(false);

        XAxis xAxis3 = mChart3.getXAxis();
        xAxis3.setPosition(XAxis.XAxisPosition.BOTTOM);

        YAxis yAxis3 = mChart3.getAxisLeft();
        yAxis3.setPosition(YAxis.YAxisLabelPosition.OUTSIDE_CHART);

        GyroscopeX = new double[win];
        GyroscopeY = new double[win];
        GyroscopeZ = new double[win];
        EstimationX = new double[win];
        EstimationY = new double[win];
        EstimationZ = new double[win];
        for (int i = 0; i < win; i++) {
            GyroscopeX[i] = 0;
            GyroscopeY[i] = 0;
            GyroscopeZ[i] = 0;
            EstimationX[i] = 0;
            EstimationY[i] = 0;
            EstimationZ[i] = 0;
        }
        mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        mGyroscope = mSensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE);
    }
    @Override
    protected void onResume() {
        super.onResume();
        mSensorManager.registerListener((SensorEventListener) this, mGyroscope, SensorManager.SENSOR_DELAY_NORMAL);
    }

    @Override
    protected void onPause() {
        super.onPause();
        mSensorManager.unregisterListener((SensorEventListener) this);
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
    }

    @Override
    public void onSensorChanged(SensorEvent event) {

        if (startStop == true) {
            // A taxa de amostragem das medidas é a taxa de atualização do sensor
            x = event.values[0];
            y = event.values[1];
            z = event.values[2];

            xKF = x;
            yKF = y;
            zKF = z;

            //Filtro de Kalman
            //X
            px = px + qx;
            kx = px / (px + rx);
            xKF = xKF + kx * (GyroscopeX[win - 1] - xKF);
            px = (1 - kx) * px;
            rx = 1 + (rx / (rx + kx));

            // -> Vetor Estimação -  Filtro de Kalman
            for (int i = 0; i < win - 1; i++) {
                EstimationX[i] = EstimationX[i + 1];
            }
            EstimationX[win - 1] = xKF;

            //Y
            py = py + qy;
            ky = py / (py + ry);
            yKF = yKF + ky * (GyroscopeY[win - 1] - yKF);
            py = (1 - ky) * py;
            ry = 1 + (ry / (ry + ky));

            // -> Vetor Estimação -  Filtro de Kalman
            for (int i = 0; i < win - 1; i++) {
                EstimationY[i] = EstimationY[i + 1];
            }
            EstimationY[win - 1] = yKF;

            //Z
            pz = pz + qz;
            kz = pz / (pz + rz);
            zKF = zKF + kz * (GyroscopeZ[win - 1] - zKF);
            pz = (1 - kz) * pz;
            rz = 1 + (rz / (rz + kz));

            // -> Vetor Estimação -  Filtro de Kalman
            for (int i = 0; i < win - 1; i++) {
                EstimationZ[i] = EstimationZ[i + 1];
            }
            EstimationZ[win - 1] = zKF;

            for (int i = 0; i < win - 1; i++) {
                GyroscopeX[i] = GyroscopeX[i + 1];
                GyroscopeY[i] = GyroscopeY[i + 1];
                GyroscopeZ[i] = GyroscopeZ[i + 1];
            }
            GyroscopeX[win - 1] = x;
            GyroscopeY[win - 1] = y;
            GyroscopeZ[win - 1] = z;


            int j = 0;
            ArrayList<Entry> TrueValueX = new ArrayList<>();
            for (int i = (-win); i < 0; i++) {
                TrueValueX.add(new Entry(i, (float) GyroscopeX[j]));
                j = j + 1;
            }

            j = 0;
            ArrayList<Entry> TrueValueY = new ArrayList<>();
            for (int i = (-win); i < 0; i++) {
                TrueValueY.add(new Entry(i, (float) GyroscopeY[j]));
                j = j + 1;
            }

            j = 0;
            ArrayList<Entry> TrueValueZ = new ArrayList<>();
            for (int i = (-win); i < 0; i++) {
                TrueValueZ.add(new Entry(i, (float) GyroscopeZ[j]));
                j = j + 1;
            }

            j = 0;
            ArrayList<Entry> EstimationValueX = new ArrayList<>();
            for (int i = (-win); i < 0; i++) {
                EstimationValueX.add(new Entry(i, (float) EstimationX[j]));
                j = j + 1;
            }

            j = 0;
            ArrayList<Entry> EstimationValueY = new ArrayList<>();
            for (int i = (-win); i < 0; i++) {
                EstimationValueY.add(new Entry(i, (float) EstimationY[j]));
                j = j + 1;
            }

            j = 0;
            ArrayList<Entry> EstimationValueZ = new ArrayList<>();
            for (int i = (-win); i < 0; i++) {
                EstimationValueZ.add(new Entry(i, (float) EstimationZ[j]));
                j = j + 1;
            }

            LineDataSet set1, set2, set3, set4, set5, set6;
            set1 = new LineDataSet(TrueValueX, "ωx = " + x + " rad/s");
            set1.setColor(Color.RED);
            set1.setDrawCircles(false);
            set1.setValueTextColor(Color.RED);

            set2 = new LineDataSet(TrueValueY, "ωy = " + y + " rad/s");
            set2.setColor(Color.RED);
            set2.setDrawCircles(false);
            set2.setValueTextColor(Color.RED);

            set3 = new LineDataSet(TrueValueZ, "ωz = " + z + " rad/s");
            set3.setColor(Color.RED);
            set3.setDrawCircles(false);
            set3.setValueTextColor(Color.RED);

            set4 = new LineDataSet(EstimationValueX, "ω̂ x = " + xKF + " rad/s");
            set4.setColor(Color.BLUE);
            set4.setDrawCircles(false);
            set4.setValueTextColor(Color.BLUE);

            set5 = new LineDataSet(EstimationValueY, "ω̂ y = " + yKF + " rad/s");
            set5.setColor(Color.BLUE);
            set5.setDrawCircles(false);
            set5.setValueTextColor(Color.BLUE);

            set6 = new LineDataSet(EstimationValueZ, "ω̂ z = " + zKF + " rad/s");
            set6.setColor(Color.BLUE);
            set6.setDrawCircles(false);
            set6.setValueTextColor(Color.BLUE);

            LineData data = new LineData(set1, set4);
            mChart1.setData(data);
            mChart1.setAutoScaleMinMaxEnabled(true);
            mChart1.notifyDataSetChanged();
            mChart1.invalidate();
            mChart1.setMaxVisibleValueCount(5);

            LineData data2 = new LineData(set2, set5);
            mChart2.setData(data2);
            mChart2.setAutoScaleMinMaxEnabled(true);
            mChart2.notifyDataSetChanged();
            mChart2.invalidate();
            mChart2.setMaxVisibleValueCount(5);

            LineData data3 = new LineData(set3, set6);
            mChart3.setData(data3);
            mChart3.setAutoScaleMinMaxEnabled(true);
            mChart3.notifyDataSetChanged();
            mChart3.invalidate();
            mChart3.setMaxVisibleValueCount(5);

            for (int i = 0; i < win - 1; i++) {
                GyroscopeX[i] = GyroscopeX[i + 1];
                GyroscopeY[i] = GyroscopeY[i + 1];
                GyroscopeZ[i] = GyroscopeZ[i + 1];
                EstimationX[i] = EstimationX[i + 1];
                EstimationY[i] = EstimationY[i + 1];
                EstimationZ[i] = EstimationZ[i + 1];
            }
            GyroscopeX[win - 1] = x;
            GyroscopeY[win - 1] = y;
            GyroscopeZ[win - 1] = z;
            EstimationX[win - 1] = xKF;
            EstimationY[win - 1] = yKF;
            EstimationZ[win - 1] = zKF;
        }
    }
}