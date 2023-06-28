package com.wharvex.shank.parser.nodes;

public class MathOpNode extends Node {

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

  public enum MathOpType {
    ADD,
    SUBTRACT,
    MULTIPLY,
    DIVIDE,
    MOD
  }

  private LeftSide leftSide;
  private RightSide rightSide;

  private MathOpType mathOpType;

  public MathOpNode(MathOpType mathOpType, Node leftNode, Node rightNode, int lineNum) {
    this.mathOpType = mathOpType;
    this.leftSide = new LeftSide(leftNode);
    this.rightSide = new RightSide(rightNode);
    this.lineNum = lineNum;
  }

  public Node getLeftSide() {
    return this.leftSide.node;
  }

  public Node getRightSide() {
    return this.rightSide.node;
  }

  public MathOpType getMathOpType() {
    return this.mathOpType;
  }

  public String toString() {
    // If MathOpNodes are nested, you need to recursively go through them to print them, but how do
    // you know when to stop?
    // System.out.println("hi");
    return "("
        + this.mathOpType.toString()
        + " "
        + this.leftSide.toString()
        + " "
        + this.rightSide.toString()
        + ")";
  }
}
