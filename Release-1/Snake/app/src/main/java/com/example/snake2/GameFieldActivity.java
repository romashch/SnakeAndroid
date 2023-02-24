package com.example.snake2;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.Timer;
import java.util.TimerTask;

public class GameFieldActivity extends Activity implements View.OnClickListener, View.OnTouchListener {
    public static final String GAMEFIELD_KEY_SPEED = "com.example.snake2.GameField.SPEED";
    public static final String GAMEFIELD_KEY_TYPE = "com.example.snake2.GameField.TYPE";
    public static final String GAMEFIELD_KEY_STATE = "com.example.snake2.GameField.STATE";
    public static final String GAMEFIELD_KEY_IS_SWIPE = "com.example.snake2.GameField.IS_SWIPE";

    private int sourcePeriod;
    private final int STEP_SPEED = 50;
    private SpeedEnum speed;
    private TypeEnum labyrinth;

    final private int WIDTH = 16;
    final private int HEIGHT1 = 14;
    final private int HEIGHT2 = 22;
    public StateGameEnum state;
    private int[] directions1 = {R.id.top, R.id.bottom, R.id.left, R.id.right};
    private TextView numOfScoresTextView;
    private int numOfScores = 0;

    private Field field;
    private GameEngineering game;
    private ProgressBar changeLabIndicator;
    private ProgressBar bonusEatIndicator;
    private static final int MAX_COUNT_DOWN_TO_FINISH = 6;
    private int countDownToFinish = MAX_COUNT_DOWN_TO_FINISH;

    private Timer myTimer;
    private Handler uiHandler;
    private int period;

