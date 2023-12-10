package com.paint.paint.Shapes;

public class Line extends Shape{
    public int[] coordinates;
    public Line() {

        this.name = "Line";
        this.coordinates = new int[]{400, 120, 400, 120};
        this.setStroke("Black");
    }
    public Line(Line target) {
        super(target);
        this.name = "Line";
    }
    @Override
    public Shape clone() {
        return new Line(this);
    }
}
