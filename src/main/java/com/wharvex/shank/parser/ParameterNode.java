package com.wharvex.shank.parser;

public class ParameterNode extends Node {

  VariableReferenceNode varParam;
  Node nonVarParam;

  public ParameterNode(Node varOrNonVarParam, boolean isVar) {
    if (isVar) {
      this.varParam = (VariableReferenceNode) varOrNonVarParam;
    } else {
      this.nonVarParam = varOrNonVarParam;
    }
  }

  public VariableReferenceNode getVarParam() {
    return varParam;
  }

  public Node getNonVarParam() {
    return nonVarParam;
  }

  public Node getParam() {
    if (this.getVarParam() != null) {
      return this.getVarParam();
    } else {
      return this.getNonVarParam();
    }
  }

  public boolean isVar() {
    return this.getVarParam() != null;
  }

  public String toString() {
    if (this.getVarParam() == null) {
      return this.getNonVarParam().toString();
    } else {
      return this.getVarParam().toString() + " (var)";
    }
  }
}
