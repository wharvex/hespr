package com.wharvex.hespr.parser.nodes;

import com.wharvex.hespr.parser.ParserHelper;
import java.util.ArrayList;
import java.util.List;

public class FunctionCallNode extends StatementNode {
  private List<ArgumentNode> args;
  private final String funcName;

  public FunctionCallNode(String funcName, List<ArgumentNode> args, int lineNum) {
    this.funcName = funcName;
    this.args = args;
    this.lineNum = lineNum;
  }

  public FunctionCallNode(String funcName, int lineNum) {
    this.funcName = funcName;
    this.lineNum = lineNum;
    this.args = new ArrayList<>();
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
