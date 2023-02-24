package com.example.snake2;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;

import static com.example.snake2.TypeCellEnum.BonusEat;
import static com.example.snake2.TypeCellEnum.Wall;

public class Field extends View {
    private final int width = 16;
    private int height = 14;
    private Paint paint = new Paint();
    private TypeCellEnum[][] map;
    private boolean isDarkBlueBonusEat = true;
    private boolean isRedHeadSnake = true;
    private boolean isNoGame = false;

    public void endGame() {
        isNoGame = true;
        isRedHeadSnake = false;
    }

    public void setMap(TypeCellEnum[][] map) {
        this.map = map;
    }

    public Field(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
    }

    public void setHeight(int height) {
        this.height = height;
        Coordinate.setHEIGHT(height);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        boolean isBonusEat = false;
        if (map != null) {
            float cellSize = getWidth() / width;
            for (int x = 0; x < width; x++) {
                for (int y = 0; y < height; y++) {
                    switch (map[x][y]) {
                        case Wall:
                            paint.setColor(getResources().getColor(R.color.gray5));
                            break;
                        case HeadSnake:
                            if (!isNoGame || isRedHeadSnake) {
                                paint.setColor(getResources().getColor(R.color.head_snake));
                            } else {
                                paint.setColor(getResources().getColor(R.color.yellow_main));
                            }
                            break;
                        case BodySnake:
                            paint.setColor(getResources().getColor(R.color.body_snake));
                            break;
                        case Eat:
                            paint.setColor(getResources().getColor(R.color.eat));
                            break;
                        case BonusEat:
                            if (isDarkBlueBonusEat || isNoGame) {
                                paint.setColor(getResources().getColor(R.color.blue_four));
                            } else {
                                paint.setColor(getResources().getColor(R.color.blue_two));
                            }
                            break;
                        default:
                            paint.setColor(getResources().getColor(R.color.white));
                            break;
                    }
                    if (map[x][y] == Wall) {
                        canvas.drawRoundRect(new RectF(x * cellSize, y * cellSize, (x + 1) * cellSize, (y + 1) * cellSize), cellSize / 3, cellSize / 3, paint);
                    } else if (map[x][y] != BonusEat) {
                        canvas.drawCircle(x * cellSize + cellSize / 2f, y * cellSize + cellSize / 2f,
                                cellSize / 2f, paint);
                    } else if (!isBonusEat) {
                        canvas.drawCircle(x * cellSize + cellSize,
                                y * cellSize + cellSize, cellSize, paint);
                        isBonusEat = true;
                    }
                }
            }
            if (isBonusEat)
                isDarkBlueBonusEat = !isDarkBlueBonusEat;
            else
                isDarkBlueBonusEat = true;
            if (isNoGame)
                isRedHeadSnake = !isRedHeadSnake;
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        setMeasuredDimension(getMeasuredWidth(), getMeasuredWidth() / width * height);
    }
}