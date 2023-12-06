package com.paint.paint.ShapeManager;
import com.paint.paint.Shapes.*;
public class Director {
    ShapeRegistry registry = new ShapeRegistry();
    public Director() {
    }
    public void removeShape(int Id) {
        registry.remove(Id);
    }
    public void putShapeOnTop(int Id) {
        Shape shape=this.getShapeFromRegistry(Id);
        registry.putOnTop(Id, shape);
    }
    public Shape getShape(String shapeType) {
        Shape shape = ShapesFactory.getShape(shapeType);
        registry.put(shape);
        return shape;
    }
    public Shape CopyShape(int Id) {
        return registry.copy(Id);
    }
    public Shape getShapeFromRegistry(int Id) {
        return registry.get(Id);
    }
    public void putShapeAt(int Id, int oldKey, int newKey) {
        Shape shape=this.getShapeFromRegistry(Id);
        registry.putAt(oldKey, newKey, shape);
    }
    public void moveShape(int Id, float x, float y) {
        Shape shape = registry.get(Id);
        shape.setx(x);
        shape.sety(y);
    }
    public void resizeShape(int Id, float x, float y) {
        Shape shape = registry.get(Id);
        shape.setScaleX(x);
        shape.setScaleY(y);
    }
    public void rotateShape(int Id, float rotation) {
        Shape shape = registry.get(Id);
        shape.setRotation(rotation);
    }
    public void changeColor(int Id, String color) {
        Shape shape = registry.get(Id);
        shape.setcolor(color);
    }
}
