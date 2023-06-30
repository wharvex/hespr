package com.wharvex.hespr.parser.nodes;

import com.wharvex.hespr.parser.ParserHelper;
import java.util.List;

public class ForNode extends StatementNode {

  private final Node from;
  private final Node to;
  private final VariableReferenceNode varRef;
  private final List<StatementNode> statements;

  public ForNode(VariableReferenceNode varRef, Node from, Node to, List<StatementNode> statements,
      int lineNum) {
    this.varRef = varRef;
    this.from = from;
    this.to = to;
    this.statements = statements;
    this.lineNum = lineNum;
  }

  public VariableReferenceNode getVarRef() {
    return this.varRef;
  }

  public Node getFrom() {
    return this.from;
  }

  public Node getTo() {
    return this.to;
  }

  public List<StatementNode> getStatements() {
    return this.statements;
  }

  public String toString() {
    return
        "for (var: "
            + this.getVarRef()
            + ", from: "
            + this.getFrom()
            + ", to: "
            + this.getTo()
            + ") {\n    "
            + ParserHelper.listToString(this.statements)
            + "\n    }";
  }
}
