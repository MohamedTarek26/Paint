package com.paint.paint.events;

import com.paint.paint.ShapeManager.Director;
import com.paint.paint.Shapes.Shape;

public class RemoveShapeEvent implements Event{

    private Director director;
    private int shapeIndex;
    private Shape shape;

    private boolean isRemoved = false;

    public RemoveShapeEvent(Director director, int shapeIndex){
        this.director = director;
        this.shapeIndex = shapeIndex;
    }


    @Override
    public void apply() {
        shape = director.getShapeFromRegistry(shapeIndex).clone();
        director.removeShape(shapeIndex);
        isRemoved = true;
    }

    @Override
    public void revert() {
        if (isRemoved) {
            director.addShape(shapeIndex, shape);
        }
    }
    
}
