package com.example.snake2;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class GameEngineering {
    public static final int STEP_CHANGE_LAB_INDICATOR = 10;
    public static final int MAX_CHANGE_LAB_INDICATOR = 50;
    public static int MAX_BONUS_EAT_INDICATOR;
    public static final int STEP_BONUS_EAT_INDICATOR = 1;
    private int progressChangeLab;
    private int progressBonusEat;
    private int height;
    private int width;
    private TypeCellEnum[][] map;
    private StateGameEnum state = StateGameEnum.Running;
    private TypeEnum labyrinth;
    private TypeEnum currentChangeLabyrinth;
    private int numOfScores = 0;
    private List<Coordinate> walls = new ArrayList<>();
    private List<Coordinate> snake = new ArrayList<>();
    private List<Coordinate> bonusEat = new ArrayList<>();
    private Coordinate eat;
    private DirectionEnum direction;
    private Random random;
    private int countDownToBonusEat;
    private int wasBonusEat = 2;
    private boolean isLongHeight;

    public GameEngineering(int width, int height, TypeEnum lab) {
        this.width = width;
        this.height = height;
        isLongHeight = width < height;
        MAX_BONUS_EAT_INDICATOR = (width + height) / 2;
        map = new TypeCellEnum[width][height];
        for (int i = 0; i < width; i++)
            for (int j = 0; j < height; j++)
                map[i][j] = TypeCellEnum.Empty;
        labyrinth = lab;
        if (labyrinth == TypeEnum.Change) {
            progressChangeLab = 0;
            currentChangeLabyrinth = TypeEnum.Box;
            addWalls(currentChangeLabyrinth);
        } else
            addWalls(labyrinth);
        setBeginDirection(labyrinth);
        addSnake();
        updateCountDownToBonusEat();
        generateEat();
    }

    public StateGameEnum getStateGame() {
        return state;
    }

    public int getScores() {
        return numOfScores;
    }

    public int getProgressChangeLab() {
        return progressChangeLab;
    }

    public int getProgressBonusEat() {
        return progressBonusEat;
    }

    public boolean isBonusEat() {
        return !bonusEat.isEmpty();
    }

    private Coordinate getHeadSnake() {
        return snake.get(0);
    }

    public boolean setDirection(DirectionEnum direction) {
        boolean condition = (direction == DirectionEnum.Top && this.direction != DirectionEnum.Bottom && this.direction != direction) ||
                (direction == DirectionEnum.Bottom && this.direction != DirectionEnum.Top && this.direction != direction) ||
                (direction == DirectionEnum.Left && this.direction != DirectionEnum.Right && this.direction != direction) ||
                (direction == DirectionEnum.Right && this.direction != DirectionEnum.Left && this.direction != direction);
        if (condition)
            this.direction = direction;
        return condition;
    }

    private void setBeginDirection(TypeEnum lab) {
        if (lab == TypeEnum.Mill)
            direction = DirectionEnum.Top;
        else
            direction = DirectionEnum.Right;
    }

    public void updateCountDownToBonusEat() {
        bonusEat.clear();
        random = new Random();
        countDownToBonusEat = 3 + random.nextInt(3);
    }

    private void generateBonusEat() {
        if (--countDownToBonusEat > 0 || isBonusEat())
            return;
        else {
            random = new Random();
            Coordinate newEat;
            boolean isCoincidence;
            do {
                isCoincidence = false;
                newEat = new Coordinate(random.nextInt(width - 1), random.nextInt(height - 1));
                for (Coordinate c : walls) {
                    if (c.equals(newEat) || c.equals(newEat.stepDown()) ||
                            c.equals(newEat.stepRight()) || c.equals(newEat.stepDiag()))
                        isCoincidence = true;
                }
                for (Coordinate c : snake) {
                    if (c.equals(newEat) || c.equals(newEat.stepDown()) ||
                            c.equals(newEat.stepRight()) || c.equals(newEat.stepDiag()))
                        isCoincidence = true;
                }
                if (eat.equals(newEat) || eat.equals(newEat.stepDown()) ||
                        eat.equals(newEat.stepRight()) || eat.equals(newEat.stepDiag()))
                    isCoincidence = true;
            }
            while (isCoincidence);
            bonusEat.add(newEat);
            bonusEat.add(newEat.stepRight());
            bonusEat.add(newEat.stepDown());
            bonusEat.add(newEat.stepDiag());
            progressBonusEat = MAX_BONUS_EAT_INDICATOR;
        }
    }

    public TypeCellEnum[][] getMap() {
        for (int i = 0; i < width; i++) {
            for (int j = 0; j < height; j++) {
                map[i][j] = TypeCellEnum.Empty;
            }
        }
        for (Coordinate c : walls) {
            map[c.getX()][c.getY()] = TypeCellEnum.Wall;
        }
        for (Coordinate c : snake) {
            map[c.getX()][c.getY()] = TypeCellEnum.BodySnake;
        }
        map[getHeadSnake().getX()][getHeadSnake().getY()] = TypeCellEnum.HeadSnake;
        map[eat.getX()][eat.getY()] = TypeCellEnum.Eat;
        for (Coordinate c : bonusEat) {
            map[c.getX()][c.getY()] = TypeCellEnum.BonusEat;
        }
        return map;
    }

    private void addSnake() {
        snake.add(new Coordinate(width / 2 - 1, height / 2 - 1));
    }

    private void addWalls(TypeEnum labyrinth) {
        int a;
        int b;
        switch (labyrinth) {
            case None:
                break;
            case Box:
                for (int x = 0; x < width; x++) {
                    walls.add(new Coordinate(x, 0));
                    walls.add(new Coordinate(x, height - 1));
                }
                for (int y = 1; y < height - 1; y++) {
                    walls.add(new Coordinate(0, y));
                    walls.add(new Coordinate(width - 1, y));
                }
                break;
            case Tunnel:
                if (!isLongHeight) a = 4;
                else a = 6;
                for (int x = 0; x < width; x++) {
                    walls.add(new Coordinate(x, 0));
                    walls.add(new Coordinate(x, height - 1));
                    if (x == 3) x = width - 5;
                }
                for (int y = 1; y < height - 1; y++) {
                    walls.add(new Coordinate(0, y));
                    walls.add(new Coordinate(width - 1, y));
                    if (y == a) y = height - a - 2;
                }
                for (int x = 4; x < width - 4; x++) {
                    walls.add(new Coordinate(x, a));
                    walls.add(new Coordinate(x, height - a - 1));
                }
                break;
            case Mill:
                if (!isLongHeight) {
                    a = 2;
                    b = 8;
                } else {
                    a = 4;
                    b = 13;
                }
                for (int x = 0; x < 5; x++) {
                    walls.add(new Coordinate(x, height - a - 1));
                    walls.add(new Coordinate(width - x - 1, a));
                }
                for (int y = 0; y < b; y++) {
                    walls.add(new Coordinate(4, y));
                    walls.add(new Coordinate(width - 5, height - y - 1));
                }
                break;
            case Room:
                if (!isLongHeight)
                    a = 4;
                else
                    a = 6;
                for (int x = 0; x < width - 3; x++) {
                    walls.add(new Coordinate(x, 0));
                    if (x == 1) x = 3;
                }
                for (int x = 0; x < width; x++) {
                    walls.add(new Coordinate(x, a));
                    if (x == 6) x = 8;
                }
                for (int x = 0; x < width; x++) {
                    walls.add(new Coordinate(x, height - a - 1));
                }
                for (int y = 1; y < 2; y++) {
                    walls.add(new Coordinate(0, y));
                }
                for (int y = 1; y < a; y++) {
                    walls.add(new Coordinate(6, y));
                }
                for (int y = height - a; y < height; y++) {
                    walls.add(new Coordinate(width - 7, y));
                }
                break;
        }
    }

    //true, если еда съедена змейкой, иначе false
    public boolean update() {
        switch (direction) {
            case Top:
                return updateSnake(0, -1);
            case Bottom:
                return updateSnake(0, 1);
            case Left:
                return updateSnake(-1, 0);
            case Right:
                return updateSnake(1, 0);
        }
        return false;
    }

    private void generateEat() {
        random = new Random();
        Coordinate newEat;
        boolean isCoincidence;
        do {
            isCoincidence = false;
            newEat = new Coordinate(random.nextInt(width), random.nextInt(height));
            for (Coordinate c : walls) {
                if (c.equals(newEat))
                    isCoincidence = true;
            }
            for (Coordinate c : snake) {
                if (c.equals(newEat))
                    isCoincidence = true;
            }
            for (Coordinate c : bonusEat) {
                if (c.equals(newEat))
                    isCoincidence = true;
            }
        }
        while (isCoincidence);
        eat = newEat;
        generateBonusEat();
    }

    //true, если еда съедена змейкой, иначе false
    private boolean updateSnake(int deltaX, int deltaY) {
        boolean isEating = false;
        Coordinate newHead = new Coordinate(getHeadSnake().getX() + deltaX,
                getHeadSnake().getY() + deltaY);
        for (Coordinate c : walls) {
            if (c.equals(newHead))
                state = StateGameEnum.NoGame;
        }
        for (Coordinate c : snake) {
            if (c.equals(newHead) && c != snake.get(snake.size() - 1))
                state = StateGameEnum.NoGame;
        }
        snake.add(0, newHead);
        if (isBonusEat())
            progressBonusEat -= STEP_BONUS_EAT_INDICATOR;
        int i = 0;
        while (i < bonusEat.size() && !bonusEat.get(i).equals(newHead))
            i++;
        if (isBonusEat() && progressBonusEat == 0)
            updateCountDownToBonusEat();
        if (i < bonusEat.size()) {
            numOfScores += progressBonusEat;
            isEating = true;
            updateCountDownToBonusEat();
            wasBonusEat = 0;
        }
        if (newHead.equals(eat)) {
            if (labyrinth == TypeEnum.Change) {
                progressChangeLab++;
                if (progressChangeLab == MAX_CHANGE_LAB_INDICATOR / STEP_CHANGE_LAB_INDICATOR + 1)
                    updateLabyrinth();
            }
            numOfScores++;
            isEating = true;
            generateEat();
        } else if (wasBonusEat > 1)
            snake.remove(snake.size() - 1);
        else
            wasBonusEat++;
        return isEating;
    }

    private void updateLabyrinth() {
        if (labyrinth != TypeEnum.Change)
            return;
        else {
            walls.clear();
            snake.clear();
            switch (currentChangeLabyrinth) {
                case Box:
                    currentChangeLabyrinth = TypeEnum.Tunnel;
                    break;
                case Tunnel:
                    currentChangeLabyrinth = TypeEnum.Mill;
                    break;
                case Mill:
                    currentChangeLabyrinth = TypeEnum.Room;
                    break;
                case Room:
                    currentChangeLabyrinth = TypeEnum.Box;
                    break;
            }
            addWalls(currentChangeLabyrinth);
            addSnake();
            setBeginDirection(currentChangeLabyrinth);
            progressChangeLab = 0;
        }
    }
}