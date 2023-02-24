package com.example.snake2;

import androidx.appcompat.app.AppCompatActivity;

import android.content.ContentValues;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import static android.app.ProgressDialog.show;

public class TypeActivity extends AppCompatActivity implements RadioGroup.OnCheckedChangeListener {

    public static final String TYPE_KEY_TYPE = "com.example.snake2.Type.TYPE";

    private int types1[] = {
            R.id.radio_without_labyrinth,
            R.id.radio_box_labyrinth,
            R.id.radio_tunnel_labyrinth,
            R.id.radio_mill_labyrinth,
            R.id.radio_room_labyrinth,
            R.id.radio_change_labyrinths
    };

    public TypeEnum labyrinth = TypeEnum.None;

    private RadioButton[] radioButtons = new RadioButton[types1.length];

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_type);
        labyrinth = (TypeEnum) getIntent().getSerializableExtra(TYPE_KEY_TYPE);

        RadioGroup radioGroup = findViewById(R.id.radio_group);
        initializeComponents();
        radioGroup.setOnCheckedChangeListener(this);

        initialChoice();
    }

    @Override
    public void onCheckedChanged(RadioGroup radioGroup, int checkedId) {
        Intent answerIntent = new Intent();
        switch (checkedId) {
            case R.id.radio_without_labyrinth:
                labyrinth = TypeEnum.None;
                break;
            case R.id.radio_box_labyrinth:
                labyrinth = TypeEnum.Box;
                break;
            case R.id.radio_tunnel_labyrinth:
                labyrinth = TypeEnum.Tunnel;
                break;
            case R.id.radio_mill_labyrinth:
                labyrinth = TypeEnum.Mill;
                break;
            case R.id.radio_room_labyrinth:
                labyrinth = TypeEnum.Room;
                break;
            case R.id.radio_change_labyrinths:
                labyrinth = TypeEnum.Change;
                break;
        }
        answerIntent.putExtra(TYPE_KEY_TYPE, labyrinth);
        setResult(RESULT_OK, answerIntent);
    }

    private void initializeComponents() {
        for (int i = 0; i < types1.length; i++)
            radioButtons[i] = findViewById(types1[i]);
    }

    private void initialChoice() {
        switch (labyrinth) {
            case None:
                radioButtons[0].setChecked(true);
                break;
            case Box:
                radioButtons[1].setChecked(true);
                break;
            case Tunnel:
                radioButtons[2].setChecked(true);
                break;
            case Mill:
                radioButtons[3].setChecked(true);
                break;
            case Room:
                radioButtons[4].setChecked(true);
                break;
            case Change:
                radioButtons[5].setChecked(true);
                break;
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        DBHelper dbHelper = new DBHelper(this);
        SQLiteDatabase database = dbHelper.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(DBHelper.COL_TYPE, labyrinth.ordinal());
        database.update(DBHelper.TABLE_OF_SELECTED_PARAMETERS, contentValues, DBHelper.COL_ID + "= ?", new String[]{String.valueOf(1)});
        dbHelper.close();
    }
}