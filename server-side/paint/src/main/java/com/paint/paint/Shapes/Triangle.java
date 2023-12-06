package com.paint.paint.Shapes;

public class Triangle extends Shape{
    public int sides;
    public int radius;

    public Triangle() {
        this.name = "Triangle";
    }

    public Triangle(Triangle target) {
        super(target);
        if (target != null) {
            this.sides = target.sides;
            this.radius = target.radius;
        }
    }

    @Override
    public Shape clone() {
        return new Triangle(this);
    }
}
