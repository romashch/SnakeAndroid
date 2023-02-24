package com.example.snake2;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    private int[] buttons1 = new int[]{R.id.new_game, R.id.type, R.id.speed,
            R.id.records, R.id.options, R.id.about_program};
    private Button[] buttons;
    private TypeEnum labyrinth;
    private SpeedEnum speed;
    private boolean isSwipe;
    private StateGameEnum state;
    private final int TYPE_ACTIVITY = 1;
    private final int SPEED_ACTIVITY = 2;
    private final int OPTIONS_ACTIVITY = 3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getIntent();
        initializeButtonComponent(buttons1);
        restore();
    }

    private void restore() {
        DBHelper dbHelper = new DBHelper(this);
        SQLiteDatabase database = dbHelper.getReadableDatabase();
        Cursor cursor = database.query(DBHelper.TABLE_OF_SELECTED_PARAMETERS, null,null,null,null,null,null);
        if (cursor.moveToFirst()) {
            int index1 = cursor.getColumnIndex(DBHelper.COL_TYPE);
            int index2 = cursor.getColumnIndex(DBHelper.COL_SPEED);
            int index3 = cursor.getColumnIndex(DBHelper.COL_CONTROL);
            labyrinth = TypeEnum.values()[cursor.getInt(index1)];
            speed = SpeedEnum.values()[cursor.getInt(index2)];
            isSwipe = cursor.getInt(index3) == 1;
            cursor.close();
        }
        dbHelper.close();
    }

    @Override
    public void onClick(View view) {
        Intent intent;
        switch (view.getId()) {
            case R.id.new_game: {
                state = StateGameEnum.NoGame;
                intent = new Intent(this, GameFieldActivity.class);
                intent.putExtra(GameFieldActivity.GAMEFIELD_KEY_TYPE, labyrinth);
                intent.putExtra(GameFieldActivity.GAMEFIELD_KEY_SPEED, speed);
                intent.putExtra(GameFieldActivity.GAMEFIELD_KEY_STATE, state);
                intent.putExtra(GameFieldActivity.GAMEFIELD_KEY_IS_SWIPE, isSwipe);
                startActivity(intent);
                break;
            }
            case R.id.type: {
                intent = new Intent(this, TypeActivity.class);
                intent.putExtra(TypeActivity.TYPE_KEY_TYPE, labyrinth);
                startActivityForResult(intent, TYPE_ACTIVITY);
                break;
            }
            case R.id.speed: {
                intent = new Intent(this, SpeedActivity.class);
                intent.putExtra(SpeedActivity.SPEED_KEY_SPEED, speed);
                startActivityForResult(intent, SPEED_ACTIVITY);
                break;
            }
            case R.id.records: {
                intent = new Intent(this, RecordsActivity.class);
                startActivity(intent);
                break;
            }
            case R.id.options: {
                intent = new Intent(this, OptionsActivity.class);
                intent.putExtra(OptionsActivity.OPTIONS_KEY_IS_SWIPE, isSwipe);
                startActivityForResult(intent, OPTIONS_ACTIVITY);
                break;
            }
            case R.id.about_program: {
                intent = new Intent(this, TextActivity.class);
                startActivity(intent);
                break;
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == TYPE_ACTIVITY && resultCode == RESULT_OK) {
            TypeEnum newLabyrinth = (TypeEnum) data.getSerializableExtra(TypeActivity.TYPE_KEY_TYPE);
            if (newLabyrinth != labyrinth) {
                state = StateGameEnum.NoGame;
                Toast.makeText(getApplicationContext(), getResources().getString(R.string.lab_changed), Toast.LENGTH_SHORT).show();
            }
            labyrinth = newLabyrinth;
        } else if (requestCode == SPEED_ACTIVITY && resultCode == RESULT_OK) {
            SpeedEnum newSpeed = (SpeedEnum) data.getSerializableExtra(SpeedActivity.SPEED_KEY_SPEED);
            if (newSpeed != speed) {
                state = StateGameEnum.NoGame;
                Toast.makeText(getApplicationContext(), getResources().getString(R.string.speed_changed), Toast.LENGTH_SHORT).show();
            }
            speed = newSpeed;
        }
        else if (requestCode == OPTIONS_ACTIVITY && resultCode == RESULT_OK) {
            isSwipe = data.getBooleanExtra(OptionsActivity.OPTIONS_KEY_IS_SWIPE, false);
        }
    }

    private void initializeButtonComponent(int[] views) {
        buttons = new Button[views.length];
        for (int i = 0; i < views.length; i++) {
            buttons[i] = findViewById(views[i]);
            buttons[i].setOnClickListener(this);
        }
    }
}
