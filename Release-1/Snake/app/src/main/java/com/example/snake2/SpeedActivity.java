package com.example.snake2;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.content.ContentValues;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

public class SpeedActivity extends AppCompatActivity implements View.OnClickListener {
    
    public static final String SPEED_KEY_SPEED = "com.example.snake2.Speed.SPEED";
    final private int NUM_OF_SPEEDS = 8;

    private int speeds1[] = { R.id.speed1, R.id.speed2, R.id.speed3, R.id.speed4, R.id.speed5,
            R.id.speed6, R.id.speed7, R.id.speed8 };

    private Button speeds[] = new Button[NUM_OF_SPEEDS];

    private SpeedEnum speed = SpeedEnum.One;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_speed);
        speed = (SpeedEnum)getIntent().getSerializableExtra(SPEED_KEY_SPEED);

        initializeComponent(speeds1);
        for (int i = 0; i < NUM_OF_SPEEDS; i++) {
            speeds[i].setOnClickListener(this);
        }
        
        coloring();
    }

    @Override
    public void onClick(View view) {
        Intent answerIntent = new Intent();
        int i = 0;
        while (speeds[i] != view)
            i++;
        switch (i)
        {
            case 0:
                speed = SpeedEnum.One;
                break;
            case 1:
                speed = SpeedEnum.Two;
                break;
            case 2:
                speed = SpeedEnum.Three;
                break;
            case 3:
                speed = SpeedEnum.Four;
                break;
            case 4:
                speed = SpeedEnum.Five;
                break;
            case 5:
                speed = SpeedEnum.Six;
                break;
            case 6:
                speed = SpeedEnum.Seven;
                break;
            case 7:
                speed = SpeedEnum.Eight;
                break;
        }
        for (int j = 0; j < NUM_OF_SPEEDS; j++)
        {
            if (j <= i)
                speeds[j].setBackgroundColor(ContextCompat.getColor(this, R.color.green_four));
            else
                speeds[j].setBackgroundColor(Color.WHITE);
        }
        answerIntent.putExtra(SPEED_KEY_SPEED, speed);
        setResult(RESULT_OK, answerIntent);
    }
    
    private void initializeComponent(int[] tg1) {
        for (int i = 0; i < NUM_OF_SPEEDS; i++) {
            speeds[i] = findViewById(tg1[i]);
        }
    }
    
    private void coloring()
    {
        int i = -1;
        switch (speed)
        {
            case One:
                i = 0;
                break;
            case Two:
                i = 1;
                break;
            case Three:
                i = 2;
                break;
            case Four:
                i = 3;
                break;
            case Five:
                i = 4;
                break;
            case Six:
                i = 5;
                break;
            case Seven:
                i = 6;
                break;
            case Eight:
                i = 7;
                break;
        }
        for (int j = 0; j < NUM_OF_SPEEDS; j++)
        {
            if (j <= i)
                speeds[j].setBackgroundColor(ContextCompat.getColor(this, R.color.green_four));
            else
                speeds[j].setBackgroundColor(Color.WHITE);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        DBHelper dbHelper = new DBHelper(this);
        SQLiteDatabase database = dbHelper.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(DBHelper.COL_SPEED, speed.ordinal());
        database.update(DBHelper.TABLE_OF_SELECTED_PARAMETERS, contentValues, DBHelper.COL_ID + "= ?", new String[]{String.valueOf(1)});
        dbHelper.close();
    }
}
