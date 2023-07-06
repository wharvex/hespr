package com.wharvex.hespr.parser.nodes;

import com.wharvex.hespr.lexer.TokenType;
import com.wharvex.hespr.parser.ParserHelper;
import java.util.List;
import java.util.Optional;

public class WhenNode extends StatementNode {

  private Node condition;
  private final List<StatementNode> statements;
  private WhenNode nextWhen;
  private final TokenType whenOrElifOrElse;

  public WhenNode(Node condition, List<StatementNode> statements, TokenType whenOrElifOrElse,
      int lineNum) {
    this.condition = condition;
    this.statements = statements;
    this.whenOrElifOrElse = whenOrElifOrElse;
    this.lineNum = lineNum;
  }

  public WhenNode(
      Node condition,
      List<StatementNode> statements,
      WhenNode nextWhen,
      TokenType whenOrElifOrElse, int lineNum) {
    this.condition = condition;
    this.statements = statements;
    this.nextWhen = nextWhen;
    this.whenOrElifOrElse = whenOrElifOrElse;
    this.lineNum = lineNum;
  }

  public WhenNode(List<StatementNode> statements, TokenType whenOrElifOrElse) {
    this.statements = statements;
    this.whenOrElifOrElse = whenOrElifOrElse;
  }

  public Optional<WhenNode> getNextWhen() {
    return Optional.ofNullable(this.nextWhen);
  }

  public Node getCondition() {
    return this.condition;
  }

  public TokenType getWhenOrElifOrElse() {
    return this.whenOrElifOrElse;
  }

  public List<StatementNode> getStatements() {
    return this.statements;
  }

  public String toString() {
    String head = this.getWhenOrElifOrElse().toString().toLowerCase();
    String condition =
        this.getWhenOrElifOrElse() == TokenType.ELSE ? "" : this.getCondition().toString();
    String ret =
        head + " " + condition + " {\n    " + ParserHelper.listToString(this.statements)
            + "\n    } ";
    return ret + (this.getNextWhen() == null ? "" : this.getNextWhen().toString());
  }
}
