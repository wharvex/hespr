package com.wharvex.hespr.parser.nodes;

import com.wharvex.hespr.parser.ParserHelper;
import java.util.List;

public class FunctionNode extends Node {

  private final String name;
  private final List<VariableNode> params;
  private List<VariableNode> constants;
  private List<VariableNode> variables;
  private List<StatementNode> statements;

  public FunctionNode(
      String name,
      List<VariableNode> params,
      List<VariableNode> variables,
      List<VariableNode> constants,
      List<StatementNode> statements, int lineNum) {
    this.name = name;
    this.params = params;
    this.variables = variables;
    this.constants = constants;
    this.statements = statements;
  }

  public FunctionNode(String name, List<VariableNode> params) {
    this.name = name;
    this.params = params;
  }

  public String getName() {
    return this.name;
  }

  public boolean isVariadic() {
    return false;
  }

  public List<VariableNode> getParams() {
    return this.params;
  }

  public List<VariableNode> getVariables() {
    return this.variables;
  }

  public List<VariableNode> getConstants() {
    return this.constants;
  }

  public List<StatementNode> getStatements() {
    return this.statements;
  }

  @Override
  public String toString() {
    return this.getName()
        + " {"
        + "\n  Params:\n"
        + "    "
        + ParserHelper.listToString(this.params)
        + "\n\n  Variables:\n"
        + "    "
        + ParserHelper.listToString(this.variables)
        + "\n\n  Constants:\n"
        + "    "
        + ParserHelper.listToString(this.constants)
        + "\n\n  Statements:\n"
        + "    "
        + ParserHelper.listToString(this.statements)
        + "\n}\n";
  }
}
