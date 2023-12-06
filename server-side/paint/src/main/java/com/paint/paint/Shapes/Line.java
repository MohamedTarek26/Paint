package com.paint.paint.Shapes;

public class Line extends Shape{
    public int[] coordinates;
    public Line() {

        this.name = "Line";
        this.coordinates = new int[]{100, 50, 200, 50};
    }
    public Line(Line target) {
        super(target);
        this.name = "Line";
        this.coordinates = new int[]{100, 50, 200, 50};
    }
    @Override
    public Shape clone() {
        return new Line(this);
    }
}
