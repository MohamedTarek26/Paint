package  com.paint.paint.Shapes;

public class Circle extends Shape {

    public int radius = 300;

    public Circle() {
        this.name = "Circle";
        this.setcolor("#4dac26");
    }

    public Circle(Circle target) {
        super(target);
        if (target != null) {
            this.radius = target.radius;
            this.name = "Circle";
        }
    }

    @Override
    public Shape clone() {
        return new Circle(this);
    }
}