package com.example.sensor;

import static android.hardware.SensorManager.getRotationMatrix;

import static androidx.dynamicanimation.animation.DynamicAnimation.*;

import androidx.appcompat.app.AppCompatActivity;
import androidx.dynamicanimation.animation.DynamicAnimation;
import androidx.dynamicanimation.animation.FlingAnimation;
import androidx.dynamicanimation.animation.FloatPropertyCompat;
import androidx.dynamicanimation.animation.SpringAnimation;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.sensor.databinding.ActivityMainBinding;

public class MainActivity extends AppCompatActivity implements SensorEventListener {

    private SensorManager sensorManager; // менеджер сенсоров/ доступ к датчику.
    Sensor sensorAccel;
    Sensor sensorGeo;
    private float[] rotationMatrix; // матрица поворота
    private float[] accelerometer ;  // данные с акселерометра
    private float[] geomagnetism;
//    текстовые поля для вывода информации
    private TextView xyAngle;
    private TextView xzAngle;
    private TextView yzAngle;
    private ImageView emoji;
    private boolean flag_start = false;



    public void flingItX(View view, float x) {
        FlingAnimation flingAnimationX = new FlingAnimation(emoji, X);
//        FlingAnimation flingAnimationY = new FlingAnimation(emoji, Y);

        flingAnimationX.setStartVelocity(x);
        flingAnimationX.setMinimumVisibleChange(0.00001f);
       // flingAnimationX.setFriction(0.5f);// трение

        flingAnimationX.start();
    }
    public void flingItY(View view,float y) {
        FlingAnimation flingAnimationY = new FlingAnimation(emoji, Y);

        flingAnimationY.setStartVelocity(y);
        flingAnimationY.setMinimumVisibleChange(0.00001f);
        //flingAnimationY.setFriction(0.5f);

        flingAnimationY.start();
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE); //получаем объект менеджера датчиков
        sensorGeo = sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
        sensorAccel = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER); // ускорение с гравитация 9,8
        accelerometer = new float[]{0, 0, 0};
        // поля для вывода показаний
        xyAngle = findViewById(R.id.xyValue);
        xzAngle= findViewById(R.id.xzValue);
        yzAngle = findViewById(R.id.yzValue);


        emoji = (ImageView)findViewById(R.id.emoji);

        Button button = (Button)findViewById(R.id.start);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(flag_start) flag_start=false;
                else flag_start=true;
            }
        });
    }




//  уточняем данные необходимых датчиков
    @Override
    protected void onResume() {
        super.onResume();
//          тип нужного нам датчика и частоту обновления данных
        sensorManager.registerListener(this, sensorGeo, SensorManager.SENSOR_DELAY_UI );
        sensorManager.registerListener(this, sensorAccel, SensorManager.SENSOR_DELAY_UI );
    }
//  в методе onPause() останавливаем получение данных:
    @Override
    protected void onPause() {
        super.onPause();
        sensorManager.unregisterListener(this);
    }
    String format(float values){
        return String.format("%1$.2f",values);
    }
    @Override
    public void onSensorChanged(SensorEvent event) {
        loadSensorData(event); // получаем данные с датчика
        SensorManager.getRotationMatrix(rotationMatrix, null, accelerometer, geomagnetism); //получаем матрицу поворота
       // SensorManager.getOrientation(rotationMatrix, orientation); //получаем данные ориентации устройства в пространстве

        if((xyAngle ==null)||(xzAngle==null)||(yzAngle ==null)){
            xyAngle = findViewById(R.id.xyValue);
            xzAngle= findViewById(R.id.xzValue);
            yzAngle = findViewById(R.id.yzValue);
        }
//      вывод результата
        xyAngle.setText(String.valueOf(format(accelerometer[0])));
        xzAngle.setText(String.valueOf(format(accelerometer[1])));
        yzAngle.setText(String.valueOf(format(accelerometer[2])));
//        xyAngle.setText(String.valueOf(format(emoji.getViewTreeObserver().addOnGlobalLayoutListener());
//        xzAngle.setText(String.valueOf(format(accelerometer[1])));
//        yzAngle.setText(String.valueOf(format(accelerometer[2])));

        if(flag_start) {
            flingItX(emoji, -100*Math.round(accelerometer[0]));
            flingItY(emoji, 100*Math.round(accelerometer[1]));
//            if(emoji.getViewTreeObserver().addOnGlobalLayoutListener())
        }


    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    private void loadSensorData(SensorEvent event) {
        final int type = event.sensor.getType(); //определяем тип датчика
        if (type == Sensor.TYPE_ACCELEROMETER) { //если акселерометр
            for(int i = 0; i < 3; i++){
                accelerometer[i] = event.values[i];
            }
        }
        if (type == Sensor.TYPE_MAGNETIC_FIELD) { //если геомагнитный датчик
            geomagnetism = event.values.clone();
        }
    }

}