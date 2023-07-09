package com.wharvex.hespr.parser;

import com.wharvex.hespr.ExcType;
import com.wharvex.hespr.SyntaxErrorException;
import com.wharvex.hespr.lexer.Token;
import com.wharvex.hespr.lexer.TokenType;
import com.wharvex.hespr.parser.builtins.*;
import com.wharvex.hespr.parser.nodes.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Parser {

  private final List<Token> tokens;
  private final CurrentToken curToken;

  public Parser(List<Token> tokens) throws SyntaxErrorException {
    this.tokens = tokens;
    this.curToken = new CurrentToken(
        this.optionalPeekToken(0).orElseThrow(() -> new SyntaxErrorException(
            ExcType.EOF_ERROR, 1, "")),
        this.optionalPeekToken(1).orElseThrow(() -> new SyntaxErrorException(
            ExcType.EOF_ERROR, 1, "")),
        this.optionalPeekToken(2).orElseThrow(() -> new SyntaxErrorException(
            ExcType.EOF_ERROR, 1, "")));
  }

  // TOKENS METHODS NOT USED IN PARSERS

  private Optional<Token> optionalPeekToken(int idx) throws SyntaxErrorException {
    if (idx >= this.tokens.size()) {
      return Optional.empty();
    }
    if (this.tokens.get(idx) == null) {
      throw new SyntaxErrorException(ExcType.INTERNAL_ERROR,
          "Found a null entry in tokens at index " + idx);
    }
    return Optional.of(this.tokens.get(idx));
  }

  public boolean tokensNotEmpty() {
    return this.tokens.size() > 0;
  }

  private void setCurToken(Token token) {
    this.curToken.setNxtNxtToken(token);
  }

  private void updateCurToken() throws SyntaxErrorException {
    this.setCurToken(this.optionalPeekToken(2).orElse(new Token("", TokenType.ENDOFFILE, -1)));
  }

  private Token removeToken() throws SyntaxErrorException {
    if (this.tokensNotEmpty()) {
      Token ret = this.tokens.remove(0);
      if (ret == null) {
        throw new SyntaxErrorException(ExcType.INTERNAL_ERROR,
            "Found a null entry in tokens at index 0");
      }
      this.updateCurToken();
      return ret;
    } else {
      throw new SyntaxErrorException(ExcType.EOF_ERROR, "");
    }
  }

  private TokenType getCurTokenType() {
    return this.curToken.getCurTokenType();
  }

  // TOKENS METHODS USED IN PARSERS

  private Token getCurToken() {
    return this.curToken.getCurToken();
  }

  private boolean prevTokenTypeIs(TokenType tokenType) {
    return this.curToken.getPrevTokenType() == tokenType;
  }

  private TokenType getPrevTokenType() {
    return this.curToken.getPrevTokenType();
  }

  private Token getPrevToken() {
    return this.curToken.getPrevToken();
  }

  private int findBeforeNextEOL(TokenType findMe) throws SyntaxErrorException {
    int i = 0;
    Optional<Token> nextToken = this.optionalPeekToken(i);
    while (nextToken.isPresent() && nextToken.get().getTokenType() != TokenType.ENDOFLINE) {
      if (nextToken.get().getTokenType() == findMe) {
        return i;
      }
      nextToken = this.optionalPeekToken(++i);
    }
    return -1;
  }

  private Optional<TokenType> findBeforeNextEOL(List<TokenType> findMe, int startAt)
      throws SyntaxErrorException {
    Optional<Token> nextToken = this.optionalPeekToken(startAt);
    while (nextToken.isPresent() && nextToken.get().getTokenType() != TokenType.ENDOFLINE) {
      if (findMe.contains(nextToken.get().getTokenType())) {
        return Optional.of(nextToken.get().getTokenType());
      }
      nextToken = this.optionalPeekToken(++startAt);
    }
    return Optional.empty();
  }

  private Optional<Token> optionalMatchAndRemoveTokenSafe(TokenType tokenType)
      throws SyntaxErrorException {
    return this.curTokenTypeIs(tokenType) ? Optional.of(this.removeToken()) : Optional.empty();
  }

  private Optional<Token> optionalMatchAndRemoveTokenSafe(List<TokenType> tokenTypes)
      throws SyntaxErrorException {
    return tokenTypes.contains(this.getCurTokenType()) ? Optional.of(this.removeToken())
        : Optional.empty();
  }

  private void expectsEndOfLine() throws SyntaxErrorException {
    this.optionalMatchAndRemoveTokenSafe(TokenType.ENDOFLINE)
        .orElseThrow(() -> new SyntaxErrorException(TokenType.ENDOFLINE, this.getCurToken()));
    this.eatEOLs();
  }

  private void eatEOLs() throws SyntaxErrorException {
    while (this.curTokenTypeIs(TokenType.ENDOFLINE)) {
      this.optionalMatchAndRemoveTokenSafe(TokenType.ENDOFLINE);
    }
  }

  private boolean curTokenTypeIs(TokenType tokenType) {
    return this.getCurTokenType() == tokenType;
  }

  private boolean curTokenTypeIs(List<TokenType> tokenTypes) {
    return tokenTypes.contains(this.getCurTokenType());
  }

  private int getCurTokenLineNum() {
    return this.curToken.getLineNum();
  }

  // PARSERS

  public ProgramNode parse() throws Exception {
    ProgramNode program = new ProgramNode();
    if (this.curTokenTypeIs(TokenType.ENDOFLINE)) {
      this.expectsEndOfLine();
    }
    while (!this.curTokenTypeIs(TokenType.ENDOFFILE)) {
      program.addFunction(this.parseFunc());
    }
    program.addFunction(new BuiltinEnd());
    program.addFunction(new BuiltinGetRandom());
    program.addFunction(new BuiltinIntegerToReal());
    program.addFunction(new BuiltinLeft());
    program.addFunction(new BuiltinRead());
    program.addFunction(new BuiltinRealToInteger());
    program.addFunction(new BuiltinRight());
    program.addFunction(new BuiltinSquareRoot());
    program.addFunction(new BuiltinStart());
    program.addFunction(new BuiltinSubstring());
    program.addFunction(new BuiltinWrite());
    System.out.println(program);
    return program;
  }

  private Node parseExpression() throws SyntaxErrorException {
    Node term1 = this.parseTerm();
    Optional<Token> optionalExpOp = this.optionalMatchAndRemoveTokenSafe(List.of(TokenType.PLUS,
        TokenType.MINUS));
    if (optionalExpOp.isEmpty() && this.curTokenTypeIs(
        Stream.concat(ParserHelper.compareTypes.stream(), ParserHelper.expOrTermEndTypes.stream())
            .toList())) {
      return term1;
    }
    // todo: pass new SyntaxErrorException to orElseThrow
    TokenType expOpType = optionalExpOp.map(Token::getTokenType).orElseThrow();
    MathOpNode.MathOpType mathOpType = switch (expOpType) {
      case PLUS -> MathOpNode.MathOpType.ADD;
      case MINUS -> MathOpNode.MathOpType.SUBTRACT;
      default -> throw new SyntaxErrorException(ExcType.INTERNAL_ERROR, "");
    };
    return new MathOpNode(mathOpType, term1, this.parseExpression(), this.getCurTokenLineNum());
  }

  private Node parseTerm() throws SyntaxErrorException {
    Node factor1 = this.parseFactor();
    Optional<Token> optionalTermOp = this.optionalMatchAndRemoveTokenSafe(ParserHelper.termOpTypes);
    if (optionalTermOp.isEmpty() && this.curTokenTypeIs(
        Stream.of(ParserHelper.expOpTypes, ParserHelper.compareTypes,
                ParserHelper.expOrTermEndTypes).flatMap(Collection::stream)
            .collect(Collectors.toList()))) {
      return factor1;
    }
    // todo: pass new SyntaxErrorException to orElseThrow
    TokenType termOpType = optionalTermOp.map(Token::getTokenType)
        .orElseThrow(() -> new SyntaxErrorException(this.getCurToken()));
    MathOpNode.MathOpType mathOpType = switch (termOpType) {
      case TIMES -> MathOpNode.MathOpType.MULTIPLY;
      case MOD -> MathOpNode.MathOpType.MOD;
      case DIVIDE -> MathOpNode.MathOpType.DIVIDE;
      default -> throw new SyntaxErrorException(ExcType.INTERNAL_ERROR, "");
    };

    return new MathOpNode(mathOpType, factor1,
        this.findBeforeNextEOL(List.of(TokenType.PLUS, TokenType.MINUS), 1).isPresent()
            ? this.parseFactor() : this.parseExpression(), this.getCurTokenLineNum());
  }

  private Node parseFactor() throws SyntaxErrorException {
    Token factorToken = this.optionalMatchAndRemoveTokenSafe(ParserHelper.factorTypes)
        .orElse(new Token("", TokenType.NONE, -1));
    switch (factorToken.getTokenType()) {
      case MINUS -> {
        return this.optionalMatchAndRemoveTokenSafe(List.of(TokenType.NUMBER,
                TokenType.NUMBER_DECIMAL))
            .map(ft -> ft.getTokenType() == TokenType.NUMBER ? new IntegerNode(
                Integer.parseInt("-" + ft.getValueString()), ft.getTokenLineNum())
                : new RealNode(Float.parseFloat("-" + ft.getValueString()), ft.getTokenLineNum()))
            .orElseThrow(() -> new SyntaxErrorException(List.of(
                TokenType.NUMBER, TokenType.NUMBER_DECIMAL), this.getCurToken()));
      }
      case NUMBER -> {
        return new IntegerNode(Integer.parseInt(factorToken.getValueString()),
            factorToken.getTokenLineNum());
      }
      case NUMBER_DECIMAL -> {
        return new RealNode(Float.parseFloat(factorToken.getValueString()),
            factorToken.getTokenLineNum());
      }
      case IDENTIFIER -> {
        return this.parseVariableRef();
      }
      case LPAREN -> {
        Node parenContents = this.parseExpression();
        this.optionalMatchAndRemoveTokenSafe(TokenType.RPAREN)
            .orElseThrow(() -> new SyntaxErrorException(
                TokenType.RPAREN, this.getCurToken()));
        return parenContents;
      }
      case TRUE -> {
        return new BooleanNode(true, factorToken.getTokenLineNum());
      }
      case FALS -> {
        return new BooleanNode(false, factorToken.getTokenLineNum());
      }
      case STRINGLITERAL -> {
        return new StringNode(factorToken.getValueString(), factorToken.getTokenLineNum());
      }
      case CHARACTERLITERAL -> {
        return new CharacterNode(factorToken.getValueString().charAt(1),
            factorToken.getTokenLineNum());
      }
      default -> throw new SyntaxErrorException(ParserHelper.factorTypes, this.getCurToken());
    }
  }

  private FunctionNode parseFunc() throws Exception {
    Token blokToken = this.optionalMatchAndRemoveTokenSafe(TokenType.BLOK)
        .orElseThrow(() -> new SyntaxErrorException(TokenType.BLOK, this.getCurToken()));
    Token funcName = this.optionalMatchAndRemoveTokenSafe(TokenType.IDENTIFIER).orElseThrow(
        () -> new SyntaxErrorException(TokenType.IDENTIFIER, this.getCurToken()));

    // PARAMETER DECLARATIONS

    List<VariableNode> paramVars = new ArrayList<>();
    if (this.optionalMatchAndRemoveTokenSafe(TokenType.TILDE).isEmpty()) {
      this.optionalMatchAndRemoveTokenSafe(TokenType.PIPE).orElseThrow(
          () -> new SyntaxErrorException(TokenType.PIPE, this.getCurToken()));
      if (this.findBeforeNextEOL(TokenType.PIPE) < 0) {
        throw new SyntaxErrorException(TokenType.PIPE, this.getCurToken());
      }
      this.parseParamDeclarations(paramVars);
    }
    this.expectsEndOfLine();

    // LOCAL VARIABLE/CONSTANT DECLARATIONS

    List<VariableNode> variableVars = new ArrayList<>();
    List<VariableNode> constVars = new ArrayList<>();
    Optional<Token> optionalVariablesOrConstantsToken = this.optionalMatchAndRemoveTokenSafe(
        List.of(TokenType.FLUX, TokenType.PERM));
    if (optionalVariablesOrConstantsToken.isEmpty() && !this.curTokenTypeIs(TokenType.INDENT)) {
      throw new SyntaxErrorException(ParserHelper.varConstIndent, this.getCurToken());
    }
    while (!this.curTokenTypeIs(TokenType.INDENT)) {
      if (this.prevTokenTypeIs(TokenType.FLUX)) {
        this.parseVariables(variableVars);
        this.expectsEndOfLine();
      } else {
        constVars = this.parseConstants();
      }
      optionalVariablesOrConstantsToken = this.optionalMatchAndRemoveTokenSafe(
          List.of(TokenType.FLUX, TokenType.PERM));
      if (optionalVariablesOrConstantsToken.isEmpty() && !this.curTokenTypeIs(TokenType.INDENT)) {
        throw new SyntaxErrorException(ParserHelper.varConstIndent, this.getCurToken());
      }
    }

    // GET STATEMENTS AND RETURN

    List<StatementNode> statements = this.parseStatements()
        .orElseThrow(() -> new SyntaxErrorException(blokToken));
    return new FunctionNode(
        funcName.getValueString(), paramVars, variableVars, constVars, statements,
        blokToken.getTokenLineNum());
  }

  private void parseVariables(List<VariableNode> vars) throws Exception {
    List<VariableNode> varNames = this.parseLocalVarNames();
    this.parseVarsTypeAndRange(varNames);
    vars.addAll(varNames);
  }

  private void parseVarsTypeAndRange(List<VariableNode> variables) throws SyntaxErrorException {
    TokenType dataTypeTokenType = this.optionalMatchAndRemoveTokenSafe(ParserHelper.dataTypes)
        .map(Token::getTokenType)
        .orElseThrow(() -> new SyntaxErrorException(ParserHelper.dataTypes, this.getCurToken()));
    boolean isArray = this.optionalMatchAndRemoveTokenSafe(TokenType.ARR).isPresent();
    VariableRange range =
        this.curTokenTypeIs(ParserHelper.factorTypes) ? this.parseRange() : new VariableRange();
    for (VariableNode variableNode : variables) {
      variableNode.setType(ParserHelper.getVarTypeFromTokenType(dataTypeTokenType));
      variableNode.setIsArray(isArray);
      variableNode.setRange(range);
    }
  }

  private List<VariableNode> parseConstants() throws SyntaxErrorException {
    var constDecs = new ArrayList<ConstantDeclaration>();
    VariableNode constVar;
    var constVars = new ArrayList<VariableNode>();
    do {
      constDecs.add(this.parseConstant());
      // todo: this could be an endless loop if the file ends with the current line
      this.optionalMatchAndRemoveTokenSafe(
              List.of(TokenType.SEMICOLON, TokenType.ENDOFLINE))
          .orElseThrow(() -> new SyntaxErrorException(List.of(
              TokenType.SEMICOLON, TokenType.ENDOFLINE), this.getCurToken()));
    } while (this.prevTokenTypeIs(TokenType.SEMICOLON));
    this.eatEOLs();

    for (ConstantDeclaration cd : constDecs) {
      constVar = new VariableNode(cd.getName(), VariableType.ANY, false, false,
          this.getCurTokenLineNum());
      constVar.setVal(cd.getVal());
      constVars.add(constVar);
    }
    return constVars;
  }

  private ConstantDeclaration parseConstant() throws SyntaxErrorException {
    Token name = this.optionalMatchAndRemoveTokenSafe(TokenType.IDENTIFIER)
        .orElseThrow(() -> new SyntaxErrorException(TokenType.IDENTIFIER, this.getCurToken()));

    return (new ConstantDeclaration(name.getValueString(), this.parseFactor()));
  }

  private void parseParamVarNames(List<VariableNode> varNodes) throws SyntaxErrorException {
    do {
      boolean isVar = this.optionalMatchAndRemoveTokenSafe(TokenType.VAR).isPresent();
      varNodes.add(this.optionalMatchAndRemoveTokenSafe(TokenType.IDENTIFIER)
          .map(idToken -> new VariableNode(idToken.getValueString(), isVar,
              idToken.getTokenLineNum()))
          .orElseThrow(() -> new SyntaxErrorException(TokenType.IDENTIFIER, this.getCurToken())));
    } while (this.optionalMatchAndRemoveTokenSafe(TokenType.COMMA).isEmpty());
  }

  private List<VariableNode> parseLocalVarNames() throws SyntaxErrorException {
    List<VariableNode> varNodes = new ArrayList<>();
    do {
      varNodes.add(this.optionalMatchAndRemoveTokenSafe(TokenType.IDENTIFIER)
          .map(idToken -> new VariableNode(idToken.getValueString(), false,
              idToken.getTokenLineNum()))
          .orElseThrow(() -> new SyntaxErrorException(TokenType.IDENTIFIER, this.getCurToken())));
    }
    while (this.optionalMatchAndRemoveTokenSafe(TokenType.COMMA).isEmpty());
    return varNodes;
  }

  private VariableRange parseRange() throws SyntaxErrorException {
    Node from = this.parseFactor();
    this.optionalMatchAndRemoveTokenSafe(TokenType.ARROW)
        .orElseThrow(() -> new SyntaxErrorException(
            TokenType.ARROW, this.getCurToken()));
    return new VariableRange(from, this.parseFactor());
  }

  private void parseParamDeclarations(List<VariableNode> paramVars) throws SyntaxErrorException {
    while (this.optionalMatchAndRemoveTokenSafe(TokenType.PIPE).isEmpty()) {
      do {
        this.parseParamVarNames(paramVars);
        this.parseVarsTypeAndRange(paramVars);
      } while (this.optionalMatchAndRemoveTokenSafe(TokenType.SEMICOLON).isEmpty()
          && !this.curTokenTypeIs(
          TokenType.PIPE));
    }
  }

  private Node parseBoolCompare() throws SyntaxErrorException {
    Node expression1 = this.parseExpression();
    Optional<Token> optionalCompareToken = this.optionalMatchAndRemoveTokenSafe(
        ParserHelper.compareTypes);
    if (optionalCompareToken.isEmpty()) {
      return expression1;
    }
    CompareType compareType = ParserHelper.getCompTypeFromTokenType(
        optionalCompareToken.map(Token::getTokenType).orElse(TokenType.NONE));
    return new BooleanCompareNode(compareType, expression1, this.parseExpression(),
        this.getCurTokenLineNum());
  }

  private VariableReferenceNode parseVariableRef() throws SyntaxErrorException {
    String name = this.getPrevToken().getValueString();
    this.optionalMatchAndRemoveTokenSafe(TokenType.LSQUARE);
    if (this.prevTokenTypeIs(TokenType.LSQUARE)) {
      VariableReferenceNode ret = new VariableReferenceNode(name, this.parseExpression(),
          this.getCurTokenLineNum());
      this.optionalMatchAndRemoveTokenSafe(TokenType.RSQUARE)
          .orElseThrow(() -> new SyntaxErrorException(
              TokenType.RSQUARE, this.getCurToken()));
      return ret;
    }
    return new VariableReferenceNode(name, this.getCurTokenLineNum());
  }

  private AssignmentNode parseAssignment() throws Exception {
    VariableReferenceNode leftSide = this.parseVariableRef();
    this.optionalMatchAndRemoveTokenSafe(TokenType.ASSIGN);
    Node rightSide = this.parseBoolCompare();
    this.expectsEndOfLine();
    return new AssignmentNode(leftSide, rightSide, this.getCurTokenLineNum());
  }

  private Optional<StatementNode> parseStatement() throws Exception {
    this.optionalMatchAndRemoveTokenSafe(ParserHelper.statementInitTypes);
    switch (this.getPrevTokenType()) {
      case WITH -> {
        return Optional.of(this.parseWith());
      }
      case WHIL -> {
        return Optional.of(this.parseWhile());
      }
      case TILL -> {
        return Optional.of(this.parseRepeat());
      }
      case WHEN -> {
        return Optional.of(this.parseIf());
      }
      case IDENTIFIER -> {
        return this.findBeforeNextEOL(TokenType.ASSIGN) >= 0 ? Optional.of(
            this.parseAssignment())
            : Optional.of(this.parseFunctionCall());
      }
      case DEDENT -> {
        return Optional.empty();
      }
      default ->
          throw new SyntaxErrorException(ParserHelper.statementInitTypes, this.getPrevToken());
    }
  }

  private Optional<List<StatementNode>> parseStatements() throws Exception {
    this.optionalMatchAndRemoveTokenSafe(TokenType.INDENT)
        .orElseThrow(() -> new SyntaxErrorException(
            TokenType.INDENT, this.getCurToken()));
    var ret = new ArrayList<StatementNode>();
    Optional<StatementNode> statement = this.parseStatement();
    while (statement.isPresent()) {
      ret.add(statement.get());
      statement = this.parseStatement();
    }
    return ret.size() > 0 ? Optional.of(ret) : Optional.empty();
  }

  private ForNode parseWith() throws Exception {
    Token withToken = new Token(this.getPrevToken());
    this.optionalMatchAndRemoveTokenSafe(TokenType.IDENTIFIER)
        .orElseThrow(() -> new SyntaxErrorException(
            TokenType.IDENTIFIER, this.getCurToken()));
    // TODO: Ensure in semantic analysis that this varRef has no array subscript
    VariableReferenceNode varRef = this.parseVariableRef();
    this.optionalMatchAndRemoveTokenSafe(TokenType.COLON)
        .orElseThrow(() -> new SyntaxErrorException(
            TokenType.COLON, this.getCurToken()));
    VariableRange withRange = this.parseRange();
    this.expectsEndOfLine();
    return new ForNode(varRef, withRange.getFrom(), withRange.getTo(),
        this.parseStatements().orElseThrow(() -> new SyntaxErrorException(withToken)),
        this.getCurTokenLineNum());
  }

  private WhenNode parseIf() throws Exception {
    Token ifOrElsifOrElse = this.getPrevToken();
    List<StatementNode> statements;
    if (ifOrElsifOrElse.getTokenType() != TokenType.ELSE) {
      Node condition = this.parseBoolCompare();
      this.expectsEndOfLine();
      statements = this.parseStatements()
          .orElseThrow(() -> new SyntaxErrorException(ifOrElsifOrElse));
      if (optionalMatchAndRemoveTokenSafe(List.of(TokenType.ELIF, TokenType.ELSE)).isPresent()) {
        return new WhenNode(condition, statements, this.parseIf(), ifOrElsifOrElse.getTokenType(),
            ifOrElsifOrElse.getTokenLineNum());
      } else {
        return new WhenNode(condition, statements, ifOrElsifOrElse.getTokenType(),
            ifOrElsifOrElse.getTokenLineNum());
      }
    } else {
      this.expectsEndOfLine();
      statements = this.parseStatements()
          .orElseThrow(() -> new SyntaxErrorException(ifOrElsifOrElse));
      return new WhenNode(statements, ifOrElsifOrElse.getTokenType());
    }
  }

  private WhileNode parseWhile() throws Exception {
    Token whileToken = this.getPrevToken();
    Node condition = this.parseBoolCompare();
    this.expectsEndOfLine();
    return new WhileNode(condition,
        this.parseStatements().orElseThrow(() -> new SyntaxErrorException(whileToken)),
        whileToken.getTokenLineNum());
  }

  private RepeatNode parseRepeat() throws Exception {
    Token repeatToken = this.getPrevToken();
    this.optionalMatchAndRemoveTokenSafe(TokenType.UNTIL)
        .orElseThrow(() -> new SyntaxErrorException(
            TokenType.UNTIL, this.getCurToken()));
    Node condition = this.parseBoolCompare();
    this.expectsEndOfLine();
    return new RepeatNode(condition,
        this.parseStatements().orElseThrow(() -> new SyntaxErrorException(repeatToken)),
        repeatToken.getTokenLineNum());
  }

  public ArgumentNode parseArg() throws Exception {
    if (this.optionalMatchAndRemoveTokenSafe(TokenType.VAR).isPresent()) {
      this.optionalMatchAndRemoveTokenSafe(TokenType.IDENTIFIER)
          .orElseThrow(() -> new SyntaxErrorException(
              TokenType.IDENTIFIER, this.getCurToken()));
      return new ArgumentNode(this.parseVariableRef(), true, this.getCurTokenLineNum());
    } else {
      return new ArgumentNode(this.parseFactor(), false, this.getCurTokenLineNum());
    }
  }

  private FunctionCallNode parseFunctionCall() throws Exception {
    Token funcName = this.getPrevToken();
    this.optionalMatchAndRemoveTokenSafe(TokenType.BANG).orElseThrow(() -> new SyntaxErrorException(
        TokenType.BANG, this.getCurToken()));
    if (this.curTokenTypeIs(TokenType.ENDOFLINE)) {
      this.expectsEndOfLine();
      return new FunctionCallNode(funcName.getValueString(), funcName.getTokenLineNum());
    }
    var args = new ArrayList<ArgumentNode>();
    do {
      args.add(this.parseArg());
    }
    while (!this.curTokenTypeIs(TokenType.ENDOFLINE));
    this.expectsEndOfLine();
    return new FunctionCallNode(funcName.getValueString(), args, this.getCurTokenLineNum());
  }
}
