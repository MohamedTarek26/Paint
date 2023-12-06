package com.paint.paint.Shapes;

public abstract class Shape {
    String name;
    private float x = 100;
    private float y = 100;

    private float scaleX = 1;
    private float scaleY = 1;

    private float rotation = 0;

    private String color = "Black";

    private String Stroke = "Transparent";


    public float getx() {
        return x;
    }
    public float gety() {
        return y;
    }
    public String getcolor() {
        return color;
    }
    public float getScaleX() {
        return scaleX;
    }
    public float getScaleY() {
        return scaleY;
    }
    public float getRotation() {
        return rotation;
    }
    public String getStroke() {
        return Stroke;
    }
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public void setx(float x) {
        this.x = x;
    }
    public void sety(float y) {
        this.y = y;
    }
    public void setcolor(String color) {
        this.color = color;
    }
    public void setScaleX(float scaleX) {
        this.scaleX = scaleX;
    }
    public void setScaleY(float scaleY) {
        this.scaleY = scaleY;
    }
    public void setRotation(float rotation) {
        this.rotation = rotation;
    }
    public void setStroke(String Stroke) {
        this.Stroke = Stroke;
    }
    public Shape() {
    }

    public Shape(Shape target) {
        if (target != null) {
            this.x = target.x;
            this.y = target.y;
            this.color = target.color;
        }
    }

    public abstract Shape clone();
}