package  com.paint.paint.Shapes;

public class Ellipse extends Shape{
    public int radiusX = 50;
    public int radiusY = 30;

    public Ellipse() {
        this.name = "Ellipse";
        this.setcolor("#2c7bb6");
    }

    public Ellipse(Ellipse target) {
        super(target);
        if (target != null) {
            this.radiusX = target.radiusX;
            this.radiusY = target.radiusY;
            this.name = "Ellipse";
            this.setcolor("#2c7bb6");
        }
    }

    @Override
    public Shape clone() {
        return new Ellipse(this);
    }
}
