package  com.paint.paint.Shapes;

public class Circle extends Shape {

    public int radius;

    public Circle() {
        this.name = "Circle";
    }

    public Circle(Circle target) {
        super(target);
        if (target != null) {
            this.radius = target.radius;
        }
    }

    @Override
    public Shape clone() {
        return new Circle(this);
    }
}