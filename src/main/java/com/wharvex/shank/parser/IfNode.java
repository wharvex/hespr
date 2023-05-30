package com.wharvex.shank.parser;

import com.wharvex.shank.lexer.Token;
import java.util.List;

public class IfNode extends StatementNode {

  Node condition;
  private List<StatementNode> statements;
  IfNode nextIf;
  Token.TokenType ifOrElsifOrElse;

  public IfNode(Node condition, List<StatementNode> statements, Token.TokenType ifOrElsifOrElse) {
    this.condition = condition;
    this.statements = statements;
    this.ifOrElsifOrElse = ifOrElsifOrElse;
  }

  public IfNode(
      Node condition,
      List<StatementNode> statements,
      IfNode nextIf,
      Token.TokenType ifOrElsifOrElse) {
    this.condition = condition;
    this.statements = statements;
    this.nextIf = nextIf;
    this.ifOrElsifOrElse = ifOrElsifOrElse;
  }

  public IfNode(List<StatementNode> statements, Token.TokenType ifOrElsifOrElse) {
    this.statements = statements;
    this.ifOrElsifOrElse = ifOrElsifOrElse;
  }

  public IfNode getNextIf() {
    return this.nextIf;
  }

  public Node getCondition() {
    return this.condition;
  }

  public Token.TokenType getIfOrElsifOrElse() {
    return this.ifOrElsifOrElse;
  }

  public List<StatementNode> getStatements() {
    return this.statements;
  }

  public String toString() {
    String head = this.getIfOrElsifOrElse().toString().toLowerCase();
    String condition =
        this.getIfOrElsifOrElse() == Token.TokenType.ELSE ? "" : this.getCondition().toString();
    String ret =
        head + " " + condition + " {\n    " + Parser.listToString(this.statements) + "\n    } ";
    return ret + (this.getNextIf() == null ? "" : this.getNextIf().toString());
  }
}
