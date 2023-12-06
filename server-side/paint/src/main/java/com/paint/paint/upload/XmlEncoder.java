package com.paint.paint.upload;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

//import com.paint.paint.ShapeManager.*;
import com.paint.paint.Shapes.Shape;

import java.io.File;
import java.util.ArrayList;

@XmlRootElement
class  XMLShapes {
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

        // Create JAXB context and marshaller
        JAXBContext context = JAXBContext.newInstance(XMLShapes.class);
        Marshaller marshaller = context.createMarshaller();

        // Marshal to XML and save to file
        marshaller.marshal(wrapper, new File(filePath));
    }
}
