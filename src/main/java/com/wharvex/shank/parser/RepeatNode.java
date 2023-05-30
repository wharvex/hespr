package com.wharvex.shank.parser;

import java.util.List;

public class RepeatNode extends StatementNode {

  Node condition;
  private List<StatementNode> statements;

  public RepeatNode(Node condition, List<StatementNode> statements) {
    this.condition = condition;
    this.statements = statements;
  }

  public Node getCondition() {
    return this.condition;
  }

  public List<StatementNode> getStatements() {
    return this.statements;
  }

  public String toString() {
    return "repeat until "
        + this.getCondition()
        + " {\n    "
        + Parser.listToString(this.statements)
        + "\n    }";
  }
}
