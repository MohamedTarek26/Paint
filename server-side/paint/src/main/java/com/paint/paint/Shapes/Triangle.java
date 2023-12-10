package com.paint.paint.Shapes;

public class Triangle extends Shape{
    public int sides = 3;
    public int radius = 90;

    public Triangle() {
        this.name = "Triangle";
        this.setcolor("Orange");
    }

    public Triangle(Triangle target) {
        super(target);
        if (target != null) {
            this.sides = target.sides;
            this.radius = target.radius;
            this.name = "Triangle";
            this.setcolor("Orange");
        }
    }

    @Override
    public Shape clone() {
        return new Triangle(this);
    }
}
