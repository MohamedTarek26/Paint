package  com.paint.paint.Shapes;

public class Ellipse extends Shape{
    public int radiusX = 50;
    public int radiusY = 30;

    public Ellipse() {
        this.name = "Ellipse";
        this.setcolor("Blue");
    }

    public Ellipse(Ellipse target) {
        super(target);
        if (target != null) {
            this.radiusX = target.radiusX;
            this.radiusY = target.radiusY;
            this.name = "Ellipse";
            this.setcolor("Blue");
        }
    }

    @Override
    public Shape clone() {
        return new Ellipse(this);
    }
}
