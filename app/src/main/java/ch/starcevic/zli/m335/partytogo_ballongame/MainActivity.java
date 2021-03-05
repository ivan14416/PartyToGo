package ch.starcevic.zli.m335.partytogo_ballongame;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;

public class MainActivity extends AppCompatActivity {

    EditText nicknameEditText;
    Button startGameButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);

        this.setView();
    }

    public void setView() {
        this.nicknameEditText = findViewById(R.id.editTextNickname);
        this.startGameButton = findViewById(R.id.startGameButton);

        Intent gameActivity = new Intent(this, GameActivity.class);

        this.startGameButton.setOnClickListener(v -> {
            if (this.nicknameEditText.getTextSize() != 0) {
                gameActivity.putExtra("nickname", nicknameEditText.getText().toString());
                startActivity(gameActivity);
            }
        });
    }
}