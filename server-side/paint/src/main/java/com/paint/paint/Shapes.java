package com.paint.paint;
import java.util.*;

abstract class Shape implements Cloneable {
    private static int nextId = 1;
    private int id;
    public Shape() {
        this.id = nextId++;
    }
    public int getId() {
        return id;
    }
    @Override
    protected Shape clone() throws CloneNotSupportedException {
        Shape clone = (Shape) super.clone();
        clone.id = nextId++;
        return clone;
    }
}

class Line extends Shape {
    public Line() {
        super();
    }
}
class Rectangle extends Shape {
    public Rectangle() {
        super();
    }
}
class Square extends Shape {
    public Square() {
        super();
    }
}
class Circle extends Shape {
    public Circle() {
        super();
    }
}
class Ellipse extends Shape {
    public Ellipse() {
        super();
    }
}
class Triangle extends Shape {
    public Triangle() {
        super();
    }
}

class ShapeFactory {
    private final List<Shape> createdShapes = new ArrayList<>();

    public Shape getShape(String shapeType) {
        Shape shape;
        shape = switch (shapeType.toLowerCase()) {
                case "line" -> new Line();
                case "rectangle" -> new Rectangle();
                case "circle" -> new Circle();
                case "ellipse" -> new Ellipse();
                case "square" -> new Square();
                case "triangle" -> new Triangle();
                default -> throw new IllegalArgumentException("Invalid shape type");
            };
            createdShapes.add(shape);
        return shape;
    }
    public Shape copyShapeById(int id) {
        for (Shape shape : createdShapes) {
            if (shape.getId() == id) {
                try {
                    return shape.clone();
                } catch (CloneNotSupportedException e) {
                    e.printStackTrace();
                }
            }
        }
        return null;
    }
    public Shape getShapeById(int id) {
        for (Shape shape : createdShapes) {
            if (shape.getId() == id) {
                    return shape;
            }
        }
        System.out.println("Shape not found");
        return null;
    }

    public List<Shape> getCreatedShapes() {
        return createdShapes;
    }
}
