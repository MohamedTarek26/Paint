package com.paint.paint.Shapes;

public class Rectangle extends Shape {
    public int width = 500;
    public int height = 250;

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
            this.setcolor("#ff0266");
        }
    }

    @Override
    public Shape clone() {
        return new Rectangle(this);
    }
}
