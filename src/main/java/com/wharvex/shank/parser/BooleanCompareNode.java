package com.wharvex.shank.parser;

public class BooleanCompareNode extends Node {

  private class LeftSide {

    Node node;

    public LeftSide(Node node) {
      this.node = node;
    }

    public String toString() {
      return this.node.toString();
    }
  }

  private class RightSide {

    Node node;

    public RightSide(Node node) {
      this.node = node;
    }

    public String toString() {
      return this.node.toString();
    }
  }

  public enum CompareType {
    LESSTHAN,
    GREATERTHAN,
    EQUALS,
    GREATEREQUAL,
    LESSEQUAL,
    NOTEQUAL
  }

  private LeftSide leftSide;
  private RightSide rightSide;

  private CompareType compareType;

  public BooleanCompareNode(CompareType compareType, Node leftNode, Node rightNode) {
    this.compareType = compareType;
    this.leftSide = new LeftSide(leftNode);
    this.rightSide = new RightSide(rightNode);
  }

  public CompareType getCompareType() {
    return this.compareType;
  }

  public Node getLeftSide() {
    return this.leftSide.node;
  }

  public Node getRightSide() {
    return this.rightSide.node;
  }

  /**
   * Polish notation style, like MathOpNode. leftSide is [compareType] rightSide
   */
  public String toString() {
    return "("
        + this.compareType.toString()
        + " "
        + this.leftSide.toString()
        + " "
        + this.rightSide.toString()
        + ")";
  }
}
