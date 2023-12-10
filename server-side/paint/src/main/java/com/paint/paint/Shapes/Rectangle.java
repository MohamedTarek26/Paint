package com.paint.paint.Shapes;

public class Rectangle extends Shape {
    public int width = 200;
    public int height = 100;

    public Rectangle() {
        this.name = "Rectangle";
        this.setcolor("#ff0266");
    }

    public Rectangle(Rectangle target) {
        super(target);
        if (target != null) {
            this.width = target.width;
            this.height = target.height;
            this.name = "Rectangle";
        }
    }

    @Override
    public Shape clone() {
        return new Rectangle(this);
    }
}
