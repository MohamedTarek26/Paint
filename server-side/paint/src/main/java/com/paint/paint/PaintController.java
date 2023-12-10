package com.paint.paint;

import java.beans.XMLDecoder;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.JAXBException;

import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import java.io.File;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import com.paint.paint.ShapeManager.Director;
import com.paint.paint.ShapeManager.XMLShapes;
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
    public ArrayList<Shape> listShapes() {

        return director.getCache();
    }


    // create a shape according to shapeType by adding a create event and applying it
    @PostMapping("/create")
    public Shape createShape(@RequestParam String shapeType) {
        CreateShapeEvent event = new CreateShapeEvent(director, shapeType);
        eventsHandler.addEvent(event);
        event.apply();
        return event.getCreatedShape();
    }

    @GetMapping("/clear")
    public void clear() {
        director.setCache(new ArrayList<Shape>());
        eventsHandler.clear();
    }

    // copy a shape according to shapeIndex by adding a copy event and applying it
    @PostMapping("/copy")
    public Shape copyShape(@RequestParam int shapeIndex) {
        CopyEvent event = new CopyEvent(director, shapeIndex);
        eventsHandler.addEvent(event);
        event.apply();
        event.getCreatedShape();
        Shape shape = (director.getCache()).get(shapeIndex);
        return shape;
    }

    // remove a shape according to shapeIndex by adding a remove event and applying it
    @PostMapping("/remove")
    public void removeShape(@RequestParam int shapeIndex) {
        RemoveShapeEvent event = new RemoveShapeEvent(director, shapeIndex);
        eventsHandler.addEvent(event);
        event.apply();
    }

    // move a shape according to shapeIndex and newShapeIndex by adding a move event and applying it
    @PostMapping("/move")
    public void moveShape(@RequestParam int id, @RequestParam int x, @RequestParam int y) {
        Shape shape = director.getShapeFromRegistry(id);

        LocationChangeEvent event = new LocationChangeEvent(director, id, shape.getx(), shape.gety(), x, y);
        eventsHandler.addEvent(event);
        event.apply();
    }

    @PostMapping("/transform")
    public void transformShape(@RequestParam int id, @RequestParam float scaleX, @RequestParam float scaleY, @RequestParam float rotation, @RequestParam float x, @RequestParam float y) {
        Shape shape = director.getShapeFromRegistry(id);
        if (scaleX == shape.getScaleX() && scaleY == shape.getScaleY() && rotation == shape.getRotation() && x == shape.getx() && y == shape.gety()) {
            return;
        }
        TransformEvent event = new TransformEvent(director, id, shape.getScaleX(), shape.getScaleY(), shape.getRotation(), scaleX, scaleY, rotation, shape.getx(), shape.gety(), x, y);
        eventsHandler.addEvent(event);
        event.apply();
    }

    @PostMapping("/color")
    public void changeColor(@RequestParam int id, @RequestParam String color) {
        Shape shape = director.getShapeFromRegistry(id);

        ColorChangeEvent event = new ColorChangeEvent(director, id, shape.getcolor(), color);
        eventsHandler.addEvent(event);
        event.apply();
    }

    @PostMapping("/stroke")
    public void changeStroke(@RequestParam int id, @RequestParam String stroke) {
        Shape shape = director.getShapeFromRegistry(id);

        StrokeChangeEvent event = new StrokeChangeEvent(director, id, shape.getStroke(), stroke);
        eventsHandler.addEvent(event);
        event.apply();
    }

    @PostMapping("/select")
    public void select(@RequestParam int id) {
        SelectionChangeEvent event = new SelectionChangeEvent(director, id);

        eventsHandler.addEvent(event);
        event.apply();
    }

    @PostMapping("/undo")
    public ArrayList<Shape> undo() {
        eventsHandler.undo();
        return director.getCache();
    }

    @PostMapping("/redo")
    public ArrayList<Shape> redo() {

        eventsHandler.redo();
        return director.getCache();
    }

    @GetMapping("/events_size")
    public int size() {

        return eventsHandler.size();
    }

    @GetMapping("/list_events")
    public ArrayList<Event> listEvents() {

        return eventsHandler.getEvents();
    }


    public ResponseEntity<InputStreamResource> makeFileDownloadable(String filePath) {
        try {
            File file = new File(filePath);
            InputStreamResource resource = new InputStreamResource(new FileInputStream(file));

            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment;filename=" + file.getName())
                    .contentType(MediaType.APPLICATION_OCTET_STREAM)
                    .contentLength(file.length())
                    .body(resource);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return ResponseEntity.badRequest().build();
    }

    @GetMapping("/download/json")
    public ResponseEntity<InputStreamResource> saveJson() {
        ObjectMapper mapper = new ObjectMapper();
        List<Shape> shapes = director.getCache();
        String filePath = "src/main/java/com/paint/paint/saved/shapes.json";

        try {
            mapper.writeValue(new File(filePath), shapes);

        } catch (IOException e) {
            e.printStackTrace();
        }


        return makeFileDownloadable(filePath);
    }


    @GetMapping("/download/xml")
    public ResponseEntity<InputStreamResource> savexml() throws JAXBException {


        String filePath = "src/main/java/com/paint/paint/saved/shapes.xml";
        ArrayList<Shape> shapes = director.getCache();

        XmlEncoder.encodeToXml(shapes, filePath);

        return makeFileDownloadable(filePath);
    }

    @PostMapping("/upload/xml")
    public ResponseEntity<List<Shape>> uploadXml(@RequestParam("file") MultipartFile multipartFile) throws IOException {
        String fileName = StringUtils.cleanPath(multipartFile.getOriginalFilename());
        FileUploadUtil.saveFile(fileName, multipartFile);

        // Deserialize XML file into List<Shape>
        List<Shape> shapes;
        try {
            JAXBContext jaxbContext = JAXBContext.newInstance(XMLShapes.class);
            Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();
            XMLShapes shapesWrapper = (XMLShapes) jaxbUnmarshaller.unmarshal(new File("src/main/java/com/paint/paint/saved/shapes.xml"));
            shapes = shapesWrapper.getShapes();
            eventsHandler.clear();
            director.setCache(shapes);
        } catch (JAXBException e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }

        return ResponseEntity.ok(shapes);
    }

    @PostMapping("/upload/json")
    public List<Shape> uploadJson(@RequestParam("file") MultipartFile multipartFile) throws IOException {
        String fileName = StringUtils.cleanPath(multipartFile.getOriginalFilename());
        FileUploadUtil.saveFile(fileName, multipartFile);

        // Deserialize JSON file into List<Shape>
        ObjectMapper mapper = new ObjectMapper();
        eventsHandler.clear();
        List<Shape> shapes = mapper.readValue(multipartFile.getInputStream(), new TypeReference<List<Shape>>() {
        });
        director.setCache(shapes);
        return shapes;
    }


}
