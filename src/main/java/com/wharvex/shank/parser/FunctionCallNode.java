package com.wharvex.shank.parser;

import java.util.List;

public class FunctionCallNode extends StatementNode {
  private List<ParameterNode> args;
  private String funcName;

  public FunctionCallNode(String funcName, List<ParameterNode> args) {
    this.funcName = funcName;
    this.args = args;
  }

  public FunctionCallNode(String funcName) {
    this.funcName = funcName;
  }

  public String getFuncName() {
    return this.funcName;
  }

  public List<ParameterNode> getArgs() {
    return this.args;
  }

  public String toString() {
    return this.getFuncName()
        + "(args: "
        + (this.getArgs() == null ? "none" : Parser.listToStringInline(this.getArgs()))
        + ")";
  }
}
