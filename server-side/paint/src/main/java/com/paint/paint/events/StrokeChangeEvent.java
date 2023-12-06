package com.paint.paint.events;

import com.paint.paint.ShapeManager.Director;

public class StrokeChangeEvent implements Event {
    private String oldStroke;
    private String newStroke;
    private Director director; 
    private int shapeIndex;

    public StrokeChangeEvent(Director director, int shapeIndex,String oldStroke, String newStroke){
        this.oldStroke = oldStroke;
        this.newStroke = newStroke;
        this.director = director;
        this.shapeIndex = shapeIndex;
    }

    @Override
    public void apply(){
        director.changeStroke(shapeIndex, newStroke);
    }

    @Override
    public void revert(){
        director.changeStroke(shapeIndex, oldStroke);
    }

    public String getOldStroke() {
        return oldStroke;
    }

    public String getNewStroke() {
        return newStroke;
    }

    public Director getDirector(){
        return director;
    }

    public int getShapeIndex(){
        return shapeIndex;
    }
}
