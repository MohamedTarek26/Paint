<template>
  <div>
    <!-- Taskbar -->
    <div class="taskbar">
      <button @click="saveShapes">Save as</button>
      <select v-model="saveType">
        <option value="json">JSON File</option>
        <option value="xml">XML file</option>
      </select>
      <button @click="loadOneShape">Load one shape</button>
      <label>Load</label>
      <input type="file" @change="load">
      <button @click="createShape">Add Shape</button>
      <select v-model="selectedShapeType">
        <option value="line">Line</option>
        <option value="square">Square</option>
        <option value="rectangle">Rectangle</option>
        <option value="circle">Circle</option>
        <option value="ellipse">Ellipse</option>
        <option value="triangle">Triangle</option>
        <option value="star">Star</option>
      </select>
      <button @click="undo">Undo</button>
      <button @click="redo">Redo</button>
      <button @click="deleteShape" v-if="selectedShape !== null">Delete</button>
      <div v-if="selectedShape !== null">
        <label style="font:bold;">Color:</label>
        <!-- <label>X:</label>
        <input type="x_select" v-model="xValue" @change="changex" />
        <label>Y:</label>
        <input type="number" v-model="yValue" @change="changey" />  -->
        <label style="font:300;">Fill:</label>
        <input type="checkbox" v-model="isFilled"  @change="changeColorFill" />
        <input type="color" v-if="isFilled" v-model="FillcolorValue"  @change="changeColorFill"/>
        
        <label style="font:300;">Stroke:</label>
        <input type="checkbox" v-model="isStroke" @change="changeColorStroke" />
        <input type="color" v-if="isStroke" v-model=StrokecolorValue   @change="changeColorStroke"/>
        <button @click="copy">Copy</button>
        
        

        <!-- <label>Width:</label>
        <input type="number" v-model="shapes[selectedShape].width" />
        <label>Height:</label> 
        <input type="number" v-model="shapes[selectedShape].height" /> -->

    </div>
    </div>
    <v-stage :config="stageSize"  >
    <div class="sketch-area">
      <div ref="stageContainer"></div>
    </div>
  </v-stage>
  </div>
</template>

