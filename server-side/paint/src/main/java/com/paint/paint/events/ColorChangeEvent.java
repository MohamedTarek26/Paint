package com.paint.paint.events;

import com.paint.paint.ShapeManager.Director;

public class ColorChangeEvent implements Event {
    private String oldColor;
    private String newColor;
    private Director director; 
    private int shapeIndex;

    public ColorChangeEvent(Director director, int shapeIndex,String oldColor, String newColor){
        this.oldColor = oldColor;
        this.newColor = newColor;
        this.director = director;
        this.shapeIndex = shapeIndex;
    }

    @Override
    public void apply(){
        director.changeColor(shapeIndex, newColor);
    }

    @Override
    public void revert(){
        director.changeColor(shapeIndex, oldColor);
    }

    public String getOldColor() {
        return oldColor;
    }

    public String getNewColor() {
        return newColor;
    }

    public Director getDirector(){
        return director;
    }

    public int getShapeIndex(){
        return shapeIndex;
    }
}
