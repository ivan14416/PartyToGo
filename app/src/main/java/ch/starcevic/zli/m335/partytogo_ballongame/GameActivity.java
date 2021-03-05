package ch.starcevic.zli.m335.partytogo_ballongame;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.widget.ImageView;
import android.widget.TextView;

public class GameActivity extends AppCompatActivity implements SensorEventListener {

    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;
    private TextView textViewNickname;
    private TextView textViewScore;
    private TextView textViewEndScore;
    private ImageView imageViewCircle;
    private SensorManager sensorManager;
    private Sensor acceleratorSensor;
    private long startTimer;
    private long lastTimeStamp = 0;
    private float highscore;
    private boolean gameEnded = false;
    private String playerName;
    private Bundle extras;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);

        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);

        this.sharedPreferences = this.getSharedPreferences("highscorePreferences", Context.MODE_PRIVATE);
        this.editor = sharedPreferences.edit();

        this.setView();

        this.sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        this.acceleratorSensor = this.sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        sensorManager.registerListener((SensorEventListener) this, acceleratorSensor, SensorManager.SENSOR_DELAY_FASTEST);

        this.extras = getIntent().getExtras();
        this.playerName = this.extras.getString("nickname");
    }

    @Override
    protected void onStart() {
        super.onStart();
        this.startGame();
    }

    public void setView() {
        this.textViewNickname = findViewById(R.id.textViewNickname);
        this.textViewScore = findViewById(R.id.textViewScore);
        this.textViewEndScore = findViewById(R.id.textViewEndScore);
        this.imageViewCircle = findViewById(R.id.imageViewCircle);

        this.textViewNickname.setText(this.sharedPreferences.getString("highscoreNickname", "none"));
        this.highscore = this.sharedPreferences.getFloat("highscore", 0);
        this.textViewScore.setText(Float.toString(this.highscore));
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if(!this.gameEnded) {
            if (imageViewCircle.getLayoutParams().height >= 700) {
                this.gameEnded = true;
                this.endGame();
            } else if (lastTimeStamp == 0 || System.currentTimeMillis() - lastTimeStamp > 0.001) {
                double acceleration = Math.sqrt(Math.pow(event.values[0], 2) + Math.pow(event.values[1], 2) + Math.pow(event.values[2], 2));
                while (acceleration > 20) {
                    this.lastTimeStamp = System.currentTimeMillis();
                    imageViewCircle.requestLayout();
                    imageViewCircle.getLayoutParams().height++;
                    imageViewCircle.getLayoutParams().width++;
                    acceleration -= 30;
                }
            }
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    private void startGame() {
        this.startTimer = System.currentTimeMillis();
    }
    private void endGame() {
        long stopTimer = System.currentTimeMillis();
        float endTimeInSeconds = (float) ((stopTimer - this.startTimer) / 1000.0);

        this.sensorManager.unregisterListener(this);

        this.textViewEndScore.setText(Float.toString(endTimeInSeconds) + " s");

        if (endTimeInSeconds < this.highscore || this.highscore == 0.0) {
            this.editor.putFloat("highscore", endTimeInSeconds);
            this.editor.putString("highscoreNickname", this.playerName);
            this.editor.apply();
        }

        Vibrator vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        vibrator.vibrate(VibrationEffect.createOneShot(3000, VibrationEffect.DEFAULT_AMPLITUDE));
    }
}