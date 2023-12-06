package com.paint.paint.ShapeManager;

import java.util.ArrayList;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import com.paint.paint.Shapes.Shape;

@XmlRootElement
public class XMLShapes {
    private ArrayList<Shape> shapes;

    @XmlElement(name="shape")
    public ArrayList<Shape> getShapes(){
        return shapes;
    }
    
    public void setShapes(ArrayList<Shape> shapes){
        this.shapes = shapes;
    }
}
