package  com.paint.paint.Shapes;

public class Circle extends Shape {

    public int radius = 50;

    public Circle() {
        this.name = "Circle";
        this.setcolor("Green");
    }

    public Circle(Circle target) {
        super(target);
        if (target != null) {
            this.radius = target.radius;
            this.name = "Circle";
            this.setcolor("Green");
        }
    }

    @Override
    public Shape clone() {
        return new Circle(this);
    }
}