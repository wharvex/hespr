package com.wharvex.shank.parser.nodes;

import com.wharvex.shank.parser.ParserHelper;
import java.util.List;

public class FunctionCallNode extends StatementNode {
  private List<ArgumentNode> args;
  private String funcName;

  public FunctionCallNode(String funcName, List<ArgumentNode> args) {
    this.funcName = funcName;
    this.args = args;
  }

  public FunctionCallNode(String funcName) {
    this.funcName = funcName;
  }

  public String getFuncName() {
    return this.funcName;
  }

  public List<ArgumentNode> getArgs() {
    return this.args;
  }

  public String toString() {
    return this.getFuncName()
        + "(args: "
        + (this.getArgs() == null ? "none" : ParserHelper.listToStringInline(this.getArgs()))
        + ")";
  }
}
