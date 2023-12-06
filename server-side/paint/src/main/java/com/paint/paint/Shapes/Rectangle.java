package com.paint.paint.Shapes;

public class Rectangle extends Shape {
    public int width;
    public int height;

    public Rectangle() {
        this.name = "Rectangle";
    }

    public Rectangle(Rectangle target) {
        super(target);
        if (target != null) {
            this.width = target.width;
            this.height = target.height;
        }
    }

    @Override
    public Shape clone() {
        return new Rectangle(this);
    }
}
