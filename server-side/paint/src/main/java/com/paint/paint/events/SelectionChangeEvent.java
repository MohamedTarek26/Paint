package com.paint.paint.events;
import com.paint.paint.ShapeManager.Director;

public class SelectionChangeEvent implements Event{
    private Director director; 
    private int oldShapeIndex;
    private int newShapeIndex;
    private int currentShapeIndex;

    public SelectionChangeEvent(Director director, int shapeIndex) {
        this.oldShapeIndex = shapeIndex;
        this.director = director;
        this.currentShapeIndex = shapeIndex;
    }

    @Override
    public void apply(){
        newShapeIndex = director.putShapeOnTop(oldShapeIndex);
        this.currentShapeIndex = newShapeIndex;
    }

    public void revert(){
        director.putShapeAt(newShapeIndex,oldShapeIndex);
        this.currentShapeIndex = oldShapeIndex;
    }
    
    public Director getDirector(){
        return director;
    }

    public int getCurrentShapeIndex(){
        return currentShapeIndex;
    }

}
