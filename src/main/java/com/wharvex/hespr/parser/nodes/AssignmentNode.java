package com.wharvex.hespr.parser.nodes;

public class AssignmentNode extends StatementNode {

  private VariableReferenceNode leftSide;
  private Node rightSide;

  public AssignmentNode(VariableReferenceNode leftSide, Node rightSide, int lineNum) {
    this.leftSide = leftSide;
    this.rightSide = rightSide;
    this.lineNum = lineNum;
  }

  public VariableReferenceNode getLeftSide() {
    return this.leftSide;
  }

  public Node getRightSide() {
    return this.rightSide;
  }

  public String toString() {
    return this.getLeftSide() + " := " + this.getRightSide();
  }
}
