<template>
  <div class="container">
    <!-- Top bar -->
    <div class="top-bar">
      <button @click="saveShapes" class="top-bar-button">Save as</button>
      <select v-model="saveType" class="top-bar-select">
        <option value="json">JSON File</option>
        <option value="xml">XML file</option>
      </select>
      <label class="top-bar-label">Load</label>
      <input type="file" @change="load" class="top-bar-input" />
    </div>

    <!-- Left toolbar -->
    <div class="toolbar-left">
      <p class="toolbar-title">Shapes</p>
      <div class="left-toolbar-buttons">
      <button @click="createShape('line')" class="shape-button">
        <i class="fas fa-grip-lines"></i> Line
      </button>
      <button @click="createShape('square')" class="shape-button">
        <i class="fas fa-square"></i> Square
      </button>
      <button @click="createShape('rectangle')" class="shape-button">
        <i class="fas fa-rectangle-wide"></i> Rectangle
      </button>
      <button @click="createShape('circle')" class="shape-button">
        <i class="fas fa-circle"></i> Circle
      </button>
      <button @click="createShape('ellipse')" class="shape-button">
        <i class="fas fa-ellipsis-h"></i> Ellipse
      </button>
      <button @click="createShape('triangle')" class="shape-button">
        <i class="fas fa-play"></i> Triangle
      </button>
    </div>
      <!-- Add other shape buttons -->
    </div>

    <!-- Sketch area -->
    <v-stage :config="stageSize">
      <div class="sketch-area">
        <!-- Sketch area content -->
        <div ref="stageContainer"></div>
      </div>
    </v-stage>

    <!-- Right toolbar -->
    <div class="toolbar-right">
      <p class="toolbar-title">Actions</p>
      <div class="right-toolbar-buttons">
        <button @click="undo" class="toolbar-button">Undo</button>
        <button @click="redo" class="toolbar-button">Redo</button>
        <button @click="deleteShape" v-if="selectedShape !== null" class="toolbar-button">Delete</button>
      </div>
      <div v-if="selectedShape !== null" class="shape-options">
        <label class="options-label">Color:</label>
        <div class="color-options">
          <label class="fill-label">Fill:</label>
          <button @click="changeColorFill" class="toolbar-button">Change</button>
          <input type="color"  v-model="FillColorValue" @change="changeColorsOfFill" />
        </div>
        <div class="color-options">
          <label class="stroke-label">Stroke:</label>
          <button @click="changeColorStroke" class="toolbar-button">Change</button>
          <input type="color"  v-model="StrokeColorValue" @change="changeColorOfStroke" />
        </div>
        <button @click="copy" class="toolbar-button">Copy</button>
      </div>
    </div>
  </div>
</template>


<script>
import Konva from "konva";
import axios from "axios";
// import { Stage, Layer, Rect, Circle } from 'konva';
export default {
  created() {
    this.clearData();
  },
  data() {
    return {
      layer: new Konva.Layer(),
      // stageSize: {
      //   width: 700,
      //   height: 500
      // },
      // configKonva: {
      //   width: 700,
      //   height: 500,
      // },
      FillColorValue: "black",
      StrokeColorValue: "black",
      saveType: null,
      isFilled: false,
      isStroke: false,
      isEndAfterMoved: false,
      shapes: [],
      selectedShape: null,
      selectedShapeType: null,
      selectedShapeName: "",
      stage: null,
      shapeIdCounter: 0,
      transformer: null,
    };
  },

  mounted() {
    const stageContainer = this.$refs.stageContainer;

    // Create a Konva stage
    this.stage = new Konva.Stage({
      container: stageContainer,
      width: 3200,
      height: 1700,
    });

    // Create a layer
    this.layer = new Konva.Layer();
    this.stage.add(this.layer);

    // Add transformation functionality
    this.transformer = new Konva.Transformer();
    this.layer.add(this.transformer);

    this.layer.draw();

    // Listen for click event to attach transformer to the shape
    this.stage.on("click", (e) => {
      // console.log("el3aaaaaaaaab fel click")
      const shape = e.target;
      this.selectShape(shape);
      this.updateTransformer(shape);
    });
    // this.stage.on(this.transformer, (e) =>{
    //   console.log("el3aaaaaaaaab fel transform")
    //   this.handleTransform(e.target);
    // });
    this.stage.on("dragmove", (e) => {
      console.log("el3aaaaaaaaab fel drag move");
      this.isEndAfterMoved = false;
    });
    this.stage.on("dragstart", (e) => {
      console.log("el3aaaaaaaaab fel drag start");
    });
    this.stage.on("dragend", (e) => {
      if (!this.isEndAfterMoved) {
        console.log("el3aaaaaaaaab fel drag end");
        console.log(e.target);
        this.handleDragEnd(e.target);
        this.isEndAfterMoved = true;
      }
    });
  },

  methods: {
    // copy logic
    copy() {
      console.log("the shape index copied is: " + this.selectedShape);
      const shape = this.shapes[this.selectedShape];
      if (this.selectedShape !== null)
        this.postCopyShape(this.getIndexofShape(shape));
    },

    // undo
    undo() {
      console.log("undo logic here");
      if (this.shapes.length > 0) this.postUndoShape();
      else console.log("Cannot Undo");
    },

    // redo
    redo() {
      console.log("redo logic here");
      this.postRedoShape();
    },

    changeColorFill() {
      const shape = this.shapes[this.selectedShape];
      if (this.isFilled) {
        shape.fill("transparent");
        this.isFilled = false;

      } else {
        shape.fill(this.FillColorValue);
        this.isFilled = true;
      }
      this.layer.draw();
      this.postColorShape(this.selectedShape, this.shapes[this.selectedShape]);
    },
    changeColorsOfFill() {
      const shape = this.shapes[this.selectedShape];
      shape.fill(this.FillColorValue);
      this.layer.draw();
      this.postColorShape(this.selectedShape, this.shapes[this.selectedShape]);
      this.isFilled = true;
    },
    changeColorStroke() {
      const shape = this.shapes[this.selectedShape];
      if (this.isStroke) {
        shape.stroke("transparent");
        this.isStroke = false;
      } else {
        shape.stroke(this.StrokeColorValue);
        this.isStroke = true;
      }
      this.layer.draw();
      this.postStrokeShape(this.selectedShape, this.shapes[this.selectedShape]);
    },
    changeColorOfStroke() {
      const shape = this.shapes[this.selectedShape];
      shape.stroke(this.StrokeColorValue);
      this.layer.draw();
      this.postStrokeShape(this.selectedShape, this.shapes[this.selectedShape]);
      this.isStroke = true;
    },

    // The save part
    saveShapes() {
      // Logic to save shapes to a file (JSON/XML)
      //console.log('Shapes saved');
      if (this.saveType === "json") this.saveJSON();
      else if (this.saveType === "xml") this.saveXML();
    },

    saveJSON() {
      console.log("save this file as a json");
      this.getSavingJSON();
    },
    saveXML() {
      console.log("save this file as an xml");
      this.getSavingXML();
    },

    // creation of the shapes from UI
    createShape(shape) {
      this.selectedShapeType = shape;
      if (this.selectedShapeType !== null)
        this.postCreateShape(this.selectedShapeType);
      else console.log("no shape chosen !");
    },

    handleTransform(shape) {
      // console.log("the answer of transform event is: "+shape);
      console.log(
        "changes in transform happened in: " + this.getIndexofShape(shape)
      );
      this.postTransformShape(this.getIndexofShape(shape), shape);
    },
    handleDragEnd(shape) {
      console.log("the answer of the drag event ended: " + shape);
      console.log(
        "the index of the given shape after change of position is: " +
          this.getIndexofShape(shape)
      );
      this.postMoveShape(this.getIndexofShape(shape), shape);
    },
    getIndexofShape(shape) {
      for (let i = 0; i < this.shapes.length; i++) {
        if (this.shapes[i].index === shape.index) {
          return i;
        }
      }
    },

    // add only one shape to the existing layer
    addShapeToLayer(shape) {
      this.shapes.push(shape);
      this.layer.add(shape);
      this.selectedShape = this.shapes.length - 1;
      this.updateTransformer(shape);
    },

    // selecting a shape with handling of the selection of the
    selectShape(shape) {
      //console.log('selectShape');

      this.selectedShape = null;

      if (shape.getClassName() !== "Stage") {
        for (let i = 0; i < this.shapes.length; i++) {
          if (this.shapes[i].index === shape.index) {
            this.selectedShape = i;
            this.postSelectShape(i);
            this.moveTotop();
            this.selectedShape = this.shapes.length - 1;
            console.log("after: " + this.selectedShape);
            console.log(this.shapes);
            this.isFilled = shape.fill() !== "transparent";
            if (!this.isFilled) this.FillcolorValue = shape.fill();
            this.isStroke = shape.stroke() !== "transparent";
            if (this.stroke) this.StrokecolorValue = shape.stroke();
            break;
          }
        }
      } else {
        this.selectedShape = null;
      }
    },

    moveTotop() {
      //console.log('moveToTop');
      // //console.log(this.selectedShape);
      const shape = this.shapes[this.selectedShape];
      this.shapes.splice(this.selectedShape, 1);
      this.shapes.push(shape);
      this.loadshapes();
    },

    updateTransformer(shape) {
      if (this.selectedShape !== null) {
        // console.log("ana dakhalt fel case beta3et msh ely heya bta3et el null")

        this.isFilled = shape.fill() !== "transparent";
        // console.log("fill of the shape is: "+shape.fill())
        // console.log("is filled is: "+this.isFilled)

        if (!this.isFilled) this.FillcolorValue = shape.fill();
        // console.log("stroke of the shape is: "+shape.stroke())
        // console.log("is Stroked is: "+this.isStroke)
        this.isStroke = shape.stroke() !== "transparent";
        if (this.stroke) this.StrokecolorValue = shape.stroke();

        this.transformer.attachTo(this.shapes[this.selectedShape]);
        //console.log(this.transformer);
        this.transformer.moveToTop();
        this.stage.batchDraw();
        // console.log("haneb3at lel backend");
        this.handleTransform(shape);
      } else {
        console.log("ana dakhalt fel case beta3et elnull");
        this.transformer.detach();
        this.stage.batchDraw();
        this.handleTransform(this.shapes[this.shapes.length - 1]);
      }
    },
    async clearData() {
      try {
        // Make a GET request to the '/clear' endpoint
        const response = await axios.get("http://localhost:8000/api/clear");
        // Handle the response as needed
      } catch (error) {
        // Handle errors
        console.error("Error clearing data:", error);
      }
    },
    previewShapes() {
      this.layer = new Konva.Layer();
      this.stage.add(this.layer);
      this.transformer = new Konva.Transformer();
      this.layer.add(this.transformer);
      // this.layer = this.stage.findOne('Layer');
      // Loop through the shapes and add them to the layer for preview
      this.shapes.forEach((shape) => {
        this.layer.add(shape);
        this.stage.draw();
      });

      // Update the layer
      this.layer.batchDraw();

      //console.log("7amdela 3al salamaaaa ana khalast teba3a mn preview shapes");
      this.selectedShape = null;
    },

    deleteShape() {
      //console.log('deleteShape');
      //console.log(this.selectedShape);
      this.postRemoveShape(this.selectedShape);
      // Remove shape from the list
      const shape = this.shapes[this.selectedShape];
      // //console.log(shape._x);
      //console.log(shape);
      // //console.log(shape.$refs);
      //console.log(this.shapes);
      //console.log(this.selectedShape);
      // //console.log(this.shapes[this.selectedShape].x());
      this.shapes.splice(this.selectedShape, 1);
      //console.log(this.shapes);
      this.selectedShape = null;
      // Clear the layer and redraw remaining shapes
      this.layer.removeChildren();
      this.transformer.detach();
      this.previewShapes();
    },
    loadshapes() {
      this.layer.removeChildren();
      this.transformer.detach();
      this.previewShapes();
      this.layer = new Konva.Layer();
      this.stage.add(this.layer);
      this.transformer = new Konva.Transformer();
      this.layer.add(this.transformer);

      // Loop through the shapes and add them to the layer for preview
      this.shapes.forEach((shape) => {
        this.layer.add(shape);
        this.stage.draw();
      });

      // Update the layer
      this.layer.batchDraw();

      //console.log("7amdela 3al salamaaaa ana khalast teba3a mn preview shapes");
      this.selectedShape = null;
    },

    // The load part from a file
    load(event) {
      const file = event.target.files[0];
      const fileType = file.name.split(".").pop();
      console.log("file name is " + file.name);
      console.log("file type is: " + fileType);

      // Logic to load shapes from a file (JSON/XML)
      //console.log('Shapes loaded');
      if (/*this where we now that the file is JSON*/ fileType === "json") {
        this.loadJSONfromFile(file);
      } else if (/*This file is XML file*/ fileType === "xml") {
        this.loadXMLfromFile(file);
      } else console.log("Error, not readable file");
    },

    loadJSONfromFile(file) {
      this.postLoadingJSON(file);
      const reader = new FileReader();
      reader.onload = (e) => {
        const content = e.target.result;
        console.log("content of page is: " + content);
        console.log("content of the file is " + content);
        const ParesedJSON = JSON.parse(content);
        this.loadFromLoad(ParesedJSON);
      };
      reader.readAsText(file);
    },
    loadXMLfromFile(file) {
      console.log("the load XML function here");
      this.postLoadingXML(file);
    },

    // this function is used to load a new layer with shapes from a JSON file
    loadFromLoad(ParesedJSON) {
      console.log(ParesedJSON);
      this.layer.removeChildren();
      this.transformer.detach();
      this.shapes = [];
      this.layer = new Konva.Layer();
      this.stage.add(this.layer);
      this.transformer = new Konva.Transformer();
      this.layer.add(this.transformer);
      if (Array.isArray(ParesedJSON) && ParesedJSON.length === 0)
        console.log("There is nothing to draw");
      else {
        ParesedJSON.forEach((jsonShape) => {
          this.loadOneShape(jsonShape);
        });
      }
      // this.shapes.forEach(shape => {
      //   this.layer.add(shape);
      //   this.stage.draw();
      // });
    },

    // load only one shape on the existing layer
    loadOneShape(jsonShape) {
      console.log("went here in loading one shape with " + jsonShape);

      console.log(jsonShape);

      // const jsonData = JSON.parse(json);
      console.log("the json data are:");
      // console.log(jsonData);
      const shape = this.createShapeOnLoad(jsonShape);
      if (shape !== null) {
        this.addShapeToLayer(shape);
      }
    },
    // trying load things
    createShapeOnLoad(attr) {
      let shape;
      console.log("attr before going to the if condition " + attr);
      console.log("attr name is: " + attr.name);
      if (attr.name === "Line") {
        shape = this.addLineOnLoad(attr);
      } else if (attr.name === "Square") {
        shape = this.addSquareOnLoad(attr);
      } else if (attr.name === "Rectangle") {
        shape = this.addRectangleOnLoad(attr);
      } else if (attr.name === "Ellipse") {
        shape = this.addEllipseOnLoad(attr);
      } else if (attr.name === "Circle") {
        shape = this.addCircleOnLoad(attr);
      } else if (attr.name === "Triangle") {
        shape = this.addTriangleOnLoad(attr);
      } else if (attr.name === "Star") {
        shape = this.addStarOnLoad(attr);
      } else {
        shape = null;
        console.log("no shape chosen !");
      }
      console.log("shape is");
      console.log(shape);
      return shape;
    },
    addCircleOnLoad(attr) {
      const circ = new Konva.Circle({
        name: attr.name,
        x: attr.x,
        y: attr.y,
        rotation: attr.rotation,
        fill: attr.color,
        scaleX: attr.scaleX,
        scaleY: attr.scaleY,
        stroke: attr.stroke,
        radius: attr.radius,
        draggable: true,
      });

      return circ;
    },
    addRectangleOnLoad(attr) {
      const rect = new Konva.Rect({
        name: attr.name,
        x: attr.x,
        y: attr.y,
        rotation: attr.rotation,
        fill: attr.color,
        scaleX: attr.scaleX,
        scaleY: attr.scaleY,
        stroke: attr.stroke,
        height: attr.height,
        width: attr.width,
        draggable: true,
      });

      return rect;
    },
    addTriangleOnLoad(attr) {
      console.log("I'm adding a triangel with attributes of: " + attr);
      const triangle = new Konva.RegularPolygon({
        name: attr.name,
        x: attr.x,
        y: attr.y,
        rotation: attr.rotation,
        fill: attr.color,
        scaleX: attr.scaleX,
        scaleY: attr.scaleY,
        stroke: attr.stroke,
        sides: attr.sides,
        radius: attr.radius,
        draggable: true,
      });
      return triangle;
    },
    addStarOnLoad(attr) {
      const star = new Konva.Star({
        attr,
        draggable: true,
      });

      return star;
    },

    addEllipseOnLoad(attr) {
      const ellipse = new Konva.Ellipse({
        name: attr.name,
        x: attr.x,
        y: attr.y,
        rotation: attr.rotation,
        fill: attr.color,
        scaleX: attr.scaleX,
        scaleY: attr.scaleY,
        stroke: attr.stroke,
        radiusX: attr.radiusX,
        radiusY: attr.radiusY,
        draggable: true,
      });
      return ellipse;
    },
    addSquareOnLoad(attr) {
      const square = new Konva.Rect({
        name: attr.name,
        x: attr.x,
        y: attr.y,
        rotation: attr.rotation,
        fill: attr.color,
        scaleX: attr.scaleX,
        scaleY: attr.scaleY,
        stroke: attr.stroke,
        height: attr.height,
        width: attr.width,
        draggable: true,
      });
      return square;
    },
    addLineOnLoad(attr) {
      const line = new Konva.Line({
        name: attr.name,
        x: attr.x,
        y: attr.y,
        rotation: attr.rotation,
        scaleX: attr.scaleX,
        scaleY: attr.scaleY,
        stroke: attr.stroke,
        points: attr.coordinates,
        draggable: true,
      });
      return line;
    },

    // api handling
    async postCreateShape(shape) {
      try {
        const response = await axios.post(
          `http://localhost:8000/api/create?shapeType=${shape}`
        );
        this.loadOneShape(response.data);
      } catch (error) {
        console.log("Error in post Create: " + error);
      }
    },
    async postCopyShape(index) {
      console.log("the index of the shape copied is: " + index);

      try {
        const response = await axios.post(
          `http://localhost:8000/api/copy?shapeIndex=` + index
        );
        console.log("the response of copy is: " + response.data);
        this.loadOneShape(response.data);
      } catch (error) {
        console.log("Error in post in Copy: " + error);
      }
    },
    async postUndoShape() {
      try {
        const response = await axios.post("http://localhost:8000/api/undo");

        this.loadFromLoad(response.data);
      } catch (error) {
        console.log("Error in post in Undo: " + error);
      }
    },
    async postRedoShape() {
      try {
        const response = await axios.post("http://localhost:8000/api/redo");
        this.loadFromLoad(response.data);
      } catch (error) {
        console.log("Error in post in Redo: " + error);
      }
    },
    async getSavingXML() {
      try {
        // Make a GET request to download the XML file
        const response = await axios.get(
          "http://localhost:8000/api/download/xml",
          { responseType: "blob" }
        );

        // Create a Blob from the response data
        const blob = new Blob([response.data], {
          type: response.headers["content-type"],
        });

        // Create a link and trigger a click to download the file
        const downloadLink = document.createElement("a");
        downloadLink.href = window.URL.createObjectURL(blob);
        downloadLink.download = "downloadedFile.xml"; // Specify the desired file name

        document.body.appendChild(downloadLink);
        downloadLink.click();
        document.body.removeChild(downloadLink);
      } catch (error) {
        // Handle errors
        console.error("Error downloading XML file:", error);
      }
    },
    async getSavingJSON() {
      try {
        const response = await axios.get(
          "http://localhost:8000/api/download/json",
          { responseType: "blob" }
        );

        // Create a Blob from the response data
        const blob = new Blob([response.data], {
          type: response.headers["content-type"],
        });

        // Create a link and trigger a click to download the file
        const downloadLink = document.createElement("a");
        downloadLink.href = window.URL.createObjectURL(blob);
        downloadLink.download = "downloadedFile.json"; // Specify the desired file name

        document.body.appendChild(downloadLink);
        downloadLink.click();
        document.body.removeChild(downloadLink);
      } catch (error) {
        // Handle errors
        console.error("Error downloading JSON file:", error);
      }

    },
    // async postSavingXML(file)
    // {
    //   try{
    //     const response = await axios.post('http://localhost:8000/api/upload/xml', {
    //       params: {
    //         file:  file,
    //       }
    //     });
    //     this.loadFromLoad(response.data)
    //   }
    //   catch(error)
    //   {
    //     console.log("Error in post in saving XML: "+error);
    //   }
    // },
    // async postSavingJSON(file)
    // {
    //   try{
    //     const response = await axios.post('http://localhost:8000/api/upload/json', {
    //       params: {
    //         file:  file,
    //       }
    //     });
    //   }
    //   catch(error)
    //   {
    //     console.log("Error in post in saving JSON: "+error);
    //   }
    // },
    async postLoadingXML(file) {
      try {
        const formData = new FormData();
        formData.append("file", file);

        const response = await axios.post(
          "http://localhost:8000/api/upload/xml",
          formData
        );
        this.loadFromLoad(response.data);
      } catch (error) {
        console.log("Error in post in uploading XML: " + error);
      }
    },
    async postLoadingJSON(file) {
      try {
        const formData = new FormData();
        formData.append("file", file);

        const response = await axios.post(
          "http://localhost:8000/api/upload/json",
          formData
        );
      } catch (error) {
        console.log("Error in post in uploading JSON: " + error);
      }
    },
    async postRemoveShape(index) {
      try {
        const response = await axios.post(
          `http://localhost:8000/api/remove?shapeIndex=` + index
        );
      } catch (error) {
        console.log("Error in post in remove: " + error);
      }
    },
    async postMoveShape(index, shape) {
      console.log("the index of the shape moved is: " + index);
      console.log("the x of the shape moved is: " + shape.x());
      console.log("the y of the shape moved is: " + shape.y());
      try {
        const response = await axios.post(
          `http://localhost:8000/api/move?id=${index}&x=${(
            shape.x()
          )}&y=${shape.y()}`
        );
        this.loadOneShape(response.data);
      } catch (error) {
        console.log("Error in post in move: " + error);
      }
    },
    async postTransformShape(index, shape) {
      try {
        console.log("the scale x is: " + shape.scaleX());
        console.log("the scale y is: " + shape.scaleY());
        console.log("the rotation is: " + shape.rotation());
        const response = await axios.post(
          `http://localhost:8000/api/transform?id=${index}&scaleX=${shape.scaleX()}&scaleY=${shape.scaleY()}&rotation=${shape.rotation()}&x=${shape.x()}&y=${shape.y()}`
        );

        // Handle the response as needed
      } catch (error) {
        // Handle errors
        console.error("Error transforming shape:", error);
      }
    },

    async postColorShape(index, shape) {
      try {
        const fillColor = shape.fill();
        console.log("the fill is: " + fillColor);

        const encodedFillColor = encodeURIComponent(fillColor);
        const response = await axios.post(
          `http://localhost:8000/api/color?id=${index}&color=${encodedFillColor}`
        );
      } catch (error) {
        console.log("Error in post in Color: " + error);
      }
    },
    async postStrokeShape(index, shape) {
      try {
        const strokeColor = shape.stroke(); // Assuming shape.stroke() returns a hex color code like #c00000
        console.log("the stroke is: " + strokeColor);

        const encodedStrokeColor = encodeURIComponent(strokeColor);

        const response = await axios.post(
          `http://localhost:8000/api/stroke?id=${index}&stroke=${encodedStrokeColor}`
        );
        // Handle the response as needed
      } catch (error) {
        // Handle errors
        console.error("Error updating stroke:", error);
      }
    },
    async postSelectShape(index) {
      try {
        const response = await axios.post(
          `http://localhost:8000/api/select?id=${index}`
        );
      } catch (error) {
        console.log("Error in post in Select: " + error);
      }
    },

    // The creation of the shapes
    createshape() {
      if (this.selectedShapeType !== null)
        this.postCreateShape(this.selectedShapeType);
    },

    createShapeBasic() {
      let shape;
      if (this.selectedShapeType === "line") {
        shape = this.addLine();
      } else if (this.selectedShapeType === "square") {
        shape = this.addSquare();
      } else if (this.selectedShapeType === "rectangle") {
        shape = this.addRectangle();
      } else if (this.selectedShapeType === "ellipse") {
        shape = this.addEllipse();
      } else if (this.selectedShapeType === "circle") {
        shape = this.addCircle();
      } else if (this.selectedShapeType === "triangle") {
        shape = this.addTriangle();
      } else if (this.selectedShapeType === "star") {
        shape = this.addStar();
      } else {
        console.log("no shape chosen !");
        return;
      }
      this.addShapeToLayer(shape);
    },
  },
};
</script>

<style>

/* Global Styles */
* {
  box-sizing: border-box;
  margin: 0;
  padding: 0;
  font-size: 50px;
  font-family: sans-serif;
  
}

body {
  font-family: sans-serif;
  background-color: #4d4d4d;
}

/* Container & Layout */
.container {
  display: flex;
  flex-direction: row;
  font-size: 50px;

}

.content {
  display: flex;
  flex: 1;
  flex-direction: row;
  font-size: 50px;
}

/* Top Bar */
.top-bar {
  position: absolute;
  top: 150px;
  left: 60dvb;
  width: 80dvh;
  background-color: #8d6f6f;
  padding: 10px 20px;
  border-radius: 50px;
  /* margin-bottom: 20px; */
  display: flex;
  align-items: center;
  display: inline-block;
}

.top-bar-button {
  padding: 8px 12px;
  margin-right: 10px;
  border: none;
  border-radius: 20px;
  background-color: #4caf50;
  color: white;
  font-size: 50px;
  cursor: pointer;
  transition: background-color 0.3s ease;
}

.top-bar-button:hover {
  background-color: #357a38;
}

.top-bar-select,
.top-bar-label,
.top-bar-input {
  padding: 8px;
  border-radius: 5px;
  border: 1px solid #ccc;
  /* font-size: 14px; */
  margin-right: 10px;
}

.top-bar-input {
  cursor: pointer;
}

/* Left Toolbar */
.toolbar-left {
  position: absolute;
  top: 300px;
  left: 50px;
  width: 500px;
  height: 78dvb;
  background-color: #8d6f6f;
  border-radius: 20px;
  padding: 20px;
  margin: auto;
  margin-right: 20px;
  /* margin-top: 50px; */
  text-align: center;


}

.toolbar-title {
  /* font-size: 18px; */
  font-weight: bold;
  margin-bottom: 15px;
}

.shape-button {
  display: block;
  width: 100%;
  height: 100px;
  text-align: left;
  padding: 10px;
  margin-bottom: 10px;
  border: none;
  border-radius: 200px;
  margin: 0px 50px 50px 0px;
  background-color: #4caf50;
  color: #ffffff;
  /* font-size: 16px; */
  cursor: pointer;
  transition: background-color 0.3s ease;
  text-align: center;

}

.shape-button i {
  margin-right: 10px;
}

.shape-button:hover {
  background-color: #ccc;
}

/* Sketch Area */
.sketch-area {
  position: absolute;
  top: 300px;
  left: 600px;
  /* flex: 1; */
  margin: 10px 10px 10px 10px;
  padding: 10px 10px 10px 10px;
  background-color: #f8f8f8;
  border: 1px solid #ccc;
  border-radius: 5px;
  overflow: hidden;
}

/* Right Toolbar */
.toolbar-right {
  position: absolute;
  top: 300px;
  right: 50px;
  width: 500px;
  height: 78dvb;
  background-color: #8d6f6f;
  border-radius: 20px;
  padding: 20px;
  text-align: center;
  
}

.right-toolbar-buttons {
  display: flex;
  flex-direction: column;
}
.toolbar-button {
  font-family: Roboto, sans-serif;
  /* font-size: 16px; */
  padding: 10px 20px;
  border-radius: 200px;
  border: none;
  margin-bottom: 50px;
  background-color: #4caf50;
  color: white;
  cursor: pointer;
  transition: background-color 0.3s ease;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.2);
}

.toolbar-button:hover {
  background-color: #357a38;
}
.options-label {
  /* display: flex; */
  font-family: Roboto, sans-serif;
  font-weight: bold;
  /* font-size: 16px; */
  margin-bottom: 10px;
}

.color-options {
  display: flex;
  align-items: center;
}

.fill-label,
.stroke-label {
  display: flex;
  font-weight: 500;
  margin-right: 10px;
  
  /* font-size: 14px; */
}

input[type="color"] {

  border: 10px solid #ccc;
  border-radius: 10px;
  padding: 5px;
  margin-right: 10px;
  cursor: pointer;
}
input[type="color"]::-webkit-color-swatch {
  width: 100px;
  /* height: 50px; */
}

