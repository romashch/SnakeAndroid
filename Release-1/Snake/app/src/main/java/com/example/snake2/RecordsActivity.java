package com.example.snake2;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import java.io.Serializable;

public class RecordsActivity extends AppCompatActivity implements View.OnClickListener {

    private int[] buttons1 = new int[]{R.id.all_records, R.id.without_labyrinth, R.id.box_labyrinth,
            R.id.tunnel_labyrinth, R.id.mill_labyrinth, R.id.room_labyrinth,
            R.id.change_labyrinths};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_records);
        Button[] buttons = InitializeButtonComponent(buttons1);
        getIntent();
        for (Button button : buttons) {
            button.setOnClickListener(this);
        }
    }

    @Override
    public void onClick(View view) {
        boolean isAll = view.getId() == R.id.all_records;
        DBHelper dbHelper = new DBHelper(this);
        SQLiteDatabase database = dbHelper.getWritableDatabase();
        String orderBy = DBHelper.COL_SCORE + " desc";
        String[] columns;
        Cursor cursor;
        if (!isAll) {
            String selection = DBHelper.COL_LABYRINTH + " = ?";
            String[] selectionArgs = new String[]{labIdToLabString(view)};
            columns = new String[]{DBHelper.COL_LABYRINTH, DBHelper.COL_SCORE};
            cursor = database.query(true, DBHelper.TABLE_OF_RECORDS, columns, selection, selectionArgs, null, null, orderBy, String.valueOf(Records2Activity.MAX_ROW));
        } else {
            columns = new String[]{DBHelper.COL_SCORE};
            cursor = database.query(true, DBHelper.TABLE_OF_RECORDS, columns, null, null, null, null, orderBy, String.valueOf(Records2Activity.MAX_ROW));
        }
        Intent intent = new Intent(this, Records2Activity.class);
        if (cursor.moveToFirst()) {
            intent.putExtra(Records2Activity.LAB_KEY, labIdToLabString(view));
            int[] records = new int[cursor.getCount()];
            int scoreIndex = cursor.getColumnIndex(DBHelper.COL_SCORE);
            int i = 0;
            do {
                records[i] = cursor.getInt(scoreIndex);
                i++;
            }
            while (cursor.moveToNext());
            intent.putExtra(Records2Activity.RECORDS_KEY, (Serializable) records);
            startActivity(intent);
        } else {
            intent.putExtra(Records2Activity.LAB_KEY, Records2Activity.EMPTY_DB);
            startActivity(intent);
        }
        cursor.close();
        dbHelper.close();
    }

    private String labIdToLabString(View view) {
        String value = null;
        switch (view.getId()) {
            case R.id.all_records:
                value = Records2Activity.ALL_LAB;
                break;
            case R.id.without_labyrinth:
                value = DBHelper.WITHOUT_LAB;
                break;
            case R.id.box_labyrinth:
                value = DBHelper.BOX_LAB;
                break;
            case R.id.tunnel_labyrinth:
                value = DBHelper.TUNNEL_LAB;
                break;
            case R.id.mill_labyrinth:
                value = DBHelper.MILL_LAB;
                break;
            case R.id.room_labyrinth:
                value = DBHelper.ROOM_LAB;
                break;
            case R.id.change_labyrinths:
                value = DBHelper.CHANGE_LAB;
                break;
        }
        return value;
    }

    private Button[] InitializeButtonComponent(int[] views) {
        Button[] buttons = new Button[views.length];
        for (int i = 0; i < views.length; i++) {
            buttons[i] = findViewById(views[i]);
        }
        return buttons;
    }
}