    private boolean isTouch = false;
    private float beginX;
    private float beginY;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_field);
        Intent intent = getIntent();

        isTouch = intent.getBooleanExtra(GAMEFIELD_KEY_IS_SWIPE, false);

        initializeImageButtonComponents(directions1);
        numOfScoresTextView = findViewById(R.id.num_of_scores);
        field = findViewById(R.id.field);

        state = (StateGameEnum) intent.getSerializableExtra(GAMEFIELD_KEY_STATE);

        speed = (SpeedEnum) intent.getSerializableExtra(GAMEFIELD_KEY_SPEED);
        labyrinth = (TypeEnum) intent.getSerializableExtra(GAMEFIELD_KEY_TYPE);
        if (!isTouch) {
            game = new GameEngineering(WIDTH, HEIGHT1, labyrinth);
        } else {
            field.setHeight(HEIGHT2);
            game = new GameEngineering(WIDTH, HEIGHT2, labyrinth);
            LinearLayout footer = findViewById(R.id.footer);
            LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) footer.getLayoutParams();
            layoutParams.weight = 0;
            field.setOnTouchListener(this);
        }

        assert game != null;
        numOfScoresTextView.setText(String.valueOf(game.getScores()));

        if (labyrinth == TypeEnum.Change)
            initializeChangeLabyrinthIndicator();
        initializeBonusEatIndicator();

        state = StateGameEnum.Running;
        period = getPeriod();
    }

    private void initializeChangeLabyrinthIndicator() {
        changeLabIndicator = findViewById(R.id.change_labyrinths_indicator);
        changeLabIndicator.setVisibility(View.VISIBLE);
        changeLabIndicator.setMax(GameEngineering.MAX_CHANGE_LAB_INDICATOR);
        changeLabIndicator.incrementProgressBy(GameEngineering.STEP_CHANGE_LAB_INDICATOR);
        changeLabIndicator.setProgress(0);
    }

    class MyTimerTask extends TimerTask {
        @Override
        public void run() {
            uiHandler.post(new Runnable() {
                @Override
                public void run() {
                    if (game.update())
                        acceleration();
                    state = game.getStateGame();
                    if (state == StateGameEnum.Running) {
                        if (labyrinth == TypeEnum.Change)
                            changeLabIndicator.setProgress(game.getProgressChangeLab() * GameEngineering.STEP_CHANGE_LAB_INDICATOR);
                        updateBonusEatIndicator();
                        numOfScores = scoring();
                        numOfScoresTextView.setText(String.valueOf(numOfScores));
                        field.setMap(game.getMap());
                        field.invalidate();
                    } else if (state == StateGameEnum.NoGame) {
                        if (countDownToFinish == MAX_COUNT_DOWN_TO_FINISH) {
                            addRowInRecordsDatabase();
                            field.endGame();
                        }
                        field.invalidate();
                        countDownToFinish--;
                        if (countDownToFinish == 0) {
                            myTimer.cancel(); // останавливаем таймер!
                            Intent intent = new Intent(GameFieldActivity.this, Text2Activity.class);
                            intent.putExtra(Text2Activity.TEXT2_KEY_TEXT, numOfScores);
                            startActivity(intent);
                            finish(); // завершение работы активности
                        }
                    }
                }
            });
        }
    }

    private void startGame() {
        myTimer = new Timer();
        uiHandler = new Handler();
        myTimer.schedule(new MyTimerTask(), 0, period);
    }

    private void acceleration() {
        if (period > sourcePeriod - STEP_SPEED * 2)
            period -= 10;
    }

    private int scoring() {
        return game.getScores() * (12 - period / STEP_SPEED);
    }

    private void addRowInRecordsDatabase() {
        if (numOfScores == 0)
            return;
        DBHelper dbHelper = new DBHelper(this);
        SQLiteDatabase database = dbHelper.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        String value = enumLabToStringLab(labyrinth);
        contentValues.put(DBHelper.COL_LABYRINTH, value);
        contentValues.put(DBHelper.COL_SCORE, numOfScores);
        database.insert(DBHelper.TABLE_OF_RECORDS, null, contentValues);
        dbHelper.close();
    }

    private void initializeBonusEatIndicator() {
        bonusEatIndicator = findViewById(R.id.bonus_food_indicator);
        bonusEatIndicator.setMax(GameEngineering.MAX_BONUS_EAT_INDICATOR);
        bonusEatIndicator.incrementProgressBy(GameEngineering.STEP_BONUS_EAT_INDICATOR);
    }

    private void updateBonusEatIndicator() {
        if (!game.isBonusEat()) {
            bonusEatIndicator.setVisibility(View.INVISIBLE);
        } else {
            bonusEatIndicator.setVisibility(View.VISIBLE);
            bonusEatIndicator.setProgress(game.getProgressBonusEat() * GameEngineering.STEP_BONUS_EAT_INDICATOR);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        myTimer.cancel(); // останавливаем таймер!
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.top:
                if (game.setDirection(DirectionEnum.Top))
                    restartTimer();
                break;
            case R.id.bottom:
                if (game.setDirection(DirectionEnum.Bottom))
                    restartTimer();
                break;
            case R.id.left:
                if (game.setDirection(DirectionEnum.Left))
                    restartTimer();
                break;
            case R.id.right:
                if (game.setDirection(DirectionEnum.Right))
                    restartTimer();
                break;
        }
    }

    private void restartTimer() {
        myTimer.cancel();
        myTimer = new Timer();
        myTimer.schedule(new MyTimerTask(), 0, period);
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        if (!isTouch)
            return false;
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                beginX = event.getX();
                beginY = event.getY();
                break;
            case MotionEvent.ACTION_UP:
                float endX = event.getX();
                float endY = event.getY();
                if (Math.abs(endX - beginX) > Math.abs(endY - beginY)) {
                    if (endX > beginX) {
                        if (game.setDirection(DirectionEnum.Right))
                            restartTimer();
                    } else if (game.setDirection(DirectionEnum.Left)) {
                        restartTimer();
                    }
                } else if (endY > beginY) {
                    if (game.setDirection(DirectionEnum.Bottom))
                        restartTimer();
                } else if (game.setDirection(DirectionEnum.Top)) {
                    restartTimer();
                }
                myTimer.cancel();
                myTimer = new Timer();
                myTimer.schedule(new MyTimerTask(), 0, period);
                break;
        }
        return true;
    }

    private void initializeImageButtonComponents(int[] directions1) {
        ImageButton[] res = new ImageButton[directions1.length];
        for (int i = 0; i < res.length; i++) {
            res[i] = findViewById(directions1[i]);
            res[i].setOnClickListener(this);
        }
    }

    private int getPeriod() {
        period = STEP_SPEED * (13 - (speed.ordinal() + 1));
        sourcePeriod = period;
        return period;
    }

    private String enumLabToStringLab(TypeEnum typeEnum) {
        switch (typeEnum) {
            case None:
                return DBHelper.WITHOUT_LAB;
            case Box:
                return DBHelper.BOX_LAB;
            case Tunnel:
                return DBHelper.TUNNEL_LAB;
            case Mill:
                return DBHelper.MILL_LAB;
            case Room:
                return DBHelper.ROOM_LAB;
            case Change:
                return DBHelper.CHANGE_LAB;
            default:
                return null;
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onResume() {
        super.onResume();
        startGame();
    }

    @Override
    protected void onPause() {
        super.onPause();
        myTimer.cancel();
    }
}