/* Selected Shape */
.selected-shape {
  border: 5px dashed #00f;
}


</style>

<!-- /* Styles for the top bar */
.container {
  display: flex;
  flex-direction: column;
  height: 100vh;
}
/* .top-bar {
  display: flex;
  align-items: center;
  background-color: #f2f2f2;
  padding: 10px 20px;
  border-radius: 5px;
  margin-bottom: 20px;
} */
/* Styles for the top bar */
.top-bar {
  background-color: #f2f2f2;
  padding: 10px 20px;
  border-radius: 5px;
  margin-bottom: 20px;
}
/* Styles for the content section */
.content {
  display: flex;
  flex: 1;
}
.top-bar-button {
  padding: 8px 12px;
  margin-right: 10px;
  border: none;
  border-radius: 5px;
  background-color: #4caf50;
  color: white;
  font-size: 14px;
  cursor: pointer;
  transition: background-color 0.3s ease;
}

.top-bar-button:hover {
  background-color: #357a38;
}

.top-bar-select,
.top-bar-label,
.top-bar-input {
  padding: 8px;
  border-radius: 5px;
  border: 1px solid #ccc;
  font-size: 14px;
  margin-right: 10px;
}

.top-bar-input {
  cursor: pointer;
}

/* Styles for the left toolbar
.toolbar-left {
  background-color: #f2f2f2;
  border-radius: 5px;
  padding: 20px;
} */

