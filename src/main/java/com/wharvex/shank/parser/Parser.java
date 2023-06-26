package com.wharvex.shank.parser;

import com.wharvex.shank.parser.VariableNode.VariableType;
import java.util.Collection;
import com.wharvex.shank.SyntaxErrorException;
import com.wharvex.shank.lexer.Token;
import com.wharvex.shank.lexer.TokenType;
import com.wharvex.shank.parser.builtins.BuiltinEnd;
import com.wharvex.shank.parser.builtins.BuiltinGetRandom;
import com.wharvex.shank.parser.builtins.BuiltinIntegerToReal;
import com.wharvex.shank.parser.builtins.BuiltinLeft;
import com.wharvex.shank.parser.builtins.BuiltinWrite;
import com.wharvex.shank.parser.builtins.BuiltinRead;
import com.wharvex.shank.parser.builtins.BuiltinRealToInteger;
import com.wharvex.shank.parser.builtins.BuiltinRight;
import com.wharvex.shank.parser.builtins.BuiltinSquareRoot;
import com.wharvex.shank.parser.builtins.BuiltinStart;
import com.wharvex.shank.parser.builtins.BuiltinSubstring;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Parser {

  private final List<Token> tokens;
  private final CurrentToken curToken;

  private static final List<TokenType> dataTypes = Arrays.asList(
      TokenType.REAL,
      TokenType.INTEGER,
      TokenType.BOOLEAN,
      TokenType.STRING,
      TokenType.CHARACTER);
  private static final List<TokenType> dataTypesIncArr = Arrays.asList(
      TokenType.ARRAY,
      TokenType.REAL,
      TokenType.INTEGER,
      TokenType.BOOLEAN,
      TokenType.STRING,
      TokenType.CHARACTER);
  private static final List<TokenType> varConstIndent = Arrays.asList(
      TokenType.VARIABLES,
      TokenType.CONSTANTS,
      TokenType.INDENT);
  private static final List<TokenType> literalTypesIncMinus = Arrays.asList(
      TokenType.NUMBER_DECIMAL,
      TokenType.NUMBER,
      TokenType.STRINGLITERAL,
      TokenType.CHARACTERLITERAL,
      TokenType.TRUE,
      TokenType.FALSE,
      TokenType.MINUS);
  private static final List<TokenType> literalTypes = Arrays.asList(
      TokenType.NUMBER_DECIMAL,
      TokenType.NUMBER,
      TokenType.STRINGLITERAL,
      TokenType.CHARACTERLITERAL,
      TokenType.TRUE,
      TokenType.FALSE);
  private static final List<TokenType> statementInitTypes = List.of(
      TokenType.FOR,
      TokenType.WHILE,
      TokenType.REPEAT,
      TokenType.IF,
      TokenType.IDENTIFIER,
      TokenType.DEDENT);
  private static final List<TokenType> ifTypes = List.of(
      TokenType.IF,
      TokenType.ELSE,
      TokenType.ELSIF);
  private static final List<TokenType> factorTypes = List.of(
      TokenType.MINUS,
      TokenType.NUMBER,
      TokenType.NUMBER_DECIMAL,
      TokenType.IDENTIFIER,
      TokenType.LPAREN,
      TokenType.TRUE,
      TokenType.FALSE,
      TokenType.STRINGLITERAL,
      TokenType.CHARACTERLITERAL);
  private static final List<TokenType> termOpTypes = List.of(
      TokenType.TIMES,
      TokenType.MOD,
      TokenType.DIVIDE);
  private static final List<TokenType> compareTypes = List.of(
      TokenType.LESSEQUAL, TokenType.LESSTHAN, TokenType.GREATEREQUAL, TokenType.GREATERTHAN,
      TokenType.NOTEQUAL, TokenType.EQUALS);
  private static final List<TokenType> expOpTypes = List.of(TokenType.MINUS, TokenType.PLUS);
  private static final List<TokenType> expOrTermEndTypes = List.of(TokenType.RPAREN,
      TokenType.ENDOFLINE, TokenType.THEN, TokenType.COMMA);

  /**
   * Constructor
   *
   * @param tokens the tokens list
   */
  public Parser(List<Token> tokens) throws SyntaxErrorException {
    this.tokens = tokens;
    this.curToken = new CurrentToken(
        this.optionalPeekToken(0).orElseThrow(() -> new SyntaxErrorException(
            SyntaxErrorException.ExcType.EOF_ERROR, 1, "")),
        this.optionalPeekToken(1).orElseThrow(() -> new SyntaxErrorException(
            SyntaxErrorException.ExcType.EOF_ERROR, 1, "")),
        this.optionalPeekToken(2).orElseThrow(() -> new SyntaxErrorException(
            SyntaxErrorException.ExcType.EOF_ERROR, 1, "")));
  }

  /**
   * Check if tokens is not empty
   *
   * @return true if not empty, false otherwise
   */
  public boolean tokensNotEmpty() {
    return this.tokens.size() > 0;
  }

  private Token getCurToken() {
    return this.curToken.getCurToken();
  }

  private void setCurToken(Token token) {
    this.curToken.setNxtNxtToken(token);
  }

  private void updateCurTokenSafe() throws SyntaxErrorException {
    this.setCurToken(this.optionalPeekToken(2).orElseThrow(() -> new SyntaxErrorException(
        SyntaxErrorException.ExcType.EOF_ERROR, -1, "")));
  }

  private void updateCurToken() throws SyntaxErrorException {
    this.setCurToken(this.optionalPeekToken(2).orElse(new Token("", TokenType.ENDOFFILE, -1)));
  }

  /**
   * Find the index position in tokens of the next occurrence of an RPAREN token
   *
   * @return the index position of the next RPAREN token
   */
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

  private String peekToString() {
    Token peekTokenRet = this.peekToken(0);
    return peekTokenRet != null ? peekTokenRet.toString() : "EOF";
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

  private Token removeToken() throws SyntaxErrorException {
    if (this.tokensNotEmpty()) {
      Token ret = this.tokens.remove(0);
      if (ret == null) {
        throw new SyntaxErrorException(SyntaxErrorException.ExcType.INTERNAL_ERROR,
            "Found a null entry in tokens at index 0");
      }
      this.updateCurToken();
      return ret;
    } else {
      throw new SyntaxErrorException(SyntaxErrorException.ExcType.EOF_ERROR, "");
    }
  }

  private Token removeTokenSafe() throws SyntaxErrorException {
    if (this.tokensNotEmpty()) {
      Token ret = this.tokens.remove(0);
      if (ret == null) {
        throw new SyntaxErrorException(SyntaxErrorException.ExcType.INTERNAL_ERROR,
            "Found a null entry in tokens at index 0");
      }
      this.updateCurTokenSafe();
      return ret;
    } else {
      throw new SyntaxErrorException(SyntaxErrorException.ExcType.EOF_ERROR, "");
    }
  }

  /**
   * Checks if the next token in tokens is an ENDOFLINE token and throws an error if it isn't.
   * Removes the token if it is and continues removing ENDOFLINE tokens if they are next in tokens.
   */
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

  private boolean nxtTokenTypeIs(TokenType tokenType) {
    return this.getCurTokenType() == tokenType;
  }

  private boolean nxtTokenTypeIs(List<TokenType> tokenTypes) {
    return tokenTypes.contains(this.getCurTokenType());
  }

  private boolean nxtNxtTokenTypeIs(TokenType tokenType) {
    return this.getCurTokenType() == tokenType;
  }

  private boolean nxtNxtTokenTypeIs(List<TokenType> tokenTypes) {
    return tokenTypes.contains(this.getCurTokenType());
  }

  private boolean curTokenIsEOF() {
    return this.getCurTokenType() == TokenType.ENDOFFILE;
  }

  private TokenType getCurTokenType() {
    return this.curToken.getCurTokenType();
  }

  private int getCurTokenLineNum() {
    return this.curToken.getLineNum();
  }

  /**
   * Returns the TokenType of the Token found at index idx of tokens
   *
   * @param idx the index position of tokens to check
   * @return the TokenType found or null if places is beyond the size of tokens
   */
  private TokenType peekTokenType(int idx) {
    if (this.tokens.size() > idx) {
      return this.tokens.get(idx).getTokenType();
    }
    return null;
  }

  /**
   * Returns the Token found at index places of tokens
   *
   * @param places the index position of tokens to check
   * @return the Token found or null if places is beyond the size of tokens
   */
  private Token peekToken(int places) {
    if (this.tokens.size() > places) {
      return this.tokens.get(places);
    }
    return null;
  }

  /**
   * This returns Optional.empty() only if the provided index to peek at is beyond the size of
   * tokens
   *
   * @param idx
   * @return
   * @throws SyntaxErrorException
   */
  private Optional<Token> optionalPeekToken(int idx) throws SyntaxErrorException {
    Optional<Token> ret;
    if (idx >= this.tokens.size()) {
      ret = Optional.empty();
    } else {
      if (this.tokens.get(idx) == null) {
        throw new SyntaxErrorException(SyntaxErrorException.ExcType.INTERNAL_ERROR,
            "Found a null entry in tokens at index " + idx);
      }
      ret = Optional.of(this.tokens.get(idx));
    }
    return ret;
  }

  private TokenType peekTokenTypeSafe(int idx) throws Exception {
    return this.optionalPeekToken(idx).map(Token::getTokenType)
        .orElseThrow(() -> new SyntaxErrorException(
            SyntaxErrorException.ExcType.EOF_ERROR, -1, ""));
  }

  private boolean checkPeekMultiple(int idx, List<TokenType> tokenTypes)
      throws Exception {
    for (TokenType tt : tokenTypes) {
      if (tt == this.peekTokenTypeSafe(idx)) {
        return true;
      }
    }
    return false;
  }

  /**
   * Return a string representation of a list of objects (Nodes)
   *
   * @param l The list of objects
   * @return The string representation with indents and linebreaks
   */
  public static String listToString(List<?> l) {
    return l == null ? "" : l.stream().map(Object::toString).collect(Collectors.joining("\n    "));
  }

  /**
   * Return a string representation of a list of objects (Nodes) without linebreaks
   *
   * @param l The list of objects
   * @return The string representation with indents and linebreaks
   */
  public static String listToStringInline(List<?> l) {
    return l == null ? "" : l.stream().map(Object::toString).collect(Collectors.joining(", "));
  }

  /**
   * Parses the program by calling function() in a loop. Every FunctionNode returned by function()
   * goes into the ProgramNode (there will be only one of these). The loop ends when the next token
   * is not DEFINE. Then the ProgramNode is printed.
   */
  public ProgramNode parse() throws Exception {
    ProgramNode program = new ProgramNode();
    TokenType peekRet = this.peekTokenType(0);
    FunctionNode func;
    if (peekRet == TokenType.ENDOFLINE) {
      this.expectsEndOfLine();
      peekRet = this.peekTokenType(0);
    }
    while (peekRet == TokenType.DEFINE) {
      func = this.parseFunc();
      program.addFunction(func);
      peekRet = this.peekTokenType(0);
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

  private MathOpNode.MathOpType getMathOpTypeFromTokenType(TokenType tt) {
    return switch (tt) {
      case PLUS -> MathOpNode.MathOpType.ADD;
      case MINUS -> MathOpNode.MathOpType.SUBTRACT;
      case TIMES -> MathOpNode.MathOpType.MULTIPLY;
      case DIVIDE -> MathOpNode.MathOpType.DIVIDE;
      case MOD -> MathOpNode.MathOpType.MOD;
      default -> null;
    };
  }

  /**
   * Parse an expression (add, subtract operations).
   *
   * @return the MathOpNode or more basic Node that represents the expression
   */
  private Node expression() throws Exception {
    Node term1 = this.term();
    Optional<Token> optionalExpOp = this.optionalMatchAndRemoveTokenSafe(List.of(TokenType.PLUS,
        TokenType.MINUS));
    if (optionalExpOp.isEmpty() && this.curTokenTypeIs(
        Stream.concat(compareTypes.stream(), expOrTermEndTypes.stream()).toList())) {
      return term1;
    }
    // todo: pass new SyntaxErrorException to orElseThrow
    TokenType expOpType = optionalExpOp.map(Token::getTokenType).orElseThrow();
    MathOpNode.MathOpType mathOpType = switch (expOpType) {
      case PLUS -> MathOpNode.MathOpType.ADD;
      case MINUS -> MathOpNode.MathOpType.SUBTRACT;
      default -> {
        throw new SyntaxErrorException(SyntaxErrorException.ExcType.INTERNAL_ERROR, "");
      }
    };
    return new MathOpNode(mathOpType, term1, this.expression(), this.getCurTokenLineNum());
  }

  /**
   * Parse a term (times, divide, mod operations)
   *
   * @return the MathOpNode or more basic Node that represents the term
   */
  private Node term() throws Exception {
    Node factor1 = this.factor();
    Optional<Token> optionalTermOp = this.optionalMatchAndRemoveTokenSafe(termOpTypes);
    if (optionalTermOp.isEmpty() && this.curTokenTypeIs(
        Stream.of(expOpTypes, compareTypes, expOrTermEndTypes).flatMap(Collection::stream)
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
      default -> {
        throw new SyntaxErrorException(SyntaxErrorException.ExcType.INTERNAL_ERROR, "");
      }
    };

    return new MathOpNode(mathOpType, factor1,
        this.findBeforeNextEOL(List.of(TokenType.PLUS, TokenType.MINUS), 1).isPresent()
            ? this.factor() : this.expression(), this.getCurTokenLineNum());
  }

  private Node factor() throws Exception {
    Token factorToken = this.optionalMatchAndRemoveTokenSafe(factorTypes)
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
        return this.parseVarRef();
      }
      case LPAREN -> {
        Node expressionNode = this.expression();
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
      default -> {
        throw new SyntaxErrorException(factorTypes, this.getCurToken());
      }
    }
  }

  /**
   * TODO: make docstring
   *
   * @param
   * @return
   */
  private boolean isTokenTypeTermOp(TokenType tt) {
    return tt == TokenType.MOD || tt == TokenType.DIVIDE || tt == TokenType.TIMES;
  }

  /**
   * TODO: make docstring
   *
   * @param
   * @return
   */
  private boolean isTokenTypeFactor(TokenType tt) {
    return tt == TokenType.MINUS
        || tt == TokenType.NUMBER
        || tt == TokenType.NUMBER_DECIMAL
        || tt == TokenType.LPAREN
        || tt == TokenType.TRUE
        || tt == TokenType.FALSE
        || tt == TokenType.STRINGLITERAL
        || tt == TokenType.IDENTIFIER;
  }

  /**
   * TODO: write docstring
   *
   * @param
   * @return
   */
  private boolean isTokenTypeNumber(TokenType tt) {
    return tt == TokenType.NUMBER || tt == TokenType.NUMBER_DECIMAL;
  }

  /**
   * TODO: write docstring
   *
   * @param
   * @return
   */
  private boolean isTokenTypeExpOp(TokenType tt) {
    return tt == TokenType.PLUS || tt == TokenType.MINUS;
  }

  /**
   * parseFunc() processes a function. It expects a define token. Then an identifier (the name).
   * Then a left paren. Then a list of 0 or more variable declarations. Then a right paren. Then an
   * endOfLine. Then constants and variables (arbitrary number). Then an indent. Then statements.
   * Then a dedent. It returns a FunctionNode or null. Returning null should trigger parse()
   *
   * @return FunctionNode or null if no define TOKEN is found
   */
  private FunctionNode parseFunc() throws Exception {
    // todo: make parseFunc return an optional
    Optional<Token> optionalDefine = this.optionalMatchAndRemoveTokenSafe(TokenType.DEFINE);
    if (optionalDefine.isEmpty()) {
      return null;
    }
//    if (this.matchAndRemoveToken(Token.TokenType.DEFINE) == null) {
//      return null;
//    }
    Token funcName = this.optionalMatchAndRemoveTokenSafe(TokenType.IDENTIFIER).orElseThrow(
        () -> new SyntaxErrorException(TokenType.IDENTIFIER, this.getCurToken()));
    this.optionalMatchAndRemoveTokenSafe(TokenType.LPAREN).orElseThrow(
        () -> new SyntaxErrorException(TokenType.LPAREN, this.getCurToken()));
    // todo: make findTokenType return Optional<Integer>
    if (this.findBeforeNextEOL(TokenType.RPAREN) < 0) {
      throw new SyntaxErrorException(TokenType.RPAREN, this.getCurToken());
    }
    List<VariableNode> paramVars = this.parameterDeclarations();
    this.expectsEndOfLine();

    // LOCAL DECLARATIONS

    List<VariableNode> variableVars = new ArrayList<>();
    List<VariableNode> constVars = new ArrayList<>();
    this.optionalMatchAndRemoveTokenSafe(varConstIndent)
        .orElseThrow(() -> new SyntaxErrorException(varConstIndent, this.getCurToken()));
    while (!this.prevTokenTypeIs(TokenType.INDENT)) {
      if (this.prevTokenTypeIs(TokenType.VARIABLES)) {
        this.parseVars(variableVars);
        this.expectsEndOfLine();
      } else {
        constVars = this.parseConstants();
      }
      this.optionalMatchAndRemoveTokenSafe(varConstIndent)
          .orElseThrow(() -> new SyntaxErrorException(varConstIndent, this.getCurToken()));
    }
    List<StatementNode> statements = this.parseStatements()
        .orElseThrow(() -> new SyntaxErrorException(optionalDefine.get()));
    return new FunctionNode(
        funcName.getValueString(), paramVars, variableVars, constVars, statements);
  }

  /**
   * The range of an array refers to its indices. The range of a number refers to its values. The
   * range of a string refers to its lengths.
   * <p>
   * -1 in intFrom and intTo and/or realFrom and realTo either means the range is not set or not
   * applicable. For example, realFrom and realTo are not applicable for integers, strings, and
   * arrays.
   * <p>
   * variables numberOfCards : integer from 0 to 52
   * <p>
   * variables names : array from 0 to 5 of string
   *
   * @return
   * @throws Exception
   */
  private void parseVars(List<VariableNode> vars) throws Exception {
    List<Token> varNames = this.parseVarNames();
    boolean isArray = this.matchDataTypeIsArray();
    TokenType varTypeTT = this.getPrevTokenType();
    VariableRange range = this.parseVarRange(varTypeTT);
    VariableNode.VariableType varType;
    if (isArray) {
      this.optionalMatchAndRemoveTokenSafe(TokenType.OF)
          .orElseThrow(() -> new SyntaxErrorException(TokenType.OF, this.getCurToken()));
      varType = this.optionalMatchAndRemoveTokenSafe(dataTypes)
          .map(token -> this.getVarTypeFromTokenType(token.getTokenType()))
          .orElseThrow(() -> new SyntaxErrorException(dataTypes, this.getCurToken()));
    } else {
      varType = this.getVarTypeFromTokenType(varTypeTT);
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

  /**
   * @return
   * @throws SyntaxErrorException
   */
  private ConstantDeclaration parseConstant() throws SyntaxErrorException {
    Token name = this.optionalMatchAndRemoveTokenSafe(TokenType.IDENTIFIER)
        .orElseThrow(() -> new SyntaxErrorException(TokenType.IDENTIFIER, this.getCurToken()));

    this.optionalMatchAndRemoveTokenSafe(TokenType.EQUALS)
        .orElseThrow(() -> new SyntaxErrorException(
            TokenType.EQUALS, this.getCurToken()));
    VariableNode.VariableType varType;
    String minusIfPresent = "";
    TokenType literalTokenType = this.optionalMatchAndRemoveTokenSafe(literalTypesIncMinus)
        .map(Token::getTokenType)
        .orElseThrow(() -> new SyntaxErrorException(literalTypesIncMinus, this.getCurToken()));
    if (literalTokenType == TokenType.MINUS) {
      literalTokenType = this.optionalMatchAndRemoveTokenSafe(List.of(
              TokenType.NUMBER_DECIMAL, TokenType.NUMBER_DECIMAL))
          .map(Token::getTokenType)
          .orElseThrow(() -> new SyntaxErrorException(List.of(TokenType.NUMBER_DECIMAL,
              TokenType.NUMBER), this.getCurToken()));
    }
    varType = this.getVarTypeFromTokenType(literalTokenType);

    return (new ConstantDeclaration(
        name.getValueString(),
        varType,
        minusIfPresent + this.getPrevToken().getValueString()));
  }

  /**
   * TODO: write docstring
   *
   * @param
   * @return
   */
  private VariableNode.VariableType getVarTypeFromTokenType(TokenType tt) {
    return switch (tt) {
      case STRING, STRINGLITERAL -> VariableType.STRING;
      case CHARACTER, CHARACTERLITERAL -> VariableType.CHARACTER;
      case INTEGER, NUMBER -> VariableType.INTEGER;
      case REAL, NUMBER_DECIMAL -> VariableType.REAL;
      case BOOLEAN, TRUE, FALSE -> VariableType.BOOLEAN;
      default -> VariableType.ANY;
    };
  }

  /**
   * Determine if the given TokenType is a data type (excludes array)
   *
   * @param t the given TokenType
   * @return true if t is a data type, false otherwise
   */
  private boolean isTokenTypeDataType(TokenType t) {
    return t == TokenType.REAL
        || t == TokenType.INTEGER
        || t == TokenType.BOOLEAN
        || t == TokenType.STRING
        || t == TokenType.CHARACTER;
  }

  /**
   * TODO: write docstring
   *
   * @param
   * @return
   */
  private boolean isCommaOrColon(TokenType tt) {
    return tt == TokenType.COMMA || tt == TokenType.COLON;
  }

  /**
   * (var) identifier comma identifier...
   *
   * <p>Don't use for a "constants" line
   *
   * @return list of varName tokens
   */
  private List<Token> parseVarNames() throws SyntaxErrorException {
    List<Token> varNameTokens = new ArrayList<Token>();
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

  private boolean prevTokenTypeIs(TokenType tokenType) {
    return this.curToken.getPrevTokenType() == tokenType;
  }

  /**
   * Processes a variable range (from and to)
   *
   * <p> todo: ensure appropriate range types are used in SemanticAnalysis
   *
   * @return a VariableRange instance with the range
   */
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

  /**
   * Processes array declarations in parameters (no from or to)
   *
   * @param
   * @return
   */
  private void parseArrayDecParams() throws SyntaxErrorException {
    this.optionalMatchAndRemoveTokenSafe(TokenType.OF)
        .orElseThrow(() -> new SyntaxErrorException(TokenType.OF, this.getCurToken()));
    this.optionalMatchAndRemoveTokenSafe(dataTypes)
        .orElseThrow(() -> new SyntaxErrorException(dataTypes, this.getCurToken()));
  }

  /**
   * Processes the parameters and returns a collection of VariableNode to function().
   *
   * <p>(var) identifier comma identifier...
   *
   * <p>colon type (semicolon or right paren)
   *
   * @param
   * @return
   */
  private List<VariableNode> parameterDeclarations() throws SyntaxErrorException {
    List<VariableNode> ret = new ArrayList<>();
    boolean changeable, isArray;
    isArray = false;
    List<Token> varNames;
    TokenType varType;
    this.optionalMatchAndRemoveTokenSafe(TokenType.RPAREN);
    while (!this.prevTokenTypeIs(TokenType.RPAREN)) {
      changeable = this.optionalMatchAndRemoveTokenSafe(TokenType.VAR).isPresent();
      varNames = this.parseVarNames();
      isArray = this.matchDataTypeIsArray();
      if (isArray) {
        this.parseArrayDecParams();
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
                this.getVarTypeFromTokenType(varType),
                changeable,
                isArray, this.getPrevTokenLineNum(), new VariableRange()));
      }
    }
    return ret;
  }

  private TokenType getPrevTokenType() {
    return this.curToken.getPrevTokenType();
  }

  private Token getPrevToken() {
    return this.curToken.getPrevToken();
  }

  private boolean matchDataTypeIsArray() throws SyntaxErrorException {
    // todo: check in SemanticAnalysis that arrays aren't declared var
    return this.optionalMatchAndRemoveTokenSafe(dataTypesIncArr)
        .map(dataType -> dataType.getTokenType() == TokenType.ARRAY)
        .orElseThrow(() -> new SyntaxErrorException(dataTypesIncArr, this.getCurToken()));
  }

  private int getPrevTokenLineNum() {
    return this.curToken.getPrevTokenLineNum();
  }

  /**
   * Get the BooleanCompareNode.CompareType enum value that corresponds to the given TokenType.
   *
   * @param tt the given TokenType
   * @return the corresponding BooleanCompareNode.CompareType enum value
   */
  private BooleanCompareNode.CompareType getCompTypeFromTokenType(TokenType tt) {
    for (BooleanCompareNode.CompareType ct : BooleanCompareNode.CompareType.values()) {
      if (Objects.equals(ct.toString(), tt.toString())) {
        return ct;
      }
    }
    return null;
  }

  /**
   * Check if the given TokenType is a comparison operator (<, >, <=, etc)
   *
   * @param tt the TokenType to check
   * @return true if tt is a comparison operator, false otherwise
   */
  private boolean isCompOp(TokenType tt) {
    return tt == TokenType.LESSTHAN
        || tt == TokenType.GREATERTHAN
        || tt == TokenType.LESSEQUAL
        || tt == TokenType.GREATEREQUAL
        || tt == TokenType.EQUALS
        || tt == TokenType.NOTEQUAL;
  }

  /**
   * BOOLCOMPARE = EXPRESSION [ (<,>,<=,>=,=,<>) EXPRESSION]
   *
   * <p>[] indicates 0 or 1
   *
   * @return a Node that could be BooleanCompareNode, MathOpNode, or individual element node
   */
  private Node boolCompare() throws Exception {
    Node expression1 = this.expression();
    Optional<Token> optionalCompareToken = this.optionalMatchAndRemoveTokenSafe(List.of(
        TokenType.LESSEQUAL, TokenType.LESSTHAN, TokenType.GREATEREQUAL, TokenType.GREATERTHAN,
        TokenType.NOTEQUAL, TokenType.EQUALS));
    if (optionalCompareToken.isEmpty()) {
      return expression1;
    }
    // todo: getCompTypeFromTokenType can return null
    BooleanCompareNode.CompareType compareType = this.getCompTypeFromTokenType(
        optionalCompareToken.map(Token::getTokenType).orElse(TokenType.NONE));
    return new BooleanCompareNode(compareType, expression1, this.expression());
  }

  /**
   * Parse the syntax for a variable reference node, using recursion if needed to parse nested array
   * index expressions.
   *
   * @return a VariableReferenceNode instance for the variable
   * @throws Exception
   */
  private VariableReferenceNode parseVarRef() throws Exception {
    String name = this.getPrevToken().getValueString();
    this.optionalMatchAndRemoveTokenSafe(TokenType.LSQUARE);
    if (this.prevTokenTypeIs(TokenType.LSQUARE)) {
      VariableReferenceNode ret = new VariableReferenceNode(name, this.expression());
      this.optionalMatchAndRemoveTokenSafe(TokenType.RSQUARE)
          .orElseThrow(() -> new SyntaxErrorException(
              TokenType.RSQUARE, this.getCurToken()));
      return ret;
    }
    return new VariableReferenceNode(name, null);
  }

  /**
   * Process an assignment statement.
   *
   * @return an AssignmentNode instance
   */
  private AssignmentNode parseAssignment() throws Exception {
    VariableReferenceNode leftSide = this.parseVarRef();
    this.optionalMatchAndRemoveTokenSafe(TokenType.ASSIGN);
    Node rightSide = this.boolCompare();
    this.expectsEndOfLine();
    return new AssignmentNode(leftSide, rightSide);
  }

  private Optional<StatementNode> parseStatement() throws Exception {
    this.optionalMatchAndRemoveTokenSafe(statementInitTypes);
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
            : Optional.of(this.functionCall());
      }
      case DEDENT -> {
        return Optional.empty();
      }
      default -> {
        throw new SyntaxErrorException(statementInitTypes, this.getPrevToken());
      }
    }
  }

  /**
   * Expects indent, calls statement() repeatedly (until it returns null) and then expects dedent.
   * Returns a collection of StatementNode.
   *
   * @return an ArrayList of StatementNode instances.
   */
  private Optional<List<StatementNode>> parseStatements() throws Exception {
    this.optionalMatchAndRemoveTokenSafe(TokenType.INDENT);
    var ret = new ArrayList<StatementNode>();
    Optional<StatementNode> statement = this.parseStatement();
    while (statement.isPresent()) {
      ret.add(statement.get());
      statement = this.parseStatement();
    }
    return ret.size() > 0 ? Optional.of(ret) : Optional.empty();
  }

  /**
   * TODO: write docstring
   *
   * @param
   * @return
   */
  private ForNode parseFor() throws Exception {
    Token forToken = new Token(this.getPrevToken());
    this.optionalMatchAndRemoveTokenSafe(TokenType.IDENTIFIER)
        .orElseThrow(() -> new SyntaxErrorException(
            TokenType.IDENTIFIER, this.getCurToken()));
    // TODO: Ensure in semantic analysis that this varRef has no array subscript
    VariableReferenceNode varRef = this.parseVarRef();
    this.optionalMatchAndRemoveTokenSafe(TokenType.FROM).orElseThrow(() -> new SyntaxErrorException(
        TokenType.FROM, this.getCurToken()));
    Node fromExp = this.expression();
    this.optionalMatchAndRemoveTokenSafe(TokenType.TO).orElseThrow(() -> new SyntaxErrorException(
        TokenType.TO, this.getCurToken()));
    Node toExp = this.expression();
    this.expectsEndOfLine();
    return new ForNode(varRef, fromExp, toExp,
        this.parseStatements().orElseThrow(() -> new SyntaxErrorException(forToken)));
  }

  /**
   * TODO: write docstring
   *
   * @param
   * @return
   */
  private IfNode parseIf() throws Exception {
    Token ifOrElsifOrElse = new Token(this.getPrevToken());
    List<StatementNode> statements;
    if (ifOrElsifOrElse.getTokenType() != TokenType.ELSE) {
      Node condition = this.boolCompare();
      // TODO: Test here if condition is null
      this.optionalMatchAndRemoveTokenSafe(TokenType.THEN)
          .orElseThrow(() -> new SyntaxErrorException(
              TokenType.THEN, this.getCurToken()));
      this.expectsEndOfLine();
      statements = this.parseStatements()
          .orElseThrow(() -> new SyntaxErrorException(ifOrElsifOrElse));
      TokenType possibleNextIf = this.optionalMatchAndRemoveTokenSafe(
          List.of(TokenType.ELSIF, TokenType.ELSE)).map(Token::getTokenType).orElse(TokenType.NONE);
      if (possibleNextIf != TokenType.NONE) {
        return new IfNode(condition, statements, this.parseIf(), ifOrElsifOrElse.getTokenType());
      } else {
        return new IfNode(condition, statements, ifOrElsifOrElse.getTokenType());
      }
    } else {
      this.expectsEndOfLine();
      statements = this.parseStatements()
          .orElseThrow(() -> new SyntaxErrorException(ifOrElsifOrElse));
      return new IfNode(statements, ifOrElsifOrElse.getTokenType());
    }
  }

  /**
   * TODO: write docstring
   *
   * @param
   * @return
   */
  private WhileNode parseWhile() throws Exception {
    Token whileToken = this.getPrevToken();
    Node condition = this.boolCompare();
    this.expectsEndOfLine();
    return new WhileNode(condition,
        this.parseStatements().orElseThrow(() -> new SyntaxErrorException(whileToken)));
  }

  /**
   * TODO: write docstring
   *
   * @param
   * @return
   */
  private RepeatNode parseRepeat() throws Exception {
    Token repeatToken = new Token(this.getPrevToken());
    this.optionalMatchAndRemoveTokenSafe(TokenType.UNTIL)
        .orElseThrow(() -> new SyntaxErrorException(
            TokenType.UNTIL, this.getCurToken()));
    Node condition = this.boolCompare();
    this.expectsEndOfLine();
    return new RepeatNode(condition,
        this.parseStatements().orElseThrow(() -> new SyntaxErrorException(repeatToken)));
  }

  /**
   * TODO: write docstring
   *
   * @param
   * @return
   */
  public ArgumentNode getTheArg() throws Exception {
    if (this.optionalMatchAndRemoveTokenSafe(TokenType.VAR).isPresent()) {
      if (this.optionalMatchAndRemoveTokenSafe(TokenType.IDENTIFIER).isEmpty()) {
        throw new SyntaxErrorException(TokenType.IDENTIFIER, this.getCurToken());
      }
      return new ArgumentNode(this.parseVarRef(), true);
    } else {
      return new ArgumentNode(this.boolCompare(), false);
    }
  }

  /**
   * TODO: finish docstring
   *
   * @param
   * @return
   */
  private FunctionCallNode functionCall() throws Exception {
    Token funcName = this.getPrevToken();
    if (this.curTokenTypeIs(TokenType.ENDOFLINE)) {
      this.expectsEndOfLine();
      return new FunctionCallNode(funcName.getValueString());
    }
    ArgumentNode theArg = this.getTheArg();
    var theArgs = new ArrayList<ArgumentNode>();
    theArgs.add(theArg);
    while (this.optionalMatchAndRemoveTokenSafe(TokenType.COMMA).isPresent() || this.curTokenTypeIs(
        TokenType.VAR)) {
      theArg = this.getTheArg();
      theArgs.add(theArg);
    }
    this.expectsEndOfLine();
    return new FunctionCallNode(funcName.getValueString(), theArgs);
  }
}
