package com.wharvex.shank.parser;

public class ArgumentNode extends Node {

  VariableReferenceNode varArg;
  Node nonVarArg;

  public ArgumentNode(Node varOrNonVarArg, boolean isVar) {
    if (isVar) {
      this.varArg = (VariableReferenceNode) varOrNonVarArg;
    } else {
      this.nonVarArg = varOrNonVarArg;
    }
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
