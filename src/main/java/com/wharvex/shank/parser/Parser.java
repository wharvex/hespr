package com.wharvex.shank.parser;

import com.wharvex.shank.SyntaxErrorException;
import com.wharvex.shank.SyntaxErrorException.ExcType;
import com.wharvex.shank.lexer.Token;
import com.wharvex.shank.lexer.Token.TokenType;
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
import com.wharvex.shank.semantic.SemanticErrorException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class Parser {

  private final List<Token> tokens;
  private final PeekCheck peekCheck;

  /**
   * Constructor
   *
   * @param tokens the tokens list
   */
  public Parser(List<Token> tokens) throws SyntaxErrorException {
    this.tokens = tokens;
    this.peekCheck = new PeekCheck(this.peekToken(0), true);
  }

  /**
   * Check if tokens is not empty
   *
   * @return true if not empty, false otherwise
   */
  public boolean tokensNotEmpty() {
    return this.tokens.size() != 0;
  }

  private PeekCheck getPeekCheck() {
    return this.peekCheck;
  }

  private void updatePeekCheck() throws SyntaxErrorException {
    this.peekCheck.updatePeek(this.peekToken(0), false);
  }

  private void updatePeekCheckNullSafe() throws SyntaxErrorException {
    this.peekCheck.updatePeek(this.peekToken(0), true);
  }

  /**
   * Find the index position in tokens of the next occurrence of an RPAREN token
   *
   * @return the index position of the next RPAREN token
   */
  private int findRParen() {
    int ret = 0;
    boolean found = false;
    while (ret < this.tokens.size()) {
      if (this.tokens.get(ret).getTokenType() == Token.TokenType.RPAREN) {
        found = true;
        break;
      } else {
        ret++;
      }
    }
    return found ? ret : -1;
  }

  private String peekToString() {
    Token peekTokenRet = this.peekToken(0);
    return peekTokenRet != null ? peekTokenRet.toString() : "EOF";
  }

  /**
   * Checks if a TokenType is the next token in tokens and removes it if it is.
   *
   * @param tokenType the TokenType to match in tokens and remove if present
   * @return the Token if removed, null otherwise
   */
  private Token matchAndRemoveToken(Token.TokenType tokenType) {
    return this.tokensNotEmpty() && this.tokens.get(0).getTokenType() == tokenType
        ? this.tokens.remove(0)
        : null;
  }

  private void removeToken() {
    this.tokens.remove(0);
  }

  /**
   * Checks if the next token in tokens is an ENDOFLINE token and throws an error if it isn't.
   * Removes the token if it is and continues removing ENDOFLINE tokens if they are next in tokens.
   */
  private void expectsEndOfLine() throws Exception {
    Token endOfLine = this.matchAndRemoveToken(Token.TokenType.ENDOFLINE);
    if (endOfLine == null) {
      throw new SyntaxErrorException(
          SyntaxErrorException.ExcType.EOL_ERROR, "end of line", this.peekToString());
    }
    Token.TokenType peekRet = this.peek(0);
    while (peekRet == Token.TokenType.ENDOFLINE) {
      this.matchAndRemoveToken(peekRet);
      peekRet = this.peek(0);
    }
  }

  /**
   * Returns the TokenType of the Token found at index places of tokens
   *
   * @param places the index position of tokens to check
   * @return the TokenType found or null if places is beyond the size of tokens
   */
  private Token.TokenType peek(int places) {
    if (this.tokens.size() > places) {
      return this.tokens.get(places).getTokenType();
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
    Token.TokenType peekRet = this.peek(0);
    FunctionNode func;
    if (peekRet == Token.TokenType.ENDOFLINE) {
      this.expectsEndOfLine();
      peekRet = this.peek(0);
    }
    while (peekRet == Token.TokenType.DEFINE) {
      func = this.parseFunc();
      program.addFunction(func);
      peekRet = this.peek(0);
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

  private MathOpNode.MathOpType getMathOpTypeFromTokenType(Token.TokenType tt) {
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
    Node term1;
    MathOpNode.MathOpType mathOpType;

    // Find term and expression operator

    term1 = this.term();
    Token peekTokenRet = this.peekToken(0);
    if (peekTokenRet == null) {
      return term1;
    }
    Token.TokenType peekRet1 = peekTokenRet.getTokenType();
    switch (peekRet1) {
      case PLUS -> mathOpType = MathOpNode.MathOpType.ADD;
      case MINUS -> mathOpType = MathOpNode.MathOpType.SUBTRACT;
      default -> {
        if (!this.isTokenTypeTermOp(peekRet1)) {
          return term1;
        }
        this.removeToken();
        return new MathOpNode(
            this.getMathOpTypeFromTokenType(peekRet1),
            term1,
            this.expression(), peekTokenRet.getTokenLineNum());
      }
    }
    // Eat the expOp
    this.matchAndRemoveToken(peekRet1);

    // Peek and return

    peekRet1 = this.peek(0);
    if (!this.isTokenTypeFactor(peekRet1)) {
      throw new SyntaxErrorException(
          SyntaxErrorException.ExcType.EXPRESSION_ERROR, "factor", this.peekToString());
    }
    if (peekRet1 == Token.TokenType.MINUS && !this.isTokenTypeNumber(this.peek(1))) {
      throw new SyntaxErrorException(
          SyntaxErrorException.ExcType.EXPRESSION_ERROR, "number", this.peekToString());
    }
    Token.TokenType peekRet2 = this.peek(1);
    if (this.isTokenTypeNumber(peekRet2)) {
      peekRet2 = this.peek(2);
    }
    if (this.isTokenTypeTermOp(peekRet2)) {
      return new MathOpNode(mathOpType, term1, this.expression(), peekTokenRet.getTokenLineNum());
    } else if (this.isTokenTypeExpOp(peekRet2)) {
      return new MathOpNode(mathOpType, term1, this.expression(), peekTokenRet.getTokenLineNum());
    }
    return new MathOpNode(mathOpType, term1, this.factor(), peekTokenRet.getTokenLineNum());
  }

  /**
   * Parse a term (times, divide, mod operations)
   *
   * @return the MathOpNode or more basic Node that represents the term
   */
  private Node term() throws Exception {
    Node factor1;
    MathOpNode.MathOpType mathOpType;

    // Find factor and math operator type

    factor1 = this.factor();
    Token peekTokenRet = this.peekToken(0);
    // Need to null-check before the switch statement because switch dereferences its argument
    if (peekTokenRet == null) {
      return factor1;
    }
    Token.TokenType peekRet = peekTokenRet.getTokenType();
    switch (peekRet) {
      case TIMES -> {
        this.removeToken();
        mathOpType = MathOpNode.MathOpType.MULTIPLY;
      }
      case MOD -> {
        this.removeToken();
        mathOpType = MathOpNode.MathOpType.MOD;
      }
      case DIVIDE -> {
        this.removeToken();
        mathOpType = MathOpNode.MathOpType.DIVIDE;
      }
      default -> {
        return factor1;
      }
    }

    // Peek and return

    peekRet = this.peek(0);
    Token.TokenType peekRet2 = this.peek(1);
    if (!this.isTokenTypeFactor(peekRet)) {
      throw new SyntaxErrorException(
          SyntaxErrorException.ExcType.TERM_ERROR, "second factor", this.peekToString());
    }
    if (peekRet == Token.TokenType.MINUS && !this.isTokenTypeNumber(peekRet2)) {
      throw new SyntaxErrorException(
          SyntaxErrorException.ExcType.TERM_ERROR, "second factor", "minus " + this.peekToString());
    }
    if (this.isTokenTypeNumber(peekRet2) && peekRet != Token.TokenType.LPAREN) {
      peekRet2 = this.peek(2);
    }
    int rParenPos = this.findRParen();
    if (rParenPos > 0) {
      peekRet2 = this.peek(rParenPos + 1);
    }
    if (this.isTokenTypeTermOp(peekRet2)) {
      return new MathOpNode(mathOpType, factor1, this.term(), peekTokenRet.getTokenLineNum());
    }
    return new MathOpNode(mathOpType, factor1, this.factor(), peekTokenRet.getTokenLineNum());
  }

  /**
   * FACTOR = {-} number or lparen EXPRESSION rparen or variableReferenceNode (IDENTIFIER)
   *
   * @return the FACTOR
   */
  private Node factor() throws Exception {
    String minusOrEmpty = this.matchAndRemoveToken(Token.TokenType.MINUS) != null ? "-" : "";
    Token peekTokenRet = this.peekToken(0);
    if (peekTokenRet == null) {
      return null;
    }
    Token.TokenType peekRet = peekTokenRet.getTokenType();
    int lineNum = peekTokenRet.getTokenLineNum();
    String valStr = peekTokenRet.getValueString();
    switch (peekRet) {
      case NUMBER -> {
        this.removeToken();
        return new IntegerNode(Integer.parseInt(minusOrEmpty + valStr), lineNum);
      }
      case NUMBER_DECIMAL -> {
        this.removeToken();
        return new RealNode(Float.parseFloat(minusOrEmpty + valStr), lineNum);
      }
      case IDENTIFIER -> {
        return this.parseVarRef();
      }
      case LPAREN -> {
        this.removeToken();
        Node expRet = this.expression();
        if (expRet == null) {
          throw new SyntaxErrorException(
              SyntaxErrorException.ExcType.FACTOR_ERROR, "expression", this.peekToString());
        }
        if (this.matchAndRemoveToken(Token.TokenType.RPAREN) == null) {
          throw new SyntaxErrorException(
              SyntaxErrorException.ExcType.FACTOR_ERROR, "right paren", this.peekToString());
        }
        return expRet;
      }
      case TRUE -> {
        this.removeToken();
        return new BooleanNode(true, lineNum);
      }
      case FALSE -> {
        this.removeToken();
        return new BooleanNode(false, lineNum);
      }
      case STRINGLITERAL -> {
        this.removeToken();
        return new StringNode(valStr, lineNum);
      }
      default -> {
        return null;
      }
    }
  }

  /**
   * TODO: make docstring
   *
   * @param
   * @return
   */
  private boolean isTokenTypeTermOp(Token.TokenType tt) {
    return tt == Token.TokenType.MOD || tt == Token.TokenType.DIVIDE || tt == Token.TokenType.TIMES;
  }

  /**
   * TODO: make docstring
   *
   * @param
   * @return
   */
  private boolean isTokenTypeFactor(Token.TokenType tt) {
    return tt == Token.TokenType.MINUS
        || tt == Token.TokenType.NUMBER
        || tt == Token.TokenType.NUMBER_DECIMAL
        || tt == Token.TokenType.LPAREN
        || tt == Token.TokenType.TRUE
        || tt == Token.TokenType.FALSE
        || tt == Token.TokenType.STRINGLITERAL
        || tt == Token.TokenType.IDENTIFIER;
  }

  /**
   * TODO: write docstring
   *
   * @param
   * @return
   */
  private boolean isTokenTypeNumber(Token.TokenType tt) {
    return tt == Token.TokenType.NUMBER || tt == Token.TokenType.NUMBER_DECIMAL;
  }

  /**
   * TODO: write docstring
   *
   * @param
   * @return
   */
  private boolean isTokenTypeExpOp(Token.TokenType tt) {
    return tt == Token.TokenType.PLUS || tt == Token.TokenType.MINUS;
  }

  /**
   * parseFunc() processes a function. It expects a define token. Then an identifier (the name). Then
   * a left paren. Then a list of 0 or more variable declarations. Then a right paren. Then an
   * endOfLine. Then constants and variables (arbitrary number). Then an indent. Then statements.
   * Then a dedent. It returns a FunctionNode or null. Returning null should trigger parse()
   *
   * @return FunctionNode or null if no define TOKEN is found
   */
  private FunctionNode parseFunc() throws Exception {
    if (this.matchAndRemoveToken(Token.TokenType.DEFINE) == null) {
      return null;
    }
    Token funcName = this.matchAndRemoveToken(Token.TokenType.IDENTIFIER);
    if (funcName == null) {
      throw new SyntaxErrorException(
          SyntaxErrorException.ExcType.FUNCTION_ERROR, "function name", this.peekToString());
    }
    if (this.matchAndRemoveToken(Token.TokenType.LPAREN) == null) {
      throw new SyntaxErrorException(
          SyntaxErrorException.ExcType.FUNCTION_ERROR, "left paren", this.peekToString());
    }
    if (this.findRParen() < 0) {
      throw new SyntaxErrorException(
          SyntaxErrorException.ExcType.FUNCTION_ERROR, "right paren", this.peekToString());
    }
    List<VariableNode> paramVars = this.parameterDeclarations();
    this.expectsEndOfLine();

    // LOCAL DECLARATIONS

    this.updatePeekCheckNullSafe();
    this.getPeekCheck().expectsDeclarationsOrIndent();
    List<VariableNode> variableVars = new ArrayList<>();
    List<VariableNode> constVars = new ArrayList<>();
    VariableNode constVar;
    // Continue checking for declaration lines until INDENT is found
    while (!this.getPeekCheck().isIndent()) {
      if (this.getPeekCheck().getTokenType() == Token.TokenType.VARIABLES) {
        variableVars = this.parseVars();
      } else {
        constVars = this.parseConstants();
      }
      this.expectsEndOfLine();
      this.updatePeekCheckNullSafe();
    }
    return new FunctionNode(
        funcName.getValueString(), paramVars, variableVars, constVars, this.statements());
  }

  /**
   * The range of an array refers to its indices. The range of a number refers to its values. The
   * range of a string refers to its lengths.
   * <p>
   * -1 in intFrom and intTo and/or realFrom and realTo either means the range is not set or not
   * applicable. For example, realFrom and realTo are not applicable for integers, strings, and
   * arrays.
   *
   * @return
   * @throws Exception
   */
  private List<VariableNode> parseVars() throws Exception {
    var ret = new ArrayList<VariableNode>();
    // Remove VARIABLES token
    this.removeToken();
    List<Token> varNamesVariables = this.parseVarNames();
    Token peekTokenRet = this.peekToken(0);
    if (peekTokenRet == null) {
      return null;
    }
    Token.TokenType peekRet = peekTokenRet.getTokenType();
    if (!(peekRet == Token.TokenType.ARRAY || this.isTokenTypeDataType(peekRet))) {
      throw new SyntaxErrorException(
          SyntaxErrorException.ExcType.VARIABLES_ERROR, "data type", this.peekToString());
    }
    boolean isArray = peekRet == Token.TokenType.ARRAY;
    int intFrom, intTo;
    float realFrom, realTo;
    intFrom = intTo = -1;
    realFrom = realTo = -1;
    VariableNode.VariableType varType;
    if (isArray) {
      ArrayDeclaration arrayDec = this.parseArrayDec();
      varType = arrayDec.getType();
      intFrom = arrayDec.getFrom();
      intTo = arrayDec.getTo();
    } else {
      varType = this.getVarTypeFromTokenType(peekRet);
      this.removeToken();
      if (this.peek(0) == Token.TokenType.FROM) {
        if (peekRet != Token.TokenType.STRING
            && peekRet != Token.TokenType.REAL
            && peekRet != Token.TokenType.INTEGER) {
          throw new SemanticErrorException(
              "Variable range not supported for type "
                  + peekRet
                  + " -- line "
                  + peekTokenRet.getTokenLineNum());
        }
        VariableRange varRange = this.parseVarRange(peekRet);
        intFrom = varRange.getFrom();
        intTo = varRange.getTo();
        realFrom = varRange.getRealFrom();
        realTo = varRange.getRealTo();
      }
    }
    for (Token vn : varNamesVariables) {
      ret.add(
          new VariableNode(
              vn.getValueString(), varType, true, intFrom, intTo, isArray, realFrom, realTo,
              peekTokenRet.getTokenLineNum()));
    }
    return ret;
  }

  private List<VariableNode> parseConstants() throws SyntaxErrorException {
    var constDecs = new ArrayList<ConstantDeclaration>();
    VariableNode constVar;
    var constVars = new ArrayList<VariableNode>();
    // Remove CONSTANTS token
    this.removeToken();
    do {
      constDecs.add(this.parseConstant());
      this.updatePeekCheckNullSafe();
      if (this.getPeekCheck().isComma()) {
        this.removeToken();
        this.updatePeekCheckNullSafe();
      }
    } while (!this.getPeekCheck().isEOL());
    for (ConstantDeclaration cd : constDecs) {
      constVar = new VariableNode(cd.getName(), cd.getType(), false, -1, -1, false,
          this.getPeekCheck().getLineNum());
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
    Token name = this.matchAndRemoveToken(Token.TokenType.IDENTIFIER);
    if (name == null) {
      throw new SyntaxErrorException(
          SyntaxErrorException.ExcType.CONSTANTS_ERROR, "constant name", this.peekToString());
    }
    if (this.matchAndRemoveToken(Token.TokenType.EQUALS) == null) {
      throw new SyntaxErrorException(
          SyntaxErrorException.ExcType.FUNCTION_ERROR,
          this.peekToken(0).getTokenLineNum(),
          "found constants then name then not equals");
    }
    Token.TokenType peekRet = this.peek(0);
    VariableNode.VariableType varType;
    String minusIfPresent = "";
    switch (peekRet) {
      case NUMBER -> varType = VariableNode.VariableType.INTEGER;
      case NUMBER_DECIMAL -> varType = VariableNode.VariableType.REAL;
      case MINUS -> {
        if (this.peek(1) == TokenType.NUMBER) {
          varType = VariableNode.VariableType.INTEGER;
        } else if (this.peek(1) == TokenType.NUMBER_DECIMAL) {
          varType = VariableNode.VariableType.REAL;
        } else {
          throw new SyntaxErrorException(
              ExcType.FUNCTION_ERROR,
              this.peekToken(0).getTokenLineNum(),
              "found invalid constants declaration");
        }
        minusIfPresent = "-";
      }
      case STRINGLITERAL -> varType = VariableNode.VariableType.STRING;
      case CHARACTERLITERAL -> varType = VariableNode.VariableType.CHARACTER;
      case TRUE, FALSE -> varType = VariableNode.VariableType.BOOLEAN;
      default -> throw new SyntaxErrorException(
          ExcType.FUNCTION_ERROR,
          this.peekToken(0).getTokenLineNum(),
          "found invalid constants declaration");
    }
    return (new ConstantDeclaration(
        name.getValueString(),
        varType,
        minusIfPresent + this.matchAndRemoveToken(peekRet).getValueString()));
  }

  /**
   * TODO: write docstring
   *
   * @param
   * @return
   */
  private VariableNode.VariableType getVarTypeFromTokenType(Token.TokenType tt) {
    for (VariableNode.VariableType vt : VariableNode.VariableType.values()) {
      if (vt.toString() == tt.toString()) {
        return vt;
      }
    }
    return null;
  }

  /**
   * Determine if the given TokenType is a data type (excludes array)
   *
   * @param t the given TokenType
   * @return true if t is a data type, false otherwise
   */
  private boolean isTokenTypeDataType(Token.TokenType t) {
    return t == Token.TokenType.REAL
        || t == Token.TokenType.INTEGER
        || t == Token.TokenType.BOOLEAN
        || t == Token.TokenType.STRING
        || t == Token.TokenType.CHARACTER;
  }

  /**
   * TODO: write docstring
   *
   * @param
   * @return
   */
  private boolean isCommaOrColon(Token.TokenType tt) {
    return tt == Token.TokenType.COMMA || tt == Token.TokenType.COLON;
  }

  /**
   * (var) identifier comma identifier...
   *
   * <p>Don't use for a "constants" line
   *
   * @return list of varName tokens
   */
  private List<Token> parseVarNames() throws SyntaxErrorException {
    Token varNameToken = this.matchAndRemoveToken(Token.TokenType.IDENTIFIER);
    if (varNameToken == null) {
      throw new SyntaxErrorException(
          SyntaxErrorException.ExcType.PARAMETERS_ERROR,
          this.peekToken(0).getTokenLineNum(),
          "found no variable name identifiers or right parenthesis");
    }
    List<Token> varNameTokens = new ArrayList<Token>();
    varNameTokens.add(varNameToken);
    Token.TokenType commaOrColon = this.peek(0);
    while (commaOrColon != Token.TokenType.COLON) {
      if (!this.isCommaOrColon(commaOrColon)) {
        throw new SyntaxErrorException(
            SyntaxErrorException.ExcType.PARAMETERS_ERROR,
            this.peekToken(0).getTokenLineNum(),
            "found variable name identifier then not comma or colon");
      }
      // This must be comma after the previous while and if tests
      this.matchAndRemoveToken(Token.TokenType.COMMA);
      varNameToken = this.matchAndRemoveToken(Token.TokenType.IDENTIFIER);
      if (varNameToken == null) {
        throw new SyntaxErrorException(
            SyntaxErrorException.ExcType.PARAMETERS_ERROR,
            this.peekToken(0).getTokenLineNum(),
            "found variable name identifier then comma then not variable name identifier");
      }
      varNameTokens.add(varNameToken);
      commaOrColon = this.peek(0);
    }
    this.matchAndRemoveToken(Token.TokenType.COLON);
    return varNameTokens;
  }

  /**
   * Processes an array declaration outside parameters (has from and to)
   *
   * @return an ArrayDeclaration instance representing the declaration
   */
  private ArrayDeclaration parseArrayDec() throws SyntaxErrorException {
    this.matchAndRemoveToken(Token.TokenType.ARRAY);
    if (this.matchAndRemoveToken(Token.TokenType.FROM) == null) {
      throw new SyntaxErrorException(
          SyntaxErrorException.ExcType.VARIABLES_ERROR,
          this.peekToken(0).getTokenLineNum(),
          "found incomplete array type declaration");
    }
    int from, to;
    Token fromNumToken = this.matchAndRemoveToken(Token.TokenType.NUMBER);
    if (fromNumToken == null) {
      throw new SyntaxErrorException(
          SyntaxErrorException.ExcType.VARIABLES_ERROR,
          this.peekToken(0).getTokenLineNum(),
          "found incomplete array type declaration");
    }
    from = Integer.parseInt(fromNumToken.getValueString());
    if (this.matchAndRemoveToken(Token.TokenType.TO) == null) {
      throw new SyntaxErrorException(
          SyntaxErrorException.ExcType.VARIABLES_ERROR,
          this.peekToken(0).getTokenLineNum(),
          "found incomplete array type declaration");
    }
    Token toNumToken = this.matchAndRemoveToken(Token.TokenType.NUMBER);
    if (toNumToken == null) {
      throw new SyntaxErrorException(
          SyntaxErrorException.ExcType.VARIABLES_ERROR,
          this.peekToken(0).getTokenLineNum(),
          "found incomplete array type declaration");
    }
    to = Integer.parseInt(toNumToken.getValueString());
    if (this.matchAndRemoveToken(Token.TokenType.OF) == null) {
      throw new SyntaxErrorException(
          SyntaxErrorException.ExcType.VARIABLES_ERROR,
          this.peekToken(0).getTokenLineNum(),
          "found incomplete array type declaration");
    }
    Token.TokenType peekRet = this.peek(0);
    if (!this.isTokenTypeDataType(peekRet)) {
      throw new SyntaxErrorException(
          SyntaxErrorException.ExcType.VARIABLES_ERROR,
          this.peekToken(0).getTokenLineNum(),
          "found incomplete array type declaration");
    }
    this.matchAndRemoveToken(peekRet);
    return new ArrayDeclaration(from, to, this.getVarTypeFromTokenType(peekRet));
  }

  /**
   * Processes a variable range (from and to)
   *
   * <p>Idea behind string limits: from length to length
   *
   * @return a VariableRange instance with the range
   */
  private VariableRange parseVarRange(Token.TokenType varType) throws SyntaxErrorException {
    this.matchAndRemoveToken(Token.TokenType.FROM);
    int from, to;
    from = to = -1;
    float realFrom, realTo;
    realFrom = realTo = -1;
    Token.TokenType peekRet = this.peek(0);
    switch (peekRet) {
      case NUMBER:
        if (varType == Token.TokenType.REAL) {
          throw new SyntaxErrorException(
              SyntaxErrorException.ExcType.FUNCTION_ERROR, "real from", this.peekToString());
        }
        from = Integer.parseInt(this.peekToken(0).getValueString());
        break;
      case NUMBER_DECIMAL:
        if (varType != Token.TokenType.REAL) {
          throw new SyntaxErrorException(
              SyntaxErrorException.ExcType.FUNCTION_ERROR, "int from", this.peekToString());
        }
        realFrom = Float.parseFloat(this.peekToken(0).getValueString());
        break;

      default:
        if (varType == Token.TokenType.REAL) {
          throw new SyntaxErrorException(
              SyntaxErrorException.ExcType.FUNCTION_ERROR, "real from", this.peekToString());
        } else {
          throw new SyntaxErrorException(
              SyntaxErrorException.ExcType.FUNCTION_ERROR, "int from", this.peekToString());
        }
    }
    this.matchAndRemoveToken(peekRet);
    if (this.matchAndRemoveToken(Token.TokenType.TO) == null) {
      throw new SyntaxErrorException(
          SyntaxErrorException.ExcType.FUNCTION_ERROR, "to", this.peekToString());
    }
    peekRet = this.peek(0);
    switch (peekRet) {
      case NUMBER:
        if (varType == Token.TokenType.REAL) {
          throw new SyntaxErrorException(
              SyntaxErrorException.ExcType.FUNCTION_ERROR, "real to", this.peekToString());
        }
        to = Integer.parseInt(this.peekToken(0).getValueString());
        break;
      case NUMBER_DECIMAL:
        if (varType != Token.TokenType.REAL) {
          throw new SyntaxErrorException(
              SyntaxErrorException.ExcType.FUNCTION_ERROR, "int to", this.peekToString());
        }
        realTo = Float.parseFloat(this.peekToken(0).getValueString());
        break;

      default:
        if (varType == Token.TokenType.REAL) {
          throw new SyntaxErrorException(
              SyntaxErrorException.ExcType.FUNCTION_ERROR, "real to", this.peekToString());
        } else {
          throw new SyntaxErrorException(
              SyntaxErrorException.ExcType.FUNCTION_ERROR, "int to", this.peekToString());
        }
    }
    this.matchAndRemoveToken(peekRet);
    return new VariableRange(from, to, realFrom, realTo);
  }

  /**
   * Processes array declarations in parameters (no from or to)
   *
   * @param
   * @return
   */
  private Token.TokenType parseArrayDecParams() throws SyntaxErrorException {
    this.matchAndRemoveToken(Token.TokenType.ARRAY);
    if (this.matchAndRemoveToken(Token.TokenType.OF) == null) {
      throw new SyntaxErrorException(
          SyntaxErrorException.ExcType.PARAMETERS_ERROR,
          this.peekToken(0).getTokenLineNum(),
          "expected 'of' when parsing array type declaration, found "
              + this.peekToken(0).getValueString()
              + " -- "
              + this.peek(0));
    }
    Token.TokenType peekRet = this.peek(0);
    if (!this.isTokenTypeDataType(peekRet)) {
      throw new SyntaxErrorException(
          SyntaxErrorException.ExcType.PARAMETERS_ERROR,
          this.peekToken(0).getTokenLineNum(),
          "expected data type when parsing array type declaration, found "
              + this.peekToken(0).getValueString()
              + " -- "
              + this.peek(0));
    }
    return this.matchAndRemoveToken(peekRet).getTokenType();
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
    List<VariableNode> ret = new ArrayList<VariableNode>();
    Token.TokenType peekRet = this.peek(0);
    boolean changeable, isArray;
    isArray = false;
    List<Token> varNames;
    Token.TokenType varType;
    while (peekRet != Token.TokenType.RPAREN) {
      changeable = this.matchAndRemoveToken(Token.TokenType.VAR) != null;
      varNames = this.parseVarNames();
      peekRet = this.peek(0);
      if (!(peekRet == Token.TokenType.ARRAY || this.isTokenTypeDataType(peekRet))) {
        throw new SyntaxErrorException(
            SyntaxErrorException.ExcType.PARAMETERS_ERROR,
            this.peekToken(0).getTokenLineNum(),
            "expected variable type, found "
                + this.peekToken(0).getValueString()
                + " -- "
                + this.peek(0));
      }
      varType =
          peekRet == Token.TokenType.ARRAY
              ? this.parseArrayDecParams()
              : this.matchAndRemoveToken(peekRet).getTokenType();
      this.matchAndRemoveToken(Token.TokenType.SEMICOLON);
      if (peekRet == Token.TokenType.ARRAY) {
        isArray = true;
      }
      peekRet = this.peek(0);
      for (Token vn : varNames) {
        ret.add(
            new VariableNode(
                vn.getValueString(),
                this.getVarTypeFromTokenType(varType),
                changeable,
                -1,
                -1,
                isArray, this.peekToken(0).getTokenLineNum()));
      }
    }
    this.matchAndRemoveToken(Token.TokenType.RPAREN);
    return ret;
  }

  /**
   * Get the BooleanCompareNode.CompareType enum value that corresponds to the given TokenType.
   *
   * @param tt the given TokenType
   * @return the corresponding BooleanCompareNode.CompareType enum value
   */
  private BooleanCompareNode.CompareType getCompTypeFromTokenType(Token.TokenType tt) {
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
  private boolean isCompOp(Token.TokenType tt) {
    return tt == Token.TokenType.LESSTHAN
        || tt == Token.TokenType.GREATERTHAN
        || tt == Token.TokenType.LESSEQUAL
        || tt == Token.TokenType.GREATEREQUAL
        || tt == Token.TokenType.EQUALS
        || tt == Token.TokenType.NOTEQUAL;
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
    Token peekTokenRet = this.peekToken(0);
    if (peekTokenRet == null) {
      return expression1;
    }
    Token.TokenType peekRet = peekTokenRet.getTokenType();
    if (!this.isCompOp(peekRet)) {
      return expression1;
    }
    this.removeToken();
    BooleanCompareNode.CompareType compareType = this.getCompTypeFromTokenType(peekRet);
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
    Token nameToken = this.matchAndRemoveToken(Token.TokenType.IDENTIFIER);
    String name = nameToken.getValueString();
    Token.TokenType peekRet = this.peek(0);
    if (peekRet == Token.TokenType.LSQUARE) {
      this.matchAndRemoveToken(Token.TokenType.LSQUARE);
      VariableReferenceNode ret = new VariableReferenceNode(name, this.expression());
      if (this.matchAndRemoveToken(Token.TokenType.RSQUARE) == null) {
        throw new SyntaxErrorException(
            SyntaxErrorException.ExcType.IDX_EXP_ERROR,
            this.peekToken(0).getTokenLineNum(),
            "parseVarRef() expected Right Square Bracket, found " + this.peekToken(0));
      }
      return ret;
    }
    return new VariableReferenceNode(name, null);
  }

  /**
   * Process an assignment statement.
   *
   * @return an AssignmentNode instance
   */
  private AssignmentNode assignment() throws Exception {
    Token.TokenType peekRet = this.peek(0);
    if (peekRet != Token.TokenType.IDENTIFIER) {
      throw new SyntaxErrorException(
          SyntaxErrorException.ExcType.ASSIGNMENT_ERROR,
          this.peekToken(0).getTokenLineNum(),
          "expected identifier, found " + this.peekToken(0));
    }
    VariableReferenceNode leftSide = this.parseVarRef();
    if (this.matchAndRemoveToken(Token.TokenType.ASSIGN) == null) {
      throw new SyntaxErrorException(
          SyntaxErrorException.ExcType.ASSIGNMENT_ERROR, "assign token", this.peekToString());
    }
    Node rightSide = this.boolCompare();
    if (rightSide == null) {
      throw new SyntaxErrorException(
          SyntaxErrorException.ExcType.ASSIGNMENT_ERROR,
          this.peekToken(0).getTokenLineNum(),
          "expected valid right side of assignment, found " + this.peekToken(0));
    }
    return new AssignmentNode(leftSide, rightSide);
  }

  /**
   * Processes any single statement. For Parser 3, it should just call assignment() and return what
   * it returns.
   *
   * @return what assignment() returns if the next token is not DEDENT, null otherwise.
   */
  private StatementNode statement() throws Exception {
    Token.TokenType peekRet = this.peek(0);
    StatementNode ret;
    switch (peekRet) {
      case DEDENT:
        this.matchAndRemoveToken(Token.TokenType.DEDENT);
        return null;
      case FOR:
        ret = this.parseFor();
        break;
      case WHILE:
        ret = this.parseWhile();
        break;
      case REPEAT:
        ret = this.parseRepeat();
        break;
      case IF:
        ret = this.parseIf(Token.TokenType.IF);
        break;

      default:
        if (this.peekToken(0) == null) {
          return null;
        }
        int lineNum = this.peekToken(0).getTokenLineNum();
        int i = 1;
        boolean assignFound = false;
        while (this.peekToken(i) != null && this.peekToken(i).getTokenLineNum() == lineNum) {
          if (this.peek(i) == Token.TokenType.ASSIGN) {
            assignFound = true;
            break;
          }
          i++;
        }
        if (assignFound) {
          ret = this.assignment();
          this.expectsEndOfLine();
        } else {
          ret = this.functionCall();
        }
        break;
    }
    return ret;
  }

  /**
   * Expects indent, calls statement() repeatedly (until it returns null) and then expects dedent.
   * Returns a collection of StatementNode.
   *
   * @return an ArrayList of StatementNode instances.
   */
  private List<StatementNode> statements() throws Exception {
    var ret = new ArrayList<StatementNode>();
    if (this.matchAndRemoveToken(Token.TokenType.INDENT) == null) {
      throw new SyntaxErrorException(
          SyntaxErrorException.ExcType.STATEMENTS_ERROR, "indent", this.peekToString());
    }
    StatementNode sment = this.statement();
    while (sment != null) {
      ret.add(sment);
      sment = this.statement();
    }
    return ret;
  }

  /**
   * TODO: write docstring
   *
   * @param
   * @return
   */
  private ForNode parseFor() throws Exception {
    this.matchAndRemoveToken(Token.TokenType.FOR);
    if (this.peek(0) != Token.TokenType.IDENTIFIER) {
      throw new SyntaxErrorException(
          SyntaxErrorException.ExcType.FOR_ERROR, "variable reference", this.peekToString());
    }
    // TODO: Should this be a variable reference, since we wouldn't want it to have an
    // array subscript? Job for semantic analysis?
    VariableReferenceNode varRef = this.parseVarRef();
    if (this.matchAndRemoveToken(Token.TokenType.FROM) == null) {
      throw new SyntaxErrorException(
          SyntaxErrorException.ExcType.FOR_ERROR, "from", this.peekToString());
    }
    Node fromExp = this.expression();
    if (this.matchAndRemoveToken(Token.TokenType.TO) == null) {
      throw new SyntaxErrorException(
          SyntaxErrorException.ExcType.FOR_ERROR, "to", this.peekToString());
    }
    Node toExp = this.expression();
    this.expectsEndOfLine();
    return new ForNode(varRef, fromExp, toExp, this.statements());
  }

  /**
   * TODO: write docstring
   *
   * @param
   * @return
   */
  private IfNode parseIf(Token.TokenType ifOrElsifOrElse) throws Exception {
    this.matchAndRemoveToken(ifOrElsifOrElse);
    Node condition;
    List<StatementNode> statements;
    Token.TokenType peekRet;
    if (ifOrElsifOrElse != Token.TokenType.ELSE) {
      condition = this.boolCompare();
      // TODO: Test here if condition is null
      if (this.matchAndRemoveToken(Token.TokenType.THEN) == null) {
        throw new SyntaxErrorException(
            SyntaxErrorException.ExcType.IF_ERROR, "then", this.peekToString());
      }
      this.expectsEndOfLine();
      statements = this.statements();
      peekRet = this.peek(0);
      if (peekRet == Token.TokenType.ELSIF || peekRet == Token.TokenType.ELSE) {
        return new IfNode(condition, statements, this.parseIf(peekRet), ifOrElsifOrElse);
      } else {
        return new IfNode(condition, statements, ifOrElsifOrElse);
      }
    } else {
      this.expectsEndOfLine();
      statements = this.statements();
      return new IfNode(statements, ifOrElsifOrElse);
    }
  }

  /**
   * TODO: write docstring
   *
   * @param
   * @return
   */
  private WhileNode parseWhile() throws Exception {
    this.matchAndRemoveToken(Token.TokenType.WHILE);
    Node condition = this.boolCompare();
    this.expectsEndOfLine();
    return new WhileNode(condition, this.statements());
  }

  /**
   * TODO: write docstring
   *
   * @param
   * @return
   */
  private RepeatNode parseRepeat() throws Exception {
    this.matchAndRemoveToken(Token.TokenType.REPEAT);
    if (this.matchAndRemoveToken(Token.TokenType.UNTIL) == null) {
      throw new SyntaxErrorException(
          SyntaxErrorException.ExcType.REPEAT_ERROR, "until", this.peekToString());
    }
    Node condition = this.boolCompare();
    this.expectsEndOfLine();
    return new RepeatNode(condition, this.statements());
  }

  /**
   * TODO: write docstring
   *
   * @param
   * @return
   */
  public ParameterNode getTheParam() throws Exception {
    if (this.matchAndRemoveToken(Token.TokenType.VAR) != null) {
      return new ParameterNode(this.parseVarRef(), true);
    } else {
      return new ParameterNode(this.boolCompare(), false);
    }
  }

  /**
   * TODO: finish docstring
   *
   * @param
   * @return
   */
  private FunctionCallNode functionCall() throws Exception {
    Token funcName = this.matchAndRemoveToken(Token.TokenType.IDENTIFIER);
    if (funcName == null) {
      throw new SyntaxErrorException(
          SyntaxErrorException.ExcType.FUNC_CALL_ERROR, "function name", this.peekToString());
    }
    ParameterNode theParam = this.getTheParam();
    if (theParam.getVarParam() == null && theParam.getNonVarParam() == null) {
      this.expectsEndOfLine();
      return new FunctionCallNode(funcName.getValueString());
    }
    var theParams = new ArrayList<ParameterNode>();
    theParams.add(theParam);
    while (this.matchAndRemoveToken(Token.TokenType.COMMA) != null) {
      theParam = this.getTheParam();
      if (theParam.getVarParam() == null && theParam.getNonVarParam() == null) {
        throw new SyntaxErrorException(
            SyntaxErrorException.ExcType.FUNC_CALL_ERROR, "function call argument after comma",
            this.peekToString());
      }
      theParams.add(theParam);
    }
    this.expectsEndOfLine();
    return new FunctionCallNode(funcName.getValueString(), theParams);
  }
}
