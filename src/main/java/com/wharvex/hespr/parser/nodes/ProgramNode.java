package com.wharvex.hespr.parser.nodes;

import java.util.HashMap;

public class ProgramNode extends Node {

  public HashMap<String, FunctionNode> functions;

  /**
   * Constructor
   */
  public ProgramNode() {
    this.functions = new HashMap<String, FunctionNode>();
  }

  public void addFunction(FunctionNode function) {
    this.functions.put(function.getName(), function);
  }

  public HashMap<String, FunctionNode> getFunctions() {
    return functions;
  }

  public String toString() {
    String ret = "";
    for (FunctionNode fn : this.functions.values()) {
      ret += (fn.toString() + "\n");
    }
    return ret;
  }
}