<script>
import Konva from 'konva';
import axios from 'axios';
// import { Stage, Layer, Rect, Circle } from 'konva';
export default {
  data() {
    return {
      layer : new Konva.Layer(),
      // stageSize: {
      //   width: 700,
      //   height: 500
      // },
      // configKonva: {
      //   width: 700,
      //   height: 500,
      // },
      FillcolorValue: 'black',
      StrokecolorValue: 'black',
      saveType: null,
      isFilled: false,
      isStroke:false,
      isEndAfterMoved: false,
      shapes: [],
      selectedShape: null,
      selectedShapeType: null,
      selectedShapeName: '',
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
      width: 800,
      height: 500,
    });

    // Create a layer
    this.layer = new Konva.Layer();
    this.stage.add(this.layer);

    // Add transformation functionality
    this.transformer = new Konva.Transformer();
    this.layer.add(this.transformer);

    this.layer.draw();

    // Listen for click event to attach transformer to the shape
    this.stage.on('click', (e) => {
      // console.log("el3aaaaaaaaab fel click")
      const shape = e.target;
      this.selectShape(shape);
      this.updateTransformer(shape);
    });
    // this.stage.on(this.transformer, (e) =>{
    //   console.log("el3aaaaaaaaab fel transform")
    //   this.handleTransform(e.target);
    // });
    this.stage.on('dragmove', (e) =>{
      console.log("el3aaaaaaaaab fel drag move")
      this.isEndAfterMoved=false;
    });
    this.stage.on('dragstart', (e) =>{
      console.log("el3aaaaaaaaab fel drag start")
    });
    this.stage.on('dragend', (e) =>{
      if(!this.isEndAfterMoved){
        console.log("el3aaaaaaaaab fel drag end")
        console.log(e.target)
        this.handleDragEnd(e.target)
        this.isEndAfterMoved=true;
        
      }
    });
  },

  methods: {
    
    // copy logic
    copy(){
      console.log("the shape index copied is: "+this.selectedShape);
      if(this.selectedShape!==null)
        this.postCopyShape();
    },

    // undo
    undo(){
      console.log("undo logic here");
      if(this.shapes.length>0)
        this.postUndoShape();
      else
        console.log("Cannot Undo");
    },

    // redo 
    redo(){
      console.log("redo logic here");
      this.postRedoShape();
    },

    changeColorFill(){
      const shape = this.shapes[this.selectedShape];
      if(this.isFilled){
        shape.fill(this.FillcolorValue);
      }else{
        shape.fill('transparent');
      }
      this.layer.draw();
      this.postColorShape(this.selectedShape,this.shapes[this.selectedShape]);
    },
    changeColorStroke(){
      const shape = this.shapes[this.selectedShape];
      if(this.isStroke){
        shape.stroke(this.StrokecolorValue);
      }
      else{
        shape.stroke('transparent');
      }
      this.layer.draw();
      this.postStrokeShape(this.selectedShape,this.shapes[this.selectedShape]);

    },


    // The save part
    saveShapes() {
      // Logic to save shapes to a file (JSON/XML)
      //console.log('Shapes saved');
      if(this.saveType==="json")
        this.saveJSON();
      else if(this.saveType==="xml")
        this.saveXML();
    },

    saveJSON()
    {
      console.log("save this file as a json");
      this.getSavingJSON();
    },
    saveXML()
    {
      console.log("save this file as an xml");
      this.getSavingXML();
    },



    // creation of the shapes from UI
    createShape(){
      if(this.selectedShapeType !==null)
        this.postCreateShape(this.selectedShapeType);
      else
        console.log("no shape chosen !");
    },


    handleTransform(shape)
    {
      // console.log("the answer of transform event is: "+shape);
      console.log("changes in transform happened in: "+this.getIndexofShape(shape));
      this.postTransformShape(this.getIndexofShape(shape),shape);
    },  
    handleDragEnd(shape)
    {
      console.log("the answer of the drag event ended: "+shape)
      console.log("the index of the given shape after change of position is: "+this.getIndexofShape(shape));
      this.postMoveShape(this.getIndexofShape(shape),shape);
    },
    getIndexofShape(shape){
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
      this.selectedShape=this.shapes.length-1;
      this.updateTransformer(shape);

    },

    // selecting a shape with handling of the selection of the 
    selectShape(shape) {
      //console.log('selectShape');

      this.selectedShape = null;

      if(shape.getClassName() !== 'Stage'){
      for (let i = 0; i < this.shapes.length; i++) {
        if (this.shapes[i].index === shape.index) {
          this.selectedShape = i;
          this.postSelectShape(i);
          this.moveTotop();
          this.selectedShape=this.shapes.length-1;
          console.log("after: "+this.selectedShape);
          console.log(this.shapes);
          break;
        }
      }
    }
    else{
      this.selectedShape = null;
    }
    },

    
    moveTotop() {
      //console.log('moveToTop');
      // //console.log(this.selectedShape);
      const shape=this.shapes[this.selectedShape];
      this.shapes.splice(this.selectedShape, 1);
      this.shapes.push(shape);
      this.loadshapes();
    },

    updateTransformer(shape) {
      if(this.selectedShape !== null) {
        // console.log("ana dakhalt fel case beta3et msh ely heya bta3et el null")

        this.isFilled = shape.fill() !== 'transparent';
        // console.log("fill of the shape is: "+shape.fill())
        // console.log("is filled is: "+this.isFilled)
        
          if(!this.isFilled)
            this.FillcolorValue= shape.fill();
          // console.log("stroke of the shape is: "+shape.stroke())
          // console.log("is Stroked is: "+this.isStroke)
          this.isStroke=shape.stroke() !== 'transparent';
          if(this.stroke)
            this.StrokecolorValue= shape.stroke();

        this.transformer.attachTo(this.shapes[this.selectedShape]);
        //console.log(this.transformer);
        this.transformer.moveToTop();
        this.stage.batchDraw();
        // console.log("haneb3at lel backend");
        this.handleTransform(shape);
      }
      else{
        console.log("ana dakhalt fel case beta3et elnull")
        this.transformer.detach();
        this.stage.batchDraw();
      }
    },

    previewShapes() {
      this.layer = new Konva.Layer();
      this.stage.add(this.layer);
      this.transformer = new Konva.Transformer();
      this.layer.add(this.transformer);
      // this.layer = this.stage.findOne('Layer');
      // Loop through the shapes and add them to the layer for preview
      this.shapes.forEach(shape => {
        this.layer.add(shape);
        this.stage.draw();
      });

      // Update the layer
      this.layer.batchDraw();
      
      //console.log("7amdela 3al salamaaaa ana khalast teba3a mn preview shapes");
      this.selectedShape=null;
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
    loadshapes()
    {
      this.layer.removeChildren();
      this.transformer.detach();
      this.previewShapes();
      this.layer = new Konva.Layer();
      this.stage.add(this.layer);
      this.transformer = new Konva.Transformer();
      this.layer.add(this.transformer);

      // Loop through the shapes and add them to the layer for preview
      this.shapes.forEach(shape => {
        this.layer.add(shape);
        this.stage.draw();
      });

      // Update the layer
      this.layer.batchDraw();
      
      //console.log("7amdela 3al salamaaaa ana khalast teba3a mn preview shapes");
      this.selectedShape=null;
    },


  // The load part from a file
  load(event) {
    const file = event.target.files[0];
    const fileType = file.name.split('.').pop();
    console.log("file name is "+file.name);
    console.log("file type is: "+fileType);

    // Logic to load shapes from a file (JSON/XML)
    //console.log('Shapes loaded');
    if(/*this where we now that the file is JSON*/ fileType==='json')
    {
      this.loadJSONfromFile(file);
    }
    else if(/*This file is XML file*/ fileType==='xml')
    {
      this.loadXMLfromFile(file);
    }
    else
      console.log("Error, not readable file");
  },

  loadJSONfromFile(file)
  {
    this.postLoadingJSON(file);
    const reader = new FileReader();
    reader.onload=(e)=>{
      const content = e.target.result;
      console.log("content of page is: "+content);
      console.log("content of the file is "+content);
      const ParesedJSON = JSON.parse(content);
      this.loadFromLoad(ParesedJSON);
    }
    reader.readAsText(file);
  
  },
  loadXMLfromFile(file){
    console.log("the load XML function here");
    this.postLoadingXML(file);
  },

  // this function is used to load a new layer with shapes from a JSON file
  loadFromLoad(ParesedJSON)
  {
    
      console.log(ParesedJSON);
      this.layer.removeChildren();
      this.transformer.detach();
      this.shapes = [];
      this.layer = new Konva.Layer();
      this.stage.add(this.layer);
      this.transformer = new Konva.Transformer();
      this.layer.add(this.transformer);
      if(Array.isArray(ParesedJSON) && ParesedJSON.length === 0)
        console.log("There is nothing to draw");
      else{
      ParesedJSON.forEach(jsonShape =>{
        this.loadOneShape(jsonShape);
      });
      }
      // this.shapes.forEach(shape => {
      //   this.layer.add(shape);
      //   this.stage.draw();
      // });

  },

  // load only one shape on the existing layer
  loadOneShape(jsonShape)
  {
    console.log("went here in loading one shape with "+jsonShape);

  
    
    console.log(jsonShape)
      
    // const jsonData = JSON.parse(json);
    console.log("the json data are:");
    // console.log(jsonData);
    const shape=this.createShapeOnLoad(jsonShape);
    if(shape!==null)
    {
      this.addShapeToLayer(shape);
    }

  },
  // trying load things
  createShapeOnLoad(attr) {
      let shape;
      console.log("attr before going to the if condition "+attr);
      console.log("")
      if (attr.name === 'line') {
        shape=this.addLineOnLoad(attr);
      } else if (attr.name === 'square') {
        shape=this.addSquareOnLoad(attr);
      } else if (attr.name === 'rectangle') {
        shape=this.addRectangleOnLoad(attr);
      } else if (attr.name === 'ellipse') {
        shape=this.addEllipseOnLoad(attr);
      } else if (attr.name === 'circle') {
        shape=this.addCircleOnLoad(attr);
      } else if (attr.name === 'triangle') {
        shape=this.addTriangleOnLoad(attr);
      } else if (attr.name === 'star') {
        shape=this.addStarOnLoad(attr);
      }
      else
      {
        shape=null;
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
      rotation:attr.rotation,
      fill: attr.color,
      scaleX: attr.scaleX,
      scaleY:attr.scaleY,
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
      rotation:attr.rotation,
      fill: attr.color,
      scaleX: attr.scaleX,
      scaleY:attr.scaleY,
      stroke: attr.stroke,
      height: attr.height,
      width: attr.width,
      draggable: true,

    });

    return rect;
  },
  addTriangleOnLoad(attr) {
    console.log("I'm adding a triangel with attributes of: "+attr);
    const triangle = new Konva.RegularPolygon({
      name: attr.name,
      x: attr.x,
      y: attr.y,
      rotation:attr.rotation,
      fill: attr.color,
      scaleX: attr.scaleX,
      scaleY:attr.scaleY,
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
      rotation:attr.rotation,
      fill: attr.color,
      scaleX: attr.scaleX,
      scaleY: attr.scaleY,
      stroke: attr.stroke,
      radiusX:attr.radiusX,
      radiusY: attr.radiusY,
      draggable: true,
    });
    return ellipse ;
  },
  addSquareOnLoad(attr) {
    const square = new Konva.Rect({
      name: attr.name,
      x: attr.x,
      y: attr.y,
      rotation:attr.rotation,
      fill: attr.color,
      scaleX: attr.scaleX,
      scaleY:attr.scaleY,
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
      rotation:attr.rotation,
      scaleX: attr.scaleX,
      scaleY:attr.scaleY,
      stroke: attr.stroke,
      points: attr.coordinates,
      draggable: true,
    });
    return line;

  },

  // api handling
  async postCreateShape(shape)
  {
    try{
      const response = await axios.post('http://localhost:8080/api/create' , {
        params: {
          shapeType: shape
        }
      });
      this.loadOneShape(response.data);
    }
    catch(error)
    {
      console.log("Error in post Create: "+error);
    }
  },
  async postCopyShape(index)
  {
    try{
      const response = await axios.post('http://localhost:8080/api/copy', {
        params: {
          shapeIndex: index
        }
      });
      this.loadOneShape(response.data);
    }
    catch(error)
    {
      console.log("Error in post in Copy: "+error);
    }
  },
  async postUndoShape()
  {
    try{
      const response = await axios.post('http://localhost:8080/api/endpointName');
     
      this.loadFromLoad(response.data);
    }
    catch(error)
    {
      console.log("Error in post in Undo: "+error);
    }
  },
  async postRedoShape()
  {
    try{
      const response = await axios.post('http://localhost:8080/api/endpointName');
      this.loadFromLoad(response.data);
    }
    catch(error)
    {
      console.log("Error in post in Redo: "+error);
    }
  },
  async getSavingXML ()
  {
    try{
      await axios.get('http://localhost:8080/api/download/XML');
    }
    catch{
      console.log("Error in get while saving XML: "+error);

    }
  },
  async getSavingJSON()
  {
    try{
      await axios.get('http://localhost:8080/api/download/JSON');
    }
    catch{
      console.log("Error in get while saving JSON: "+error);
    }
  },
  async postSavingXML(file)
  {
    try{
      const response = await axios.post('http://localhost:8080/api/upload/XML', {
        params: {
          file:  file,
        }
      });
      this.loadFromLoad(response.data)
    }
    catch(error)
    {
      console.log("Error in post in saving XML: "+error);
    }
  },
  async postSavingJSON(file)
  {
    try{
      const response = await axios.post('http://localhost:8080/api/upload/JSON', {
        params: {
          file:  file,
        }
      });
    }
    catch(error)
    {
      console.log("Error in post in saving JSON: "+error);
    }
  },
  async postLoadingXML(file)
  {
    try{
      const response = await axios.post('http://localhost:8080/api/upload/XML', {

        params: {
          file:  file,
        }
      });
      this.loadOneShape(response.data);
    }
    catch(error)
    {
      console.log("Error in post in uploading XML: "+error);
    }
  },
  async postLoadingJSON(file)
  {
    try{
      const response = await axios.post('http://localhost:8080/api/upload/JSON', {

        params: {
          file:  file,
        }
      });
    }
    catch(error)
    {
      console.log("Error in post in uploading JSON: "+error);
    }
  },
  async postRemoveShape(index)
  {
    try{
      const response = await axios.post('http://localhost:8080/api/remove', {
        params: {
          shapeIndex: index
        }
      });
    }
    catch(error)
    {
      console.log("Error in post in remove: "+error);
    }
  },
  async postMoveShape(index,shape)
  {
    try{
      const response = await axios.post('http://localhost:8080/api/move', {

        params: {
          x:  shape.x(),
          y:  shape.y(),
          id: index

        }
      });
      this.loadOneShape(response.data);
    }
    catch(error)
    {
      console.log("Error in post in move: "+error);
    }
  },
  async postTransformShape(index,shape)
  {
    try{
      const response = await axios.post('http://localhost:8080/api/transform', {

        params: {

          id: index,
          scaleX:  shape.scaleX(),
          scaley:  shape.scaleY(),
          rotation: shape.rotation()

        }
      });
    }
    catch(error)
    {
      console.log("Error in post in Transform: "+error);
    }
  },
  async postColorShape(index,shape)
  {
    try{
      const response = await axios.post('http://localhost:8080/api/color', {

        params: {

          id: index,
          color: shape.fill()

        }
      });
    }
    catch(error)
    {
      console.log("Error in post in Color: "+error);
    }
  },
  async postStrokeShape(index,shape)
  {
    try{
      const response = await axios.post('http://localhost:8080/api/stroke', {

        params: {

          id: index,
          stroke: shape.stroke()

        }
      });
    }
    catch(error)
    {
      console.log("Error in post in Stroke: "+error);
    }
  },
  async postSelectShape(index)
  {
    try{
      const response = await axios.post('http://localhost:8080/api/select', {

        params: {

          id: index,

        }
      });
    }
    catch(error)
    {
      console.log("Error in post in Select: "+error);
    }
  },

  // The creation of the shapes
  createshape(){
    if(this.selectedShapeType !==null)
      this.postCreateShape(this.selectedShapeType);
  },
  

    createShapeBasic() {
      let shape;
      if (this.selectedShapeType === 'line') {
        shape=this.addLine();
      } else if (this.selectedShapeType === 'square') {
        shape=this.addSquare();
      } else if (this.selectedShapeType === 'rectangle') {
        shape=this.addRectangle();
      } else if (this.selectedShapeType === 'ellipse') {
        shape=this.addEllipse();
      } else if (this.selectedShapeType === 'circle') {
        shape=this.addCircle();
      } else if (this.selectedShapeType === 'triangle') {
        shape=this.addTriangle();
      } else if (this.selectedShapeType === 'star') {
        shape=this.addStar();
      }
      else
      {
        console.log("no shape chosen !");
        return;
      }
      this.addShapeToLayer(shape);
    },
},

}

</script>

<style>
.taskbar {
  display: flex;
  align-items: center;
  justify-content: space-between center;
  padding: 10px;
  background-color: #f0f0f0;
  border-bottom: 1px solid #ccc;
}

.sketch-area {
  margin: auto;
  margin-top: 20px;
  width: auto;
  height: auto;
  border: 3px solid #ccc;
  padding: 10px;
}
</style>


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