/* Styles for the left toolbar */
.toolbar-left {
  background-color: #f2f2f2;
  border-radius: 5px;
  padding: 20px;
  margin-right: 20px;
}

.toolbar-title {
  font-size: 18px;
  font-weight: bold;
  margin-bottom: 15px;
}

.shape-button {
  display: block;
  width: 100%;
  text-align: left;
  padding: 10px;
  margin-bottom: 10px;
  border: none;
  border-radius: 5px;
  background-color: #e0e0e0;
  color: #333;
  font-size: 16px;
  cursor: pointer;
  transition: background-color 0.3s ease;
}

.shape-button i {
  margin-right: 10px;
}

.shape-button:hover {
  background-color: #ccc;
}

/* Styles for the sketch area */
/* .sketch-area {
  border: 1px solid #ccc;
  border-radius: 5px;
  overflow: hidden;
} */
/* Styles for the sketch area */
.sketch-area {
  border: 1px solid #ccc;
  border-radius: 5px;
  overflow: hidden;
  flex: 1;
}

/* Styles for the right toolbar */
.toolbar-right {
  background-color: #f2f2f2;
  border-radius: 5px;
  padding: 20px;
}

.right-toolbar-buttons {
  display: flex;
  flex-direction: column;
}

.toolbar-button {
  padding: 8px 12px;
  margin-bottom: 10px;
  border: none;
  border-radius: 5px;
  background-color: #4caf50;
  color: white;
  font-size: 14px;
  cursor: pointer;
  transition: background-color 0.3s ease;
}

