package com.example.snake2;

import androidx.appcompat.app.AppCompatActivity;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.widget.CompoundButton;
import android.widget.ToggleButton;

public class OptionsActivity extends AppCompatActivity implements CompoundButton.OnCheckedChangeListener {

    public static final String OPTIONS_KEY_IS_SWIPE = "com.example.snake2.Speed.IS_SWIPE";
    private ToggleButton isSwipeToggleButton;
    private boolean isSwipe;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_options);
        Intent intent = getIntent();
        isSwipe = intent.getBooleanExtra(OPTIONS_KEY_IS_SWIPE, false);
        initToggleButtons();
    }

    private void initToggleButtons() {
        isSwipeToggleButton = findViewById(R.id.is_swipe);
        isSwipeToggleButton.setOnCheckedChangeListener(this);
        isSwipeToggleButton.setChecked(isSwipe);
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        if (buttonView.getId() == R.id.is_swipe) {
            isSwipe = isChecked;
        }
        Intent answerIntent = new Intent();
        answerIntent.putExtra(OPTIONS_KEY_IS_SWIPE, isSwipeToggleButton.isChecked());
        setResult(RESULT_OK, answerIntent);
    }

    @Override
    protected void onPause() {
        super.onPause();

        DBHelper dbHelper = new DBHelper(this);
        SQLiteDatabase database = dbHelper.getWritableDatabase();

        ContentValues contentValues = new ContentValues();
        int isSwipe1 = isSwipe ? 1 : 0;
        contentValues.put(DBHelper.COL_CONTROL, isSwipe1);
        database.update(DBHelper.TABLE_OF_SELECTED_PARAMETERS, contentValues, DBHelper.COL_ID + "= ?", new String[]{String.valueOf(1)});

        dbHelper.close();
    }
}
