package com.wharvex.hespr.parser.nodes;

import com.wharvex.hespr.parser.ParserHelper;
import java.util.List;

public class RepeatNode extends StatementNode {

  Node condition;
  private List<StatementNode> statements;

  public RepeatNode(Node condition, List<StatementNode> statements, int lineNum) {
    this.condition = condition;
    this.statements = statements;
    this.lineNum = lineNum;
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
        + ParserHelper.listToString(this.statements)
        + "\n    }";
  }
}
