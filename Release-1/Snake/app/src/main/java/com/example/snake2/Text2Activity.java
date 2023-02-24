package com.example.snake2;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.widget.TextView;

public class Text2Activity extends Activity {

    public static final String TEXT2_KEY_TEXT = "text2_key_TEXT";
    private TextView info;
    private static final int DELAY = 2000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_text2);

        info = findViewById(R.id.info);
        Intent intent = getIntent();
        int score = intent.getIntExtra(TEXT2_KEY_TEXT, -1);

        info.setText(info.getText() + "\n" + score);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                finish();
            }
        }, DELAY);
    }
}
