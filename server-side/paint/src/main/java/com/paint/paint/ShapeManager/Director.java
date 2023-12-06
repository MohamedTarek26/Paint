package com.paint.paint.ShapeManager;
import java.util.ArrayList;

import com.paint.paint.Shapes.Shape;
public class Director {
    ShapeRegistry registry = new ShapeRegistry();
    public Director() {
    }
    public void removeShape(int Id) {
        registry.remove(Id);
    }
    public int putShapeOnTop(int Id) {
        Shape shape=this.getShapeFromRegistry(Id);
        registry.putOnTop(Id, shape);
        return registry.size() - 1;
    }
    public Shape addShape(String shapeType) {
        Shape shape = ShapesFactory.getShape(shapeType);
        registry.put(shape);
        return shape;
    }

    public Shape addShape(int Id, String shapeType) {
        Shape shape = ShapesFactory.getShape(shapeType);
        return this.addShape(Id,shape);
    }

    public Shape addShape(int Id, Shape shape) {
        registry.putAt(Id,shape);
        return shape;
    }

    public Shape copyShape(int Id) {
        return registry.copy(Id);
    }
    public Shape getShapeFromRegistry(int Id) {
        return registry.get(Id);
    }
    public void putShapeAt(int oldKey, int newKey) {
        Shape shape=this.getShapeFromRegistry(oldKey).clone();
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

    public void changeStroke(int Id, String stroke) {
        Shape shape = registry.get(Id);
        shape.setStroke(stroke);
    }

    public int getRegistrySize(){
        return registry.size();
    }

     public ArrayList<Shape> getCache(){
        return registry.getCache();
    } 
}
