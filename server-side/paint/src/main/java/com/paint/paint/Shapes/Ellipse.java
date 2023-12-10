package  com.paint.paint.Shapes;

public class Ellipse extends Shape{
    public int radiusX = 300;
    public int radiusY = 150;

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
        }
    }

    @Override
    public Shape clone() {
        return new Ellipse(this);
    }
}
