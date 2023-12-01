<template>
  <div>
    <canvas
      ref="canvas"
      @mousedown="startDrawing"
      @mousemove="draw"
      @mouseup="stopDrawing"
      width="600"
      height="600"
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
      startX: 0,
      startY: 0,
      endX: 0,
      endY: 0,
      color: "#000",
      id: 0, //Unique id for each shape
      max_id: 0, //The max id of the shapes in the server
      shapes_icons: [
        { value: "line", icon: "↔️" },
        { value: "square", icon: "◼️" },
        { value: "rectangle", icon: "⬛" },
        { value: "circle", icon: "⭕" },
        { value: "ellipse", icon: "⭑" },
        { value: "triangle", icon: "▲" },
      ],
      shapes: [], // Array to store drawn shapes in the client side to be drawn on the canvas
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
    },
    startDrawing(e) {
      this.drawing = true;
      this.startX = e.clientX - this.canvas.offsetLeft;
      this.startY = e.clientY - this.canvas.offsetTop;
    },
    draw(e) {
      if (!this.drawing) return;

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
    },
    stopDrawing() {
      this.drawing = false;
      this.saveShape();
      //Send the shape to the server
    },
    colorize() {
      //Change the color of the shape in the client side and send the new color to the server
    },
    move() {
      //Move the shape in the client side and send the new coordinates to the server
    },
    resize() {
      //Resize the shape in the client side and send the new coordinates to the server
    },
    delete() {
      //Delete the shape from the client side and send the id to the server
    },
    undo() {
      //Remove the shape from the server but keep it in the client side list
    },
    redo() {
      //Get the shape from the history list and send it to the server
    },
    copy() {
      //Copy the shape in the client side and send the copy command to the server to create a copy with new id
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
</style>
  