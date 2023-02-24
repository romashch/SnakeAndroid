package com.example.snake2;

public class Coordinate {
    private static final int WIDTH = 16;
    private static int HEIGHT = 14;
    private int x;
    private int y;

    public Coordinate() {
        x = 0;
        y = 0;
    }

    public Coordinate(int x, int y) {
        x %= WIDTH;
        y %= HEIGHT;
        if (x < 0) x += WIDTH;
        if (y < 0) y += HEIGHT;
        this.x = x;
        this.y = y;
    }

    static void setHEIGHT(int height)
    {
        HEIGHT = height;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public Coordinate stepRight() {
        return new Coordinate(x + 1, y);
    }

    public Coordinate stepDown() {
        return new Coordinate(x, y + 1);
    }

    public Coordinate stepDiag() {
        return new Coordinate(x + 1, y + 1);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null || getClass() != obj.getClass()) return false;
        Coordinate c = (Coordinate) obj;
        if (getX() != c.getX()) return false;
        return getY() == c.getY();
    }
}
