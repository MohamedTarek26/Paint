package com.paint.paint.Shapes;

public class Line extends Shape{
    public Line() {
    }
    public Line(Line target) {
        super(target);
    }
    @Override
    public Shape clone() {
        return new Line(this);
    }
}
