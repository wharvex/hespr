package com.wharvex.shank.parser.nodes;

import com.wharvex.shank.parser.ParserHelper;
import java.util.List;

public class ForNode extends StatementNode {

  private Node from;
  private Node to;
  private VariableReferenceNode varRef;
  private List<StatementNode> statements;

  public ForNode(VariableReferenceNode varRef, Node from, Node to, List<StatementNode> statements) {
    this.varRef = varRef;
    this.from = from;
    this.to = to;
    this.statements = statements;
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
    String ret =
        "for (var: "
            + this.getVarRef()
            + ", from: "
            + this.getFrom()
            + ", to: "
            + this.getTo()
            + ") {\n    "
            + ParserHelper.listToString(this.statements)
            + "\n    }";
    return ret;
  }
}
