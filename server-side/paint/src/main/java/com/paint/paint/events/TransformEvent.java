package com.paint.paint.events;

import com.paint.paint.ShapeManager.Director;

public class TransformEvent implements Event {

  private float oldScaleX;
  private float oldScaleY;
  private float newScaleX;
  private float newScaleY;
  private float oldX;
  private float oldY;
  private float newX;
  private float newY;
  private float oldRotation;
  private float newRotation;
  private Director director;
  private int shapeIndex;

  public TransformEvent(
    Director director,
    int shapeIndex,
    float oldScaleX,
    float oldScaleY,
    float oldRotation,
    float newScaleX,
    float newScaleY,
    float newRotation,
    float oldX,
    float oldY,
    float newX,
    float newY
  ) {
    this.oldScaleX = oldScaleX;
    this.oldScaleY = oldScaleY;
    this.oldRotation = oldRotation;
    this.newScaleX = newScaleX;
    this.newScaleY = newScaleY;
    this.newRotation = newRotation;
    this.director = director;
    this.shapeIndex = shapeIndex;
    this.oldX = oldX;
    this.oldY = oldY;
    this.newX= newX;
    this.newY = newY;
  }

  @Override
  public void apply() {
    director.resizeShape(shapeIndex, newScaleX, newScaleY);
    director.rotateShape(shapeIndex, newRotation);
    director.moveShape(shapeIndex, newX, newY);
  }

  @Override
  public void revert() {
    director.resizeShape(shapeIndex, oldScaleX, oldScaleY);
    director.rotateShape(shapeIndex, oldRotation);
    director.moveShape(shapeIndex, oldX, oldY);
  }

  public double getOldScaleX() {
    return oldScaleX;
  }

  public double getOldScaleY() {
    return oldScaleY;
  }

  public double getNewScaleX() {
    return newScaleX;
  }

  public double getNewScaleY() {
    return newScaleY;
  }

  public float getOldRotation() {
    return oldRotation;
  }

  public float getNewRotation() {
    return newRotation;
  }

  public Director getDirector() {
    return director;
  }

  public int getShapeIndex() {
    return shapeIndex;
  }
  public float getOldX() {
    return oldX;
  }

  public float getOldY() {
    return oldY;
  }

  public float getNewX() {
    return newX;
  }
  public float getNewY() {
    return newY;
  }
}
