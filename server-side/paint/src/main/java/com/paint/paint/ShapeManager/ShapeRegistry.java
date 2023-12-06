package com.paint.paint.ShapeManager;
import com.paint.paint.Shapes.Shape;
import java.util.HashMap;
import java.util.Map;
import java.util.ArrayList;
public class ShapeRegistry {
    private ArrayList<Shape> cache = new ArrayList<Shape>();

    public ShapeRegistry() {
    }

    public void put(Shape shape) {
        cache.add(shape);
    }
    public int size() {
        return cache.size();
    }
    public void remove(int key) {
        cache.remove(key);
    }
    public void putOnTop(int key, Shape shape) {
        cache.add(shape);
        cache.remove(key);
    }
    public void putAt(int oldKey,int newKey,Shape shape)
    {
        cache.add(newKey,shape);
        cache.remove(oldKey);
    }
    public Shape copy(int key) {
        Shape shape = cache.get(key).clone();
        cache.add(shape);
        return shape;
    }
    public Shape get(int key) {
        return cache.get(key);
    }
}