package com.paint.paint.events;

import com.paint.paint.ShapeManager.Director;
import com.paint.paint.Shapes.Shape;

public class CreateShapeEvent implements Event{

    private Director director;
    private String shapeType;
    private int shapeIndex = -1;
    private Shape createdShape;

    public CreateShapeEvent(Director director, String shapeType){
        this.director = director;
        this.shapeType = shapeType;
    }


    @Override
    public void apply() {
        createdShape = director.addShape(shapeType);
        shapeIndex = director.getRegistrySize() - 1;
    }

    @Override
    public void revert() {
        if (shapeIndex != -1) {
            director.removeShape(shapeIndex);
            createdShape = null;
        }
    }

    public Shape getCreatedShape(){
        return createdShape;
    }
    
}
