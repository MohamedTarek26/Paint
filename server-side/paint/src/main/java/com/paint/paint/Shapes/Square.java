package com.paint.paint.Shapes;

public class Square extends Shape {
    public int width = 300;
    public int height = 300;

    public Square() {
        this.name = "Square";
        this.setcolor("#ffde86");
    }

    public Square(Square target) {
        super(target);
        if (target != null) {
            this.width = target.width;
            this.height = target.height;
            this.name = "Square";
        }
    }

    @Override
    public Shape clone() {
        return new Square(this);
    }
}
