package com.wharvex.hespr.parser.nodes;

public class ArgumentNode extends Node {

  VariableReferenceNode varArg;
  Node nonVarArg;

  public ArgumentNode(Node varOrNonVarArg, boolean isVar, int lineNum) {
    if (isVar) {
      this.varArg = (VariableReferenceNode) varOrNonVarArg;
    } else {
      this.nonVarArg = varOrNonVarArg;
    }
    this.lineNum = lineNum;
  }

  private VariableReferenceNode getVarArg() {
    return varArg;
  }

  private Node getNonVarArg() {
    return nonVarArg;
  }

  public Node getArg() {
    if (this.getVarArg() != null) {
      return this.getVarArg();
    } else {
      return this.getNonVarArg();
    }
  }

  public boolean isVar() {
    return this.getVarArg() != null;
  }

  public String toString() {
    if (this.getVarArg() == null) {
      return this.getNonVarArg().toString();
    } else {
      return this.getVarArg().toString() + " (var)";
    }
  }
}
