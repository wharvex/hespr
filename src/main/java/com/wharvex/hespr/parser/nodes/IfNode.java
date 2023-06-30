package com.wharvex.hespr.parser.nodes;

import com.wharvex.hespr.lexer.TokenType;
import com.wharvex.hespr.parser.ParserHelper;
import java.util.List;

public class IfNode extends StatementNode {

  Node condition;
  private List<StatementNode> statements;
  IfNode nextIf;
  TokenType ifOrElsifOrElse;

  public IfNode(Node condition, List<StatementNode> statements, TokenType ifOrElsifOrElse,
      int lineNum) {
    this.condition = condition;
    this.statements = statements;
    this.ifOrElsifOrElse = ifOrElsifOrElse;
    this.lineNum = lineNum;
  }

  public IfNode(
      Node condition,
      List<StatementNode> statements,
      IfNode nextIf,
      TokenType ifOrElsifOrElse, int lineNum) {
    this.condition = condition;
    this.statements = statements;
    this.nextIf = nextIf;
    this.ifOrElsifOrElse = ifOrElsifOrElse;
    this.lineNum = lineNum;
  }

  public IfNode(List<StatementNode> statements, TokenType ifOrElsifOrElse) {
    this.statements = statements;
    this.ifOrElsifOrElse = ifOrElsifOrElse;
  }

  public IfNode getNextIf() {
    return this.nextIf;
  }

  public Node getCondition() {
    return this.condition;
  }

  public TokenType getIfOrElsifOrElse() {
    return this.ifOrElsifOrElse;
  }

  public List<StatementNode> getStatements() {
    return this.statements;
  }

  public String toString() {
    String head = this.getIfOrElsifOrElse().toString().toLowerCase();
    String condition =
        this.getIfOrElsifOrElse() == TokenType.ELSE ? "" : this.getCondition().toString();
    String ret =
        head + " " + condition + " {\n    " + ParserHelper.listToString(this.statements)
            + "\n    } ";
    return ret + (this.getNextIf() == null ? "" : this.getNextIf().toString());
  }
}
