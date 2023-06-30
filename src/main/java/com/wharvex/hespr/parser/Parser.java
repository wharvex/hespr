package com.wharvex.hespr.parser;

import com.wharvex.hespr.ExcType;
import com.wharvex.hespr.SyntaxErrorException;
import com.wharvex.hespr.lexer.Token;
import com.wharvex.hespr.lexer.TokenType;
import com.wharvex.hespr.parser.builtins.*;
import com.wharvex.hespr.parser.nodes.*;
import java.util.ArrayList;
import java.util.Arrays;
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

  private void expectsEndOfLine() throws Exception {
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
        Node expressionNode = this.parseExpression();
        this.optionalMatchAndRemoveTokenSafe(TokenType.RPAREN)
            .orElseThrow(() -> new SyntaxErrorException(
                TokenType.RPAREN, this.getCurToken()));
        return expressionNode;
      }
      case TRUE -> {
        return new BooleanNode(true, factorToken.getTokenLineNum());
      }
      case FALSE -> {
        return new BooleanNode(false, factorToken.getTokenLineNum());
      }
      case STRINGLITERAL -> {
        return new StringNode(factorToken.getValueString(), factorToken.getTokenLineNum());
      }
      case CHARACTERLITERAL -> {
        return new CharacterNode(factorToken.getValueString().charAt(0),
            factorToken.getTokenLineNum());
      }
      default -> throw new SyntaxErrorException(ParserHelper.factorTypes, this.getCurToken());
    }
  }

  private FunctionNode parseFunc() throws Exception {
    Token defineToken = this.optionalMatchAndRemoveTokenSafe(TokenType.BLOK)
        .orElseThrow(() -> new SyntaxErrorException(
            TokenType.BLOK, this.getCurToken()));
    Token funcName = this.optionalMatchAndRemoveTokenSafe(TokenType.IDENTIFIER).orElseThrow(
        () -> new SyntaxErrorException(TokenType.IDENTIFIER, this.getCurToken()));
    this.optionalMatchAndRemoveTokenSafe(TokenType.LPAREN).orElseThrow(
        () -> new SyntaxErrorException(TokenType.LPAREN, this.getCurToken()));
    if (this.findBeforeNextEOL(TokenType.RPAREN) < 0) {
      throw new SyntaxErrorException(TokenType.RPAREN, this.getCurToken());
    }
    List<VariableNode> paramVars = this.parseParameterDeclarations();
    this.expectsEndOfLine();

    // LOCAL DECLARATIONS

    List<VariableNode> variableVars = new ArrayList<>();
    List<VariableNode> constVars = new ArrayList<>();
    Optional<Token> optionalVariablesOrConstantsToken = this.optionalMatchAndRemoveTokenSafe(
        List.of(TokenType.VARIABLES, TokenType.CONSTANTS));
    if (optionalVariablesOrConstantsToken.isEmpty() && !this.curTokenTypeIs(TokenType.INDENT)) {
      throw new SyntaxErrorException(ParserHelper.varConstIndent, this.getCurToken());
    }
    while (!this.curTokenTypeIs(TokenType.INDENT)) {
      if (this.prevTokenTypeIs(TokenType.VARIABLES)) {
        this.parseVariables(variableVars);
        this.expectsEndOfLine();
      } else {
        constVars = this.parseConstants();
      }
      optionalVariablesOrConstantsToken = this.optionalMatchAndRemoveTokenSafe(
          List.of(TokenType.VARIABLES, TokenType.CONSTANTS));
      if (optionalVariablesOrConstantsToken.isEmpty() && !this.curTokenTypeIs(TokenType.INDENT)) {
        throw new SyntaxErrorException(ParserHelper.varConstIndent, this.getCurToken());
      }
    }
    List<StatementNode> statements = this.parseStatements()
        .orElseThrow(() -> new SyntaxErrorException(defineToken));
    return new FunctionNode(
        funcName.getValueString(), paramVars, variableVars, constVars, statements,
        defineToken.getTokenLineNum());
  }

  private void parseVariables(List<VariableNode> vars) throws Exception {
    List<Token> varNames = this.parseVariableNames();
    boolean isArray = this.optionalMatchAndRemoveTokenSafe(ParserHelper.dataTypesIncArr)
        .map(dataType -> dataType.getTokenType() == TokenType.ARRAY)
        .orElseThrow(
            () -> new SyntaxErrorException(ParserHelper.dataTypesIncArr, this.getCurToken()));
    TokenType varTypeTT = this.getPrevTokenType();
    VariableRange range = this.parseVarRange(varTypeTT);
    VariableType varType;
    if (isArray) {
      this.optionalMatchAndRemoveTokenSafe(TokenType.OF)
          .orElseThrow(() -> new SyntaxErrorException(TokenType.OF, this.getCurToken()));
      varType = this.optionalMatchAndRemoveTokenSafe(ParserHelper.dataTypes)
          .map(token -> ParserHelper.getVarTypeFromTokenType(token.getTokenType()))
          .orElseThrow(() -> new SyntaxErrorException(ParserHelper.dataTypes, this.getCurToken()));
    } else {
      varType = ParserHelper.getVarTypeFromTokenType(varTypeTT);
    }
    for (Token vn : varNames) {
      vars.add(
          new VariableNode(
              vn.getValueString(), varType, true, isArray, this.getCurTokenLineNum(), range));
    }
  }

  private List<VariableNode> parseConstants() throws SyntaxErrorException {
    var constDecs = new ArrayList<ConstantDeclaration>();
    VariableNode constVar;
    var constVars = new ArrayList<VariableNode>();
    do {
      constDecs.add(this.parseConstant());
      this.optionalMatchAndRemoveTokenSafe(
              List.of(TokenType.COMMA, TokenType.ENDOFLINE))
          .orElseThrow(() -> new SyntaxErrorException(List.of(
              TokenType.COMMA, TokenType.ENDOFLINE), this.getCurToken()));
    } while (this.prevTokenTypeIs(TokenType.COMMA));
    this.eatEOLs();

    for (ConstantDeclaration cd : constDecs) {
      constVar = new VariableNode(cd.getName(), cd.getType(), false, false,
          this.getCurTokenLineNum());
      constVar.setVal(cd.getVal());
      constVars.add(constVar);
    }
    return constVars;
  }

  private ConstantDeclaration parseConstant() throws SyntaxErrorException {
    Token name = this.optionalMatchAndRemoveTokenSafe(TokenType.IDENTIFIER)
        .orElseThrow(() -> new SyntaxErrorException(TokenType.IDENTIFIER, this.getCurToken()));

    this.optionalMatchAndRemoveTokenSafe(TokenType.EQUALS)
        .orElseThrow(() -> new SyntaxErrorException(
            TokenType.EQUALS, this.getCurToken()));
    VariableType varType;
    String minusIfPresent = "";
    TokenType literalTokenType = this.optionalMatchAndRemoveTokenSafe(
            ParserHelper.literalTypesIncMinus)
        .map(Token::getTokenType)
        .orElseThrow(
            () -> new SyntaxErrorException(ParserHelper.literalTypesIncMinus, this.getCurToken()));
    if (literalTokenType == TokenType.MINUS) {
      literalTokenType = this.optionalMatchAndRemoveTokenSafe(ParserHelper.numberTypes)
          .map(Token::getTokenType)
          .orElseThrow(
              () -> new SyntaxErrorException(ParserHelper.numberTypes, this.getCurToken()));
    }
    varType = ParserHelper.getVarTypeFromTokenType(literalTokenType);

    return (new ConstantDeclaration(
        name.getValueString(),
        varType,
        minusIfPresent + this.getPrevToken().getValueString()));
  }

  private List<Token> parseVariableNames() throws SyntaxErrorException {
    List<Token> varNameTokens = new ArrayList<>();
    do {
      varNameTokens.add(this.optionalMatchAndRemoveTokenSafe(TokenType.IDENTIFIER)
          .orElseThrow(() -> new SyntaxErrorException(TokenType.IDENTIFIER, this.getCurToken())));
      this.optionalMatchAndRemoveTokenSafe(Arrays.asList(TokenType.COMMA, TokenType.COLON))
          .orElseThrow(
              () -> new SyntaxErrorException(Arrays.asList(TokenType.COMMA, TokenType.COLON),
                  this.getCurToken()));
    }
    while (!this.prevTokenTypeIs(TokenType.COLON));
    return varNameTokens;
  }


  private VariableRange parseVarRange(TokenType varType) throws SyntaxErrorException {
    Optional<Token> optionalFrom = this.optionalMatchAndRemoveTokenSafe(TokenType.FROM);
    if (varType == TokenType.ARRAY) {
      optionalFrom.orElseThrow(() -> new SyntaxErrorException(TokenType.FROM, this.getCurToken()));
    }
    if (optionalFrom.isEmpty()) {
      return new VariableRange();
    }

    Token fromNumToken = this.optionalMatchAndRemoveTokenSafe(
            Arrays.asList(TokenType.NUMBER, TokenType.NUMBER_DECIMAL))
        .orElseThrow(() -> new SyntaxErrorException(Arrays.asList(
            TokenType.NUMBER_DECIMAL, TokenType.NUMBER), this.getCurToken()));

    this.optionalMatchAndRemoveTokenSafe(TokenType.TO).orElseThrow(() -> new SyntaxErrorException(
        TokenType.TO, this.getCurToken()));

    Token toNumToken = this.optionalMatchAndRemoveTokenSafe(
            Arrays.asList(TokenType.NUMBER, TokenType.NUMBER_DECIMAL))
        .orElseThrow(() -> new SyntaxErrorException(Arrays.asList(
            TokenType.NUMBER_DECIMAL, TokenType.NUMBER), this.getCurToken()));

    return new VariableRange(fromNumToken, toNumToken);
  }

  private List<VariableNode> parseParameterDeclarations() throws SyntaxErrorException {
    List<VariableNode> ret = new ArrayList<>();
    boolean changeable, isArray;
    isArray = false;
    List<Token> varNames;
    TokenType varType;
    this.optionalMatchAndRemoveTokenSafe(TokenType.RPAREN);
    while (!this.prevTokenTypeIs(TokenType.RPAREN)) {
      changeable = this.optionalMatchAndRemoveTokenSafe(TokenType.VAR).isPresent();
      varNames = this.parseVariableNames();
      isArray = this.optionalMatchAndRemoveTokenSafe(ParserHelper.dataTypesIncArr)
          .map(dataType -> dataType.getTokenType() == TokenType.ARRAY)
          .orElseThrow(
              () -> new SyntaxErrorException(ParserHelper.dataTypesIncArr, this.getCurToken()));
      if (isArray) {
        this.optionalMatchAndRemoveTokenSafe(TokenType.OF)
            .orElseThrow(() -> new SyntaxErrorException(TokenType.OF, this.getCurToken()));
        this.optionalMatchAndRemoveTokenSafe(ParserHelper.dataTypes)
            .orElseThrow(
                () -> new SyntaxErrorException(ParserHelper.dataTypes, this.getCurToken()));
      }
      varType = this.getPrevTokenType();
      this.optionalMatchAndRemoveTokenSafe(
          Arrays.asList(TokenType.SEMICOLON, TokenType.RPAREN)).orElseThrow(
          () -> new SyntaxErrorException(Arrays.asList(TokenType.SEMICOLON, TokenType.RPAREN),
              this.getCurToken()));
      for (Token vn : varNames) {
        ret.add(
            new VariableNode(
                vn.getValueString(),
                ParserHelper.getVarTypeFromTokenType(varType),
                changeable,
                isArray, this.getCurTokenLineNum(), new VariableRange()));
      }
    }
    return ret;
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
      case FOR -> {
        return Optional.of(this.parseFor());
      }
      case WHILE -> {
        return Optional.of(this.parseWhile());
      }
      case REPEAT -> {
        return Optional.of(this.parseRepeat());
      }
      case IF -> {
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

  private ForNode parseFor() throws Exception {
    Token forToken = new Token(this.getPrevToken());
    this.optionalMatchAndRemoveTokenSafe(TokenType.IDENTIFIER)
        .orElseThrow(() -> new SyntaxErrorException(
            TokenType.IDENTIFIER, this.getCurToken()));
    // TODO: Ensure in semantic analysis that this varRef has no array subscript
    VariableReferenceNode varRef = this.parseVariableRef();
    this.optionalMatchAndRemoveTokenSafe(TokenType.FROM).orElseThrow(() -> new SyntaxErrorException(
        TokenType.FROM, this.getCurToken()));
    Node fromExp = this.parseExpression();
    this.optionalMatchAndRemoveTokenSafe(TokenType.TO).orElseThrow(() -> new SyntaxErrorException(
        TokenType.TO, this.getCurToken()));
    Node toExp = this.parseExpression();
    this.expectsEndOfLine();
    return new ForNode(varRef, fromExp, toExp,
        this.parseStatements().orElseThrow(() -> new SyntaxErrorException(forToken)),
        this.getCurTokenLineNum());
  }

  private IfNode parseIf() throws Exception {
    Token ifOrElsifOrElse = this.getPrevToken();
    List<StatementNode> statements;
    if (ifOrElsifOrElse.getTokenType() != TokenType.ELSE) {
      Node condition = this.parseBoolCompare();
      this.optionalMatchAndRemoveTokenSafe(TokenType.THEN)
          .orElseThrow(() -> new SyntaxErrorException(
              TokenType.THEN, this.getCurToken()));
      this.expectsEndOfLine();
      statements = this.parseStatements()
          .orElseThrow(() -> new SyntaxErrorException(ifOrElsifOrElse));
      if (optionalMatchAndRemoveTokenSafe(List.of(TokenType.ELSIF, TokenType.ELSE)).isPresent()) {
        return new IfNode(condition, statements, this.parseIf(), ifOrElsifOrElse.getTokenType(),
            ifOrElsifOrElse.getTokenLineNum());
      } else {
        return new IfNode(condition, statements, ifOrElsifOrElse.getTokenType(),
            ifOrElsifOrElse.getTokenLineNum());
      }
    } else {
      this.expectsEndOfLine();
      statements = this.parseStatements()
          .orElseThrow(() -> new SyntaxErrorException(ifOrElsifOrElse));
      return new IfNode(statements, ifOrElsifOrElse.getTokenType());
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
      return new ArgumentNode(this.parseBoolCompare(), false, this.getCurTokenLineNum());
    }
  }

  private FunctionCallNode parseFunctionCall() throws Exception {
    Token funcName = this.getPrevToken();
    if (this.curTokenTypeIs(TokenType.ENDOFLINE)) {
      this.expectsEndOfLine();
      return new FunctionCallNode(funcName.getValueString(), funcName.getTokenLineNum());
    }
    var args = new ArrayList<ArgumentNode>();
    do {
      args.add(this.parseArg());
    }
    while (this.optionalMatchAndRemoveTokenSafe(TokenType.COMMA).isPresent() || this.curTokenTypeIs(
        TokenType.VAR));
    this.expectsEndOfLine();
    return new FunctionCallNode(funcName.getValueString(), args, this.getCurTokenLineNum());
  }
}
