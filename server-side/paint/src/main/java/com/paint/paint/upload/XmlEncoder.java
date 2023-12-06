package com.paint.paint.upload;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

//import com.paint.paint.ShapeManager.*;
import com.paint.paint.Shapes.Shape;

import java.beans.XMLEncoder;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.util.ArrayList;

@XmlRootElement
class XMLShapes {
    private ArrayList<Shape> shapes;

    @XmlElement(name="shape")
    public ArrayList<Shape> getShapes(){
        return shapes;
    }

    public void setShapes(ArrayList<Shape> shapes){
        this.shapes = shapes;
    }
}


public class XmlEncoder {
    public static void encodeToXml(ArrayList<Shape> shapeList, String filePath) throws JAXBException {
        XMLShapes wrapper = new XMLShapes();
        wrapper.setShapes(shapeList);

       try (XMLEncoder encoder = new XMLEncoder(new BufferedOutputStream(new FileOutputStream(filePath)))) {
            encoder.writeObject(shapeList);
        } catch (FileNotFoundException e) {
            System.err.println("File not found: " + e.getMessage());
        }
    }
}
