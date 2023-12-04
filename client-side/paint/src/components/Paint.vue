<template>
  <div>
    <canvas
      ref="canvas"
      @mousedown="startDrawingOrDragging"
      @mousemove="drawOrDrag"
      @mouseup="stopDrawingOrDragging"
      width="800"
      height="800"
    ></canvas>

    <div class="shape-buttons">
      <button
        v-for="shape in shapes_icons"
        :key="shape.value"
        @click="selectShape(shape.value)"
        :class="{ active: selectedShape === shape.value }"
      >
        {{ shape.icon }}
      </button>
    </div>
  </div>
</template>

<script>
export default {
  data() {
    return {
      canvas: null,
      ctx: null,
      drawing: false,
      selectedShape: "line",
      isDrawing: false,
      startX: 0,
      startY: 0,
      endX: 0,
      endY: 0,
      color: "#000",
      id: 0,
      max_id: 0,
      selectedShapeId: null,
      shapes_icons: [
        { value: "line", icon: "Line" },
        { value: "square", icon: "Square" },
        { value: "rectangle", icon: "Rectangle" },
        { value: "circle", icon: "Circle" },
        { value: "ellipse", icon: "Ellipse" },
        { value: "triangle", icon: "Triangle" },
        { value: "move", icon: "Move" },
        { value: "resize", icon: "Resize" },
        { value: "delete", icon: "Delete" },
      ],
      shapes: [],
    };
  },
  mounted() {
    this.canvas = this.$refs.canvas;
    this.ctx = this.canvas.getContext("2d");
    this.drawAllShapes();
  },
  methods: {
    selectShape(shape) {
      this.selectedShape = shape;
      this.isDrawing = shape !== "move" && shape !== "resize" && shape !== "delete";
    },
    startDrawingOrDragging(e) {
      this.startX = e.clientX - this.canvas.offsetLeft;
      this.startY = e.clientY - this.canvas.offsetTop;

      // Check if a shape is clicked for dragging
      const clickedShape = this.shapes.find((shape) => {
        return this.isPointInsideShape(
          this.startX,
          this.startY,
          shape.startX,
          shape.startY,
          shape.endX,
          shape.endY,
          shape.type
        );
      });

      if (clickedShape) {
        this.selectedShapeId = clickedShape.id;
      } else {
        this.drawing = true;
        this.id = this.max_id + 1;
        this.max_id = this.id;
      }
      const selectedShapeIndex = this.shapes.findIndex(
          (shape) => shape.id === this.selectedShapeId
        );
      if (this.selectedShape === "delete") {
            console.log("delete shape3")
            this.shapes.splice(selectedShapeIndex, 1);
            this.drawAllShapes();
            this.selectedShapeId = null;
          }
    },
    drawOrDrag(e) {
      if (this.drawing) {
        this.endX = e.clientX - this.canvas.offsetLeft;
        this.endY = e.clientY - this.canvas.offsetTop;

        this.drawAllShapes();
        switch (this.selectedShape) {
          case "line":
            this.drawLine(this.startX, this.startY, this.endX, this.endY);
            break;
          case "square":
            this.drawSquare(this.startX, this.startY, this.endX, this.endY);
            break;
          case "rectangle":
            this.drawRectangle(this.startX, this.startY, this.endX, this.endY);
            break;
          case "circle":
            this.drawCircle(this.startX, this.startY, this.endX, this.endY);
            break;
          case "ellipse":
            this.drawEllipse(this.startX, this.startY, this.endX, this.endY);
            break;
          case "triangle":
            this.drawTriangle(this.startX, this.startY, this.endX, this.endY);
            break;
        }
      } else if (this.selectedShapeId !== null) {
        // Handle dragging if a shape is selected
        const selectedShapeIndex = this.shapes.findIndex(
          (shape) => shape.id === this.selectedShapeId
        );

        if (selectedShapeIndex !== -1) {
          const offsetX = e.clientX - this.canvas.offsetLeft - this.startX;
          const offsetY = e.clientY - this.canvas.offsetTop - this.startY;

          if (this.selectedShape === "resize") {
            // Handle proportional resize
            const width = this.shapes[selectedShapeIndex].endX - this.shapes[selectedShapeIndex].startX;
            const height = this.shapes[selectedShapeIndex].endY - this.shapes[selectedShapeIndex].startY;

            const aspectRatio = width / height;
            const moveX = offsetX;

            const moveY = offsetX / aspectRatio;

            this.shapes[selectedShapeIndex].endX += moveX;
            this.shapes[selectedShapeIndex].endY += moveY;
          } else if (this.selectedShape === "move") {
            // Handle normal dragging
            this.shapes[selectedShapeIndex].startX += offsetX;
            this.shapes[selectedShapeIndex].endX += offsetX;
            this.shapes[selectedShapeIndex].startY += offsetY;
            this.shapes[selectedShapeIndex].endY += offsetY;
          }

          this.drawAllShapes();

          this.startX = e.clientX - this.canvas.offsetLeft;
          this.startY = e.clientY - this.canvas.offsetTop;
        }
      }
    },
    stopDrawingOrDragging() {
      if (this.drawing) {
        this.drawing = false;
        this.saveShape();
      } else if (this.selectedShapeId !== null) {
        this.selectedShapeId = null;
      }
    },
   isPointInsideShape(x, y, startX, startY, endX, endY, shapeType) {
      switch (shapeType) {
        case "line":
          return this.isPointInsideRectangle(
            x,
            y,
            Math.min(startX, endX),
            Math.min(startY, endY),
            Math.max(startX, endX),
            Math.max(startY, endY)
          );
        case "square":
        case "rectangle":
          return this.isPointInsideRectangle(x, y, startX, startY, endX, endY);
        case "circle":
        case "ellipse":
        case "triangle":
          return this.isPointInsideShapeType(x, y, startX, startY, endX, endY, shapeType);
        default:
          return false;
      }
    },
    isPointInsideRectangle(x, y, startX, startY, endX, endY) {
      return x >= Math.min(startX,endX) && x <= Math.max(startX,endX) && y >= Math.min(startY,endY) && y <= Math.max(startY,endY);
    },
    isPointInsideShapeType(x, y, startX, startY, endX, endY, shapeType) {
      switch (shapeType) {
        case "circle":
          const radius = Math.sqrt((endX - startX) ** 2 + (endY - startY) ** 2);
          return (
            Math.sqrt((x - startX) ** 2 + (y - startY) ** 2) <= radius
          );
        case "ellipse":
          // Check if the point is inside the ellipse
          const radiusX = Math.abs((endX - startX) / 2);
          const radiusY = Math.abs((endY - startY) / 2);
          const centerX = startX < endX ? startX + radiusX : startX - radiusX;
          const centerY = startY < endY ? startY + radiusY : startY - radiusY;

          return (
            ((x - centerX) ** 2) / (radiusX ** 2) +
            ((y - centerY) ** 2) / (radiusY ** 2) <= 1
          );
        case "triangle":
         console.log("triangle")
          const minX = Math.min(startX, endX);
          const maxX = Math.max(startX, endX);
          const minY = Math.min(startY, endY);
          const maxY = Math.max(startY, endY);
          if (x < minX || x > maxX || y < minY || y > maxY) {
            console.log("out")
            return false;
          }
          const midX = startX + (endX - startX) / 2;
          const midY = startY + (endY - startY) / 2;
          if(y<midY && Math.abs(y-minY)/Math.abs(minY-maxY) < Math.abs(x-minX)/Math.abs(minX-maxX)){
            console.log("out2")
            return false;
          }
          return true;

        default:
          return false;
      }
    },
    isPointInsideRectangle(x, y, startX, startY, endX, endY) {
      return x >= Math.min(startX,endX) && x <= Math.max(startX,endX) && y >= Math.min(startY,endY) && y <= Math.max(startY,endY);
    },
    drawAllShapes() {
      this.ctx.clearRect(0, 0, this.canvas.width, this.canvas.height);
      this.shapes.forEach((shape) => {
        switch (shape.type) {
          case "line":
            this.drawLine(shape.startX, shape.startY, shape.endX, shape.endY);
            break;
          case "square":
            this.drawSquare(shape.startX, shape.startY, shape.endX, shape.endY);
            break;
          case "rectangle":
            this.drawRectangle(
              shape.startX,
              shape.startY,
              shape.endX,
              shape.endY
            );
            break;
          case "circle":
            this.drawCircle(shape.startX, shape.startY, shape.endX, shape.endY);
            break;
          case "ellipse":
            this.drawEllipse(
              shape.startX,
              shape.startY,
              shape.endX,
              shape.endY
            );
            break;
          case "triangle":
            this.drawTriangle(
              shape.startX,
              shape.startY,
              shape.endX,
              shape.endY
            );
            break;
        }
      });
    },
    drawLine(startX, startY, endX, endY) {
      this.ctx.beginPath();
      this.ctx.moveTo(startX, startY);
      this.ctx.lineTo(endX, endY);
      this.ctx.stroke();
    },
    drawCircle(startX, startY, endX, endY) {
      const radius = Math.sqrt((endX - startX) ** 2 + (endY - startY) ** 2);
      this.ctx.beginPath();
      this.ctx.arc(startX, startY, radius, 0, 2 * Math.PI);
      this.ctx.stroke();
    },
    drawEllipse(startX, startY, endX, endY) {
      const radiusX = Math.abs((endX - startX) / 2);
      const radiusY = Math.abs((endY - startY) / 2);
      const centerX = startX < endX ? startX + radiusX : startX - radiusX;
      const centerY = startY < endY ? startY + radiusY : startY - radiusY;

      this.ctx.beginPath();
      this.ctx.ellipse(centerX, centerY, radiusX, radiusY, 0, 0, 2 * Math.PI);
      this.ctx.stroke();
    },
    drawSquare(startX, startY, endX, endY) {
      const side = Math.min(Math.abs(endX - startX), Math.abs(endY - startY));
      const directionX = endX > startX ? 1 : -1;
      const directionY = endY > startY ? 1 : -1;
      this.ctx.strokeRect(startX, startY, directionX * side, directionY * side);
    },
    drawRectangle(startX, startY, endX, endY) {
      this.ctx.strokeRect(startX, startY, endX - startX, endY - startY);
    },
    drawTriangle(startX, startY, endX, endY) {
      const midX = startX + (endX - startX) / 2;
      this.ctx.beginPath();
      this.ctx.moveTo(midX, startY);
      this.ctx.lineTo(startX, endY);
      this.ctx.lineTo(endX, endY);
      this.ctx.closePath();
      this.ctx.stroke();
    },
    saveShape() {
      this.shapes.push({
        id: this.id,
        type: this.selectedShape,
        startX: this.startX,
        startY: this.startY,
        endX: this.endX,
        endY: this.endY,
      });
    },
  },
};
</script>

<style scoped>
  canvas {
    border: 5px solid #000;
  }

  .shape-buttons {
    display: flex;
    gap: 10px;
  }

  .shape-buttons button {
    font-size: 18px;
    padding: 8px;
    border: 1px solid #ccc;
    cursor: pointer;
    background-color: #f9f9f9;
  }

  .shape-buttons button.active {
    border-color: #000;
  }

  .shape-buttons button:disabled {
    cursor: not-allowed;
  }
</style>
