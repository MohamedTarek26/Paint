package com.paint.paint.events;

import com.paint.paint.ShapeManager.Director;
import com.paint.paint.Shapes.Shape;

public class CopyEvent implements Event{

    private Director director;
    private int shapeIndex;
    private int newShapeIndex = -1;
    private Shape createdShape;

    public CopyEvent(Director director, int shapeIndex){
        this.director = director;
        this.shapeIndex = shapeIndex;
    }


    @Override
    public void apply() {
        director.copyShape(shapeIndex);
        newShapeIndex = director.getRegistrySize() - 1;
    }

    @Override
    public void revert() {
        if (newShapeIndex != -1) {
            director.removeShape(newShapeIndex);
        }
    }

    public Shape getCreatedShape(){
        return createdShape;
    }
    
}
