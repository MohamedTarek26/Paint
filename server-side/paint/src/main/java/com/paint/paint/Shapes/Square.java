package com.paint.paint.Shapes;

public class Square extends Shape {
    public int width = 100;
    public int height = 100;

    public Square() {
        this.name = "Square";
        this.setcolor("Yellow");
    }

    public Square(Square target) {
        super(target);
        if (target != null) {
            this.width = target.width;
            this.height = target.height;
            this.name = "Square";
            this.setcolor("Yellow");
        }
    }

    @Override
    public Shape clone() {
        return new Square(this);
    }
}
