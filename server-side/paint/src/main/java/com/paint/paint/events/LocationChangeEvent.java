package com.paint.paint.events;

import com.paint.paint.ShapeManager.Director;
public class LocationChangeEvent implements Event {
    private float oldX;
    private float oldY;
    private float newX;
    private float newY;
    private Director director; 
    private int shapeIndex;

    public LocationChangeEvent(Director director, int shapeIndex,float oldX, float oldY,float newX,float newY){
        this.oldX = oldX;
        this.oldY = oldY;
        this.newX = newX;
        this.newY = newY;
        this.director = director;
        this.shapeIndex = shapeIndex;
    }

        @Override
    public void apply(){
        director.moveShape(shapeIndex, newX, newY);
    }

    @Override
    public void revert(){
        director.moveShape(shapeIndex, oldX, oldY);

    }

    public float getOldX() {
        return oldX;
    }

    public float getOldY() {
        return oldY;
    }

    public float getNewX() {
        return newX;
    }

    public float getNewY() {
        return newY;
    }

    public Director getDirector(){
        return director;
    }

    public int getShapeIndex(){
        return shapeIndex;
    }
}
