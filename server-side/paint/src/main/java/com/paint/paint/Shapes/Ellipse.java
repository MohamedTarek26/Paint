package  com.paint.paint.Shapes;

public class Ellipse extends Shape{
    public int radiusX;
    public int radiusY;

    public Ellipse() {
    }

    public Ellipse(Ellipse target) {
        super(target);
        if (target != null) {
            this.radiusX = target.radiusX;
            this.radiusY = target.radiusY;
        }
    }

    @Override
    public Shape clone() {
        return new Ellipse(this);
    }
}
