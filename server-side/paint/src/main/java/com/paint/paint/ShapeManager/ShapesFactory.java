package com.paint.paint.ShapeManager;
import com.paint.paint.Shapes.*;
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
        else if (shapeType.equalsIgnoreCase("SQUARE")) {
            return new Square();
        }
        else {
            return null;
        }

    }
}