.toolbar-button:hover {
  background-color: #357a38;
}

/* Styles for the shape options */
.shape-options {
  margin-top: 20px;
}

.options-label {
  font-weight: bold;
  font-size: 16px;
  margin-bottom: 10px;
}

.color-options {
  display: flex;
  align-items: center;
}

.fill-label,
.stroke-label {
  font-weight: 300;
  margin-right: 10px;
  font-size: 14px;
}
/* Add any other specific styles or adjustments as needed */
 -->

<!-- // changex(){
  //   //console.log(this.xValue);
  //   const shape = this.shapes[this.selectedShape];
  //   shape.x(this.xValue);
  //   this.layer.draw();
  // },
  // changey(){
  //   //console.log(this.yValue);
  //   const shape = this.shapes[this.selectedShape];
  //   shape.y(this.yValue);
  //   this.layer.draw();
  // }, -->


  <!-- // createShape() {
    //   let shape;
    //   if (this.selectedShapeType === 'line') {
    //     shape=this.addLine();
    //   } else if (this.selectedShapeType === 'square') {
    //     shape=this.addSquare();
    //   } else if (this.selectedShapeType === 'rectangle') {
    //     shape=this.addRectangle();
    //   } else if (this.selectedShapeType === 'ellipse') {
    //     shape=this.addEllipse();
    //   } else if (this.selectedShapeType === 'circle') {
    //     shape=this.addCircle();
    //   } else if (this.selectedShapeType === 'triangle') {
    //     shape=this.addTriangle();
    //   } else if (this.selectedShapeType === 'star') {
    //     shape=this.addStar();
    //   }
    //   else
    //   {
    //     console.log("no shape chosen !");
    //     return;
    //   }
    //   this.addShapeToLayer(shape);
    // }, -->

    <!--  // addCircle() {
      //   const circ = new Konva.Circle({
      //     name: 'circle',
      //     x: 100,
      //     y: 100,
      //     radius: 50,
      //     fill: 'green',
      //     draggable: true,
      //   });
  
      //   return circ;
      // },
      // addRectangle() {
      //   const rect = new Konva.Rect({
      //     name: 'rectangle',
      //     x: 50,
      //     y: 50,
      //     width: 200,
      //     height: 100,
      //     fill: 'black',
      //     draggable: true,
  
      //   });
  
      //   return rect;
      // },
      // addTriangle() {
      //   const triangle = new Konva.RegularPolygon({
  
      //     // name: 'triangle',
      //     // x: 100,
      //     // y: 100,
      //     // sides: 3,
      //     // radius: 90,
      //     // fill: 'blue',
      //     fill: "blue",
      //     name: "triangle",
      //     radius: 90,
      //     rotation: 0,
      //     scalex: 1,
      //     scaley: 1,
      //     sides: 3,
      //     stroke: "transparent",
      //     x: 100,
      //     y: 100,
      //     draggable: true,
      //   });
      //   return triangle;
  
      // },
      // addStar() {
      //   const star = new Konva.Star({
      //     name: 'star',
      //     x: 100,
      //     y: 100,
      //     numPoints: 5,
      //     innerRadius: 40,
      //     outerRadius: 70,
      //     fill: 'yellow',
      //     draggable: true,
      //   });
  
      //   return star;
      // },
  
      // addEllipse() {
      //   const ellipse = new Konva.Ellipse({
      //     name: 'ellipse',
      //     x: 100,
      //     y: 100,
      //     radiusX: 50,
      //     radiusY: 30,
      //     fill: 'orange',
      //     draggable: true,
      //   });
      //   return ellipse ;
      // },
      // addSquare() {
      //   const square = new Konva.Rect({
      //     name: 'square',
      //     x: 100,
      //     y: 100,
      //     width: 100,
      //     height: 100,
      //     fill: 'cyan',
      //     draggable: true,
      //   });
      //   return square;
      // },
      // addLine() {
      //   const line = new Konva.Line({
      //     name: 'line',
      //     points: [100, 50, 200, 50],
      //     stroke: 'purple',
      //     strokeWidth: 5,
      //     draggable: true,
      //   });
      //   return line;
  
      // }, -->

<!-- 
      // const jsonExample = 
      //   {
      //     "name": "square",
      //     "x": 300,
      //     "y": 300,
      //     "rotation": 0,
      //     "scaleX":1,
      //     "scaleY":1,
      //     "stroke":"Red",
      //     "height": 100,
      //     "width": 100,
      //     "color": "Transparent"
      //   }; -->