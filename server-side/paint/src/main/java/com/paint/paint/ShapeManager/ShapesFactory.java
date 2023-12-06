package com.paint.paint.ShapeManager;
import com.paint.paint.Shapes.Shape;
import com.paint.paint.Shapes.Circle;
import com.paint.paint.Shapes.Line;
import com.paint.paint.Shapes.Triangle;
import com.paint.paint.Shapes.Rectangle;
import com.paint.paint.Shapes.Ellipse;
public class ShapesFactory {
    public ShapesFactory() {
    }

    public static Shape getShape(String shapeType) {
        if (shapeType == null) {
            return null;
        }
        if (shapeType.equalsIgnoreCase("CIRCLE")) {
            return new Circle();
        } else if (shapeType.equalsIgnoreCase("LINE")) {
            return new Line();
        }
        else if (shapeType.equalsIgnoreCase("TRIANGLE")) {
            return new Triangle();
        } else if (shapeType.equalsIgnoreCase("RECTANGLE")) {
            return new Rectangle();
        }
        else if (shapeType.equalsIgnoreCase("ELLIPSE")) {
            return new Ellipse();
        }
        else {
            return null;
        }

    }
}
