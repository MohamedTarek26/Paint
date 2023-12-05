<template>
  <div>
    <v-stage :config="stageSize">
      <v-layer ref="myLayer">
        <!-- Shapes will be added dynamically here -->
      </v-layer>
    </v-stage>
    <button v-for="(shape, index) in shapes" :key="index" @click="deleteShape(index)">
      Delete Shape {{ index }}
    </button>
  </div>
</template>

<script>
import { Stage, Layer, Rect, Circle } from 'konva';

export default {
  data() {
    return {
      stageSize: {
        width: 500,
        height: 300
      },
      shapes: [] // List of shapes
    };
  },
  mounted() {
    this.previewShapes();
  },
  methods: {
    previewShapes() {
      const layer = this.$refs.myLayer.getStage();

      // Loop through the shapes and add them to the layer for preview
      this.shapes.forEach(shape => {
        let konvaShape;
        // Create different types of shapes based on your shape data
        if (shape.type === 'rectangle') {
          konvaShape = new Rect({
            x: shape.x,
            y: shape.y,
            width: shape.width,
            height: shape.height,
            fill: shape.fill
          });
        } else if (shape.type === 'circle') {
          konvaShape = new Circle({
            x: shape.x,
            y: shape.y,
            radius: shape.radius,
            fill: shape.fill
          });
        }
        layer.add(konvaShape);
      });

      // Update the layer
      layer.batchDraw();
    },
    deleteShape(index) {
      const layer = this.$refs.myLayer.getStage();

      // Remove shape from the list
      this.shapes.splice(index, 1);

      // Clear the layer and redraw remaining shapes
      layer.destroyChildren();
      this.previewShapes();
    }
  },
  components: {
    vStage: Stage,
    vLayer: Layer
  }
};
</script>