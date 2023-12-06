package com.paint.paint.events;

import com.paint.paint.ShapeManager.Director;
import com.paint.paint.Shapes.Shape;

public class RemoveShapeEvent implements Event{

    private Director director;
    private int shapeIndex;
    private Shape shape;

    public RemoveShapeEvent(Director director, int shapeIndex){
        this.director = director;
        this.shapeIndex = shapeIndex;
    }


    @Override
    public void apply() {
        shape = director.getShapeFromRegistry(shapeIndex).clone();
        director.removeShape(shapeIndex);
        shapeIndex = -1;
    }

    @Override
    public void revert() {
        if (shapeIndex != -1) {
            director.addShape(shapeIndex, shape);
        }
    }
    
}
