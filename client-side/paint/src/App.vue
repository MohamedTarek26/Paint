<template>
  <div>
    <!-- Taskbar -->
    <div class="taskbar">
      <button @click="saveShapes">Save</button>
      <button @click="loadShapes">Load</button>
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
      <button @click="deleteShape" v-if="selectedShape !== null">Delete</button>
      <div v-if="selectedShape !== null">
        <label>Color:</label>
        <input type="color"  v-model="colorValue" @change="changeColor"/>
        <!-- <label>X:</label>
        <input type="x_select" v-model="xValue" @change="changex" />
        <label>Y:</label>
        <input type="number" v-model="yValue" @change="changey" />  -->
        <label>Transparent:</label>
        <input type="checkbox" v-model="isTransparent" @change="beTransperant" />``
        <!-- <label>Width:</label>
        <input type="number" v-model="shapes[selectedShape].width" />
        <label>Height:</label> 
        <input type="number" v-model="shapes[selectedShape].height" /> -->

    </div>
    </div>
    <v-stage :config="stageSize">
    <div class="sketch-area">
      <div ref="stageContainer"></div>
    </div>
  </v-stage>
  </div>
</template>

<script>
import Konva from 'konva';
import { Stage, Layer, Rect, Circle } from 'konva';
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
      colorValue: 'black',
      xValue: 0,
      yValue: 0,
      isTransparent: false,
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
      width: 700,
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
      const shape = e.target;
      this.selectShape(shape);
      this.updateTransformer(shape);
    });
  },

  methods: {
    changeColor(){

      const shape = this.shapes[this.selectedShape];
      if(this.isTransparent){
        shape.stroke(this.colorValue);
        return;
      }
      console.log(this.colorValue);
      shape.fill(this.colorValue);
      this.layer.draw();
    },
    beTransperant(){
      console.log(this.isTransparent);
      const shape = this.shapes[this.selectedShape];
      if(this.isTransparent){
        shape.fill('transparent');
        shape.stroke(this.colorValue);
      }
      else{
        shape.fill(this.colorValue);
        shape.stroke('transparent');
      }
      this.layer.draw();
    },
    changex(){
      console.log(this.xValue);
      const shape = this.shapes[this.selectedShape];
      shape.x(this.xValue);
      this.layer.draw();
    },
    changey(){
      console.log(this.yValue);
      const shape = this.shapes[this.selectedShape];
      shape.y(this.yValue);
      this.layer.draw();
    },
    saveShapes() {
      // Logic to save shapes to a file (JSON/XML)
      console.log('Shapes saved');
    },
    loadShapes() {
      // Logic to load shapes from a file (JSON/XML)
      console.log('Shapes loaded');
    },
    createShape() {
      if (this.selectedShapeType === 'line') {
        this.addLine();
      } else if (this.selectedShapeType === 'square') {
        this.addSquare();
      } else if (this.selectedShapeType === 'rectangle') {
        this.addRectangle();
      } else if (this.selectedShapeType === 'ellipse') {
        this.addEllipse();
      } else if (this.selectedShapeType === 'circle') {
        this.addCircle();
      } else if (this.selectedShapeType === 'triangle') {
        this.addTriangle();
      } else if (this.selectedShapeType === 'star') {
        this.addStar();
      }
    },

    addCircle() {
      const circ = new Konva.Circle({
        name: this.shapeIdCounter,
        x: 100,
        y: 100,
        radius: 50,
        fill: 'green',
        draggable: true,
      });

      this.shapeIdCounter++;
      this.addShapeToLayer(circ);
    },
    addRectangle() {
      const rect = new Konva.Rect({
        name: this.shapeIdCounter,
        x: 50,
        y: 50,
        width: 200,
        height: 100,
        fill: 'black',
        // stroke: '',
        draggable: true,
      });

      this.shapeIdCounter++;
      this.addShapeToLayer(rect);
    },
    addTriangle() {
      const triangle = new Konva.RegularPolygon({
        name: this.shapeIdCounter,
        x: 100,
        y: 100,
        sides: 3,
        radius: 70,
        fill: 'blue',
        draggable: true,
      });

      this.shapeIdCounter++;
      this.addShapeToLayer(triangle);
    },
    addStar() {
      const star = new Konva.Star({
        name: this.shapeIdCounter,
        x: 100,
        y: 100,
        numPoints: 5,
        innerRadius: 40,
        outerRadius: 70,
        fill: 'yellow',
        draggable: true,
      });

      this.shapeIdCounter++;
      this.addShapeToLayer(star);
    },

    addEllipse() {
      const ellipse = new Konva.Ellipse({
        name: this.shapeIdCounter,
        x: 100,
        y: 100,
        radiusX: 50,
        radiusY: 30,
        fill: 'orange',
        draggable: true,
      });

      this.shapeIdCounter++;
      this.addShapeToLayer(ellipse);
    },
    addSquare() {
      const square = new Konva.Rect({
        name: this.shapeIdCounter,
        x: 100,
        y: 100,
        width: 100,
        height: 100,
        fill: 'cyan',
        draggable: true,
      });

      this.shapeIdCounter++;
      this.addShapeToLayer(square);
    },
    addLine() {
      const line = new Konva.Line({
        name: this.shapeIdCounter,
        points: [50, 20, 200, 20],
        stroke: 'purple',
        strokeWidth: 5,
        draggable: true,
      });

      this.shapeIdCounter++;
      this.addShapeToLayer(line);
    },

    addShapeToLayer(shape) {
      console.log('addShapeToLayer');
      console.log(shape.x());
      this.layer = this.stage.findOne('Layer');
      this.layer.add(shape);
      this.shapes.push(shape);
      this.stage.draw();
      console.log(this.shapes);
    },

    selectShape(shape) {
      console.log('selectShape');
      // console.log(shape);
      this.selectedShape = null;
      // console.log(this.selectedShape);
      // console.log(this.shapes);
      console.log("ya tareeeeek: "+shape.getClassName());
      // console.log(shape);
      // console.log("index: "+shape.index);
      // console.log("x: "+shape.x());
      if(shape.getClassName() !== 'Stage'){
        console.log("x is not null");
      
      for (let i = 0; i < this.shapes.length; i++) {
        if (this.shapes[i].index === shape.index) {
          console.log('went here');
          console.log(i);
          console.log(this.shapes[i].index);
          this.selectedShape = i;
          this.colorValue = this.shapes[i].fill();
          this.xValue = this.shapes[i].x();
          this.yValue = this.shapes[i].y();
          this.isTransparent = this.shapes[i].fill() === 'transparent';
          break;
        }
      }
    }
    else{
      this.selectedShape = null;
    }
      console.log(this.transformer);
      //this.selectedShape = this.shapes.findIndex((s) =>s.index === shape.index);
      // if(this.selectedShape === -1) {
      //   this.selectedShape = null;
      // }
      
      console.log(this.selectedShape);
      console.log("end of selectShape");
    },

    updateTransformer(shape) {
      if(this.selectedShape !== null) {
        this.transformer.attachTo(shape);
        console.log(this.transformer);
        this.transformer.moveToTop();
        this.stage.batchDraw();
      }
      else{
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
      
      console.log("7amdela 3al salamaaaa");
      this.selectedShape=null;
    },
    deleteShape() {
      console.log('deleteShape');
      console.log(this.selectedShape);

      // Remove shape from the list
      const shape = this.shapes[this.selectedShape];
      // console.log(shape._x);
      console.log(shape);
      // console.log(shape.$refs);
      console.log(this.shapes);
      console.log(this.selectedShape);
      // console.log(this.shapes[this.selectedShape].x());
      this.shapes.splice(this.selectedShape, 1);
      console.log(this.shapes);
      this.selectedShape = null;
      // Clear the layer and redraw remaining shapes
      this.layer.removeChildren();
      this.transformer.detach();
      this.previewShapes();
    }

  },
};
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
