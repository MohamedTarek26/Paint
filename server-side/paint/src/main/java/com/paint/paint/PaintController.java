package com.paint.paint;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.JAXBException;

import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.paint.paint.ShapeManager.Director;
import com.paint.paint.Shapes.Shape;
import com.paint.paint.events.*;
import com.paint.paint.upload.FileUploadResponse;
import com.paint.paint.upload.FileUploadUtil;
import com.paint.paint.upload.XmlEncoder;


@RestController
@CrossOrigin()
@RequestMapping("/api")
public class PaintController {
    private Director director = new Director();
    private EventsHandler eventsHandler = new EventsHandler();

    @GetMapping("/list")
    public ArrayList<Shape> listShapes(){

        return director.getCache();
    }


    // create a shape according to shapeType by adding a create event and applying it
    @PostMapping("/create")
    public Shape createShape(@RequestBody String shapeType){
        CreateShapeEvent event = new CreateShapeEvent(director, shapeType);
        eventsHandler.addEvent(event);
        event.apply();
        return event.getCreatedShape();
    }

    // copy a shape according to shapeIndex by adding a copy event and applying it
    @PostMapping("/copy")
    public Shape copyShape(@RequestBody int shapeIndex){
        CopyEvent event = new CopyEvent(director, shapeIndex);
        eventsHandler.addEvent(event);
        event.apply();
        return event.getCreatedShape();
    }

    // remove a shape according to shapeIndex by adding a remove event and applying it
    @PostMapping("/remove")
    public void removeShape(@RequestBody int shapeIndex){
        RemoveShapeEvent event = new RemoveShapeEvent(director, shapeIndex);
        eventsHandler.addEvent(event);
        event.apply();
    }

    // move a shape according to shapeIndex and newShapeIndex by adding a move event and applying it
    @PostMapping("/move")
    public void moveShape(@RequestParam int id,@RequestParam int x,@RequestParam int y){
        Shape shape = director.getShapeFromRegistry(id);

        LocationChangeEvent event = new LocationChangeEvent(director,id,shape.getx(),shape.gety(),x,y);
        eventsHandler.addEvent(event);
        event.apply();
    }

    @PostMapping("/transform")
    public void transformShape(@RequestBody int[] indexes){
        int id = indexes[0];
        Shape shape = director.getShapeFromRegistry(id);

        TransformEvent event = new TransformEvent(director,id,shape.getScaleX(),shape.getScaleY(),shape.getRotation(),indexes[1],indexes[2],indexes[3]);
        eventsHandler.addEvent(event);
        event.apply();
    }

    @PostMapping("/color")
    public void changeColor(@RequestParam int id,@RequestParam String color){
        Shape shape = director.getShapeFromRegistry(id);

        ColorChangeEvent event = new ColorChangeEvent(director,id, shape.getcolor(), color);
        eventsHandler.addEvent(event);
        event.apply();
    }

    @PostMapping("/stroke")
    public void changeStroke(@RequestParam int id,@RequestParam String stroke){
        Shape shape = director.getShapeFromRegistry(id);

        StrokeChangeEvent event = new StrokeChangeEvent(director,id, shape.getStroke(), stroke);
        eventsHandler.addEvent(event);
        event.apply();
    }

    @PostMapping("/select")
    public void select(@RequestParam int id){
        SelectionChangeEvent event = new SelectionChangeEvent(director, id);
        
        eventsHandler.addEvent(event);
        event.apply();
    }

    @PostMapping("/undo")
    public void undo(){
        eventsHandler.undo();
    }

    @PostMapping("/redo")
    public void redo(){

        eventsHandler.redo();
    }
    
    @GetMapping("/events_size")
    public int size(){

        return eventsHandler.size();
    }

    //  @GetMapping("/load/json")
    // public ArrayList<Shape> get() {

    //     createShapes shapes = new createShapes();
    //     return shapes.create();

    // }

    @GetMapping("/save/xml")
    public void savexml() throws JAXBException {

        
        String filePath = "src/main/java/com/paint/paint/shapes.xml";

        XmlEncoder.encodeToXml(director.getCache(), filePath);

    }
    
     @PostMapping("/file/upload")
    public ResponseEntity<FileUploadResponse> uploadFile(@RequestParam("file") MultipartFile multipartFile) throws IOException {
        String fileName = StringUtils.cleanPath(multipartFile.getOriginalFilename());
        long size = multipartFile.getSize();
        String fileType = multipartFile.getContentType();
        FileUploadResponse response = new FileUploadResponse();
        FileUploadUtil.saveFile(fileName, multipartFile);
        response.setName(fileName);
        response.setSize(size);
        response.setType(fileType);
        response.setDownloadUri("/paint/file/download?fileName=" + fileName);
        return new ResponseEntity<>(response, HttpStatus.OK);


    }

    

}
