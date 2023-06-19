package com.wharvex.shank.parser;

public class ArgumentNode extends Node {

  VariableReferenceNode varArg;
  Node nonVarParam;

  public ArgumentNode(Node varOrNonVarArg, boolean isVar) {
    if (isVar) {
      this.varArg = (VariableReferenceNode) varOrNonVarArg;
    } else {
      this.nonVarParam = varOrNonVarArg;
    }
  }

  public VariableReferenceNode getVarArg() {
    return varArg;
  }

  public Node getNonVarArg() {
    return nonVarParam;
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
