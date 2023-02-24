package com.example.snake2;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

public class Records2Activity extends AppCompatActivity {

    private static final int DIALOG_1 = 1;
    private static final int DIALOG_2 = 2;
    private String labyrinth;
    public static final int MAX_ROW = 10;
    public static final String RECORDS_KEY = "RECORDS_KEY";
    public static final String ALL_LAB = "all";
    public static final String LAB_KEY = "LAB_KEY";
    private TextView[] placeTextViews;
    private TextView[] scoreTextViews;
    private TextView empty;
    private LinearLayout[] rows;
    private int[] records;
    public static final String EMPTY_DB = "Empty database";
    private boolean isEmpty;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_records2);
        Intent intent = getIntent();
        empty = findViewById(R.id.empty_db);
        rows = initializeLinearLayouts();
        placeTextViews = initializePlaceTextViews();
        scoreTextViews = initializeScoreTextViews();
        labyrinth = intent.getStringExtra(LAB_KEY);
        assert labyrinth != null;
        isEmpty = labyrinth.equals(EMPTY_DB);
        if (labyrinth.equals(EMPTY_DB)) {
            outputEmptyRecords();
        } else {
            records = (int[]) intent.getSerializableExtra(RECORDS_KEY);
            outputRecords();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_clear, menu);
        return true;
    }

    @Override
    protected Dialog onCreateDialog(int id) {
        if (id == DIALOG_1) {
            String title = getResources().getString(R.string.attention);
            String message = getResources().getString(R.string.are_you_sure_1);
            String yesString = getResources().getString(R.string.yes);
            String noString = getResources().getString(R.string.no);
            AlertDialog.Builder adb = new AlertDialog.Builder(this);
            adb.setTitle(title);
            adb.setMessage(message);
            adb.setPositiveButton(yesString, myDialogClickListener);
            adb.setNegativeButton(noString, myDialogClickListener);
            return adb.create();
        } else if (id == DIALOG_2) {
            String message = getResources().getString(R.string.table_is_empty);
            String yesString = getResources().getString(R.string.ok);
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage(message)
                    .setPositiveButton(yesString, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                        }
                    });
            return builder.create();
        }
        return super.onCreateDialog(id);
    }

    DialogInterface.OnClickListener myDialogClickListener = new DialogInterface.OnClickListener() {
        public void onClick(DialogInterface dialog, int which) {
            switch (which) {
                case Dialog.BUTTON_POSITIVE:
                    deleteDatabase();
                    finish();
                    break;
                case Dialog.BUTTON_NEGATIVE:
                    break;
            }
        }
    };

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.clear) {
            if (!isEmpty) {
                showDialog(DIALOG_1);
            } else {
                showDialog(DIALOG_2);
            }
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void deleteDatabase() {
        DBHelper dbHelper = new DBHelper(this);
        SQLiteDatabase database = dbHelper.getWritableDatabase();
        String whereClause = DBHelper.COL_LABYRINTH + " = ?";
        String[] whereArgs = new String[]{labyrinth};
        if (!labyrinth.equals(ALL_LAB)) {
            database.delete(DBHelper.TABLE_OF_RECORDS, whereClause, whereArgs);
        } else {
            database.delete(DBHelper.TABLE_OF_RECORDS, null, null);
        }
        dbHelper.close();
        isEmpty = true;
        Toast.makeText(getApplicationContext(), getResources().getString(R.string.table_is_cleared), Toast.LENGTH_SHORT).show();
    }

    private void outputRecords() {
        LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) empty.getLayoutParams();
        layoutParams.height = 0;
        for (int i = 0; i < MAX_ROW; i++) {
            if (i < records.length) {
                placeTextViews[i].setText(String.valueOf(i + 1));
                scoreTextViews[i].setText(String.valueOf(records[i]));
            } else {
                layoutParams = (LinearLayout.LayoutParams) rows[i].getLayoutParams();
                layoutParams.height = 0;
            }
        }
    }

    private void outputEmptyRecords() {
        LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) empty.getLayoutParams();
        layoutParams.height = LinearLayout.LayoutParams.WRAP_CONTENT;
        for (int i = 0; i < MAX_ROW; i++) {
            layoutParams = (LinearLayout.LayoutParams) rows[i].getLayoutParams();
            layoutParams.height = 0;
        }
    }

    private LinearLayout[] initializeLinearLayouts() {
        LinearLayout[] res = new LinearLayout[MAX_ROW];
        res[0] = findViewById(R.id.row1);
        res[1] = findViewById(R.id.row2);
        res[2] = findViewById(R.id.row3);
        res[3] = findViewById(R.id.row4);
        res[4] = findViewById(R.id.row5);
        res[5] = findViewById(R.id.row6);
        res[6] = findViewById(R.id.row7);
        res[7] = findViewById(R.id.row8);
        res[8] = findViewById(R.id.row9);
        res[9] = findViewById(R.id.row10);
        return res;
    }

    private TextView[] initializeScoreTextViews() {
        TextView[] res = new TextView[MAX_ROW];
        res[0] = findViewById(R.id.score1);
        res[1] = findViewById(R.id.score2);
        res[2] = findViewById(R.id.score3);
        res[3] = findViewById(R.id.score4);
        res[4] = findViewById(R.id.score5);
        res[5] = findViewById(R.id.score6);
        res[6] = findViewById(R.id.score7);
        res[7] = findViewById(R.id.score8);
        res[8] = findViewById(R.id.score9);
        res[9] = findViewById(R.id.score10);
        return res;
    }

    private TextView[] initializePlaceTextViews() {
        TextView[] res = new TextView[MAX_ROW];
        res[0] = findViewById(R.id.place1);
        res[1] = findViewById(R.id.place2);
        res[2] = findViewById(R.id.place3);
        res[3] = findViewById(R.id.place4);
        res[4] = findViewById(R.id.place5);
        res[5] = findViewById(R.id.place6);
        res[6] = findViewById(R.id.place7);
        res[7] = findViewById(R.id.place8);
        res[8] = findViewById(R.id.place9);
        res[9] = findViewById(R.id.place10);
        return res;
    }
}