package com.wharvex.shank.parser;

import com.wharvex.shank.SyntaxErrorException;
import com.wharvex.shank.lexer.Token;
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
import java.util.List;
import java.util.stream.Collectors;

public class Parser {

  private List<Token> tokens;

  /**
   * Constructor
   *
   * @param tokens the tokens list
   */
  public Parser(List<Token> tokens) {
    this.tokens = tokens;
  }

  /**
   * Check if tokens is not empty
   *
   * @return true if not empty, false otherwise
   */
  public boolean tokensNotEmpty() {
    return this.tokens.size() != 0;
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
    return this.peekToken(0) != null ? this.peekToken(0).toString() : "NULL";
  }

  /**
   * Checks if a TokenType is the next token in tokens and removes it if it is.
   *
   * @param tokenType the TokenType to match in tokens and remove if present
   * @return the Token if removed, null otherwise
   */
  private Token matchAndRemove(Token.TokenType tokenType) {
    return this.tokensNotEmpty() && this.tokens.get(0).getTokenType() == tokenType
        ? this.tokens.remove(0)
        : null;
  }

  /**
   * Checks if the next token in tokens is an ENDOFLINE token and throws an error if it isn't.
   * Removes the token if it is and continues removing ENDOFLINE tokens if they are next in tokens.
   */
  private void expectsEndOfLine() throws Exception {
    Token endOfLine = this.matchAndRemove(Token.TokenType.ENDOFLINE);
    if (endOfLine == null) {
      throw new SyntaxErrorException(
          SyntaxErrorException.ExcType.EOL_ERROR, "end of line", this.peekToString());
    }
    Token.TokenType peekRet = this.peek(0);
    while (peekRet == Token.TokenType.ENDOFLINE) {
      this.matchAndRemove(peekRet);
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
      func = this.function();
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
    Token.TokenType peekRet1 = this.peek(0);
    switch (peekRet1) {
      case PLUS -> mathOpType = MathOpNode.MathOpType.ADD;
      case MINUS -> mathOpType = MathOpNode.MathOpType.SUBTRACT;
      default -> {
        if (!this.isTokenTypeTermOp(peekRet1)) {
          return term1;
        }
        return new MathOpNode(
            this.getMathOpTypeFromTokenType(this.matchAndRemove(peekRet1).getTokenType()),
            term1,
            this.expression());
      }
    }
    // Eat the expOp
    this.matchAndRemove(peekRet1);

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
      return new MathOpNode(mathOpType, term1, this.expression());
    } else if (this.isTokenTypeExpOp(peekRet2)) {
      return new MathOpNode(mathOpType, term1, this.expression());
    }
    return new MathOpNode(mathOpType, term1, this.factor());
  }

  /**
   * Parse a term (times, divide, mod operations)
   *
   * @return the MathOpNode or more basic Node that represents the term
   */
  private Node term() throws Exception {
    Node factor1;
    Token termOp;
    MathOpNode.MathOpType mathOpType;

    // Find factor and term operator

    // TODO: make this a switch statement
    factor1 = this.factor();
    termOp = this.matchAndRemove(Token.TokenType.TIMES);
    if (termOp == null) {
      termOp = this.matchAndRemove(Token.TokenType.MOD);
      if (termOp == null) {
        termOp = this.matchAndRemove(Token.TokenType.DIVIDE);
        if (termOp == null) {
          return factor1;
        } else {
          mathOpType = MathOpNode.MathOpType.DIVIDE;
        }
      } else {
        mathOpType = MathOpNode.MathOpType.MOD;
      }
    } else {
      mathOpType = MathOpNode.MathOpType.MULTIPLY;
    }

    // Peek and return

    Token.TokenType peekRet1 = this.peek(0);
    if (!this.isTokenTypeFactor(peekRet1)) {
      throw new SyntaxErrorException(
          SyntaxErrorException.ExcType.TERM_ERROR, "second factor", this.peekToString());
    }
    if (peekRet1 == Token.TokenType.MINUS && !this.isTokenTypeNumber(this.peek(1))) {
      throw new SyntaxErrorException(
          SyntaxErrorException.ExcType.TERM_ERROR, "second factor", "minus " + this.peekToString());
    }
    Token.TokenType peekRet2 = this.peek(1);
    if (this.isTokenTypeNumber(peekRet2) && peekRet1 != Token.TokenType.LPAREN) {
      peekRet2 = this.peek(2);
    }
    int rParenPos = this.findRParen();
    if (rParenPos > 0) {
      peekRet2 = this.peek(rParenPos + 1);
    }
    if (this.isTokenTypeTermOp(peekRet2)) {
      return new MathOpNode(mathOpType, factor1, this.term());
    }
    return new MathOpNode(mathOpType, factor1, this.factor());
  }

  /**
   * FACTOR = {-} number or lparen EXPRESSION rparen or variableReferenceNode (IDENTIFIER)
   *
   * @return the FACTOR
   */
  private Node factor() throws Exception {
    String minusOrEmpty = this.matchAndRemove(Token.TokenType.MINUS) != null ? "-" : "";
    Token.TokenType peekRet = this.peek(0);
    switch (peekRet) {
      case NUMBER:
        return new IntegerNode(
            Integer.parseInt(minusOrEmpty + this.matchAndRemove(peekRet).getValueString()));
      case NUMBER_DECIMAL:
        return new RealNode(
            Float.parseFloat(minusOrEmpty + this.matchAndRemove(peekRet).getValueString()));
      case IDENTIFIER:
        return this.parseVarRef();
      case LPAREN:
        this.matchAndRemove(peekRet);
        Node expRet = this.expression();
        if (expRet == null) {
          throw new SyntaxErrorException(
              SyntaxErrorException.ExcType.FACTOR_ERROR, "expression", this.peekToString());
        }
        if (this.matchAndRemove(Token.TokenType.RPAREN) == null) {
          throw new SyntaxErrorException(
              SyntaxErrorException.ExcType.FACTOR_ERROR, "right paren", this.peekToString());
        }
        return expRet;
      case TRUE:
        this.matchAndRemove(Token.TokenType.TRUE);
        return new BooleanNode(true);
      case FALSE:
        this.matchAndRemove(Token.TokenType.FALSE);
        return new BooleanNode(false);
      case STRINGLITERAL:
        return new StringNode(this.matchAndRemove(peekRet).getValueString());
      default:
        return null;
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
   * function() processes a function. It expects a define token. Then an identifier (the name). Then
   * a left paren. Then a list of 0 or more variable declarations. Then a right paren. Then an
   * endOfLine. Then constants and variables (arbitrary number). Then an indent. Then statements.
   * Then a dedent. It returns a FunctionNode or null.
   *
   * <p>After the signature, constants, and variables, function() should expect indent, then (for
   * Parser 2) call expression() until it returns null and print the resultant expressions (just to
   * make sure that the parsing is still correct) then expect dedent
   *
   * <p>TODO: break this up into smaller functions
   *
   * <p>TODO: constant section should be optional, but parser gives an error if it does not appear.
   * (According to grader's comments. I could not reproduce this error.)
   *
   * @return FunctionNode or null if no define TOKEN is found
   */
  private FunctionNode function() throws Exception {
    Token define = this.matchAndRemove(Token.TokenType.DEFINE);
    if (define == null) {
      return null;
    }
    Token funcName = this.matchAndRemove(Token.TokenType.IDENTIFIER);
    if (funcName == null) {
      throw new SyntaxErrorException(
          SyntaxErrorException.ExcType.FUNCTION_ERROR, "function name", this.peekToString());
    }
    Token lparen = this.matchAndRemove(Token.TokenType.LPAREN);
    if (lparen == null) {
      throw new SyntaxErrorException(
          SyntaxErrorException.ExcType.FUNCTION_ERROR, "left paren", this.peekToString());
    }
    int rParenPos = this.findRParen();
    if (rParenPos == -1) {
      throw new SyntaxErrorException(
          SyntaxErrorException.ExcType.FUNCTION_ERROR, "right paren", this.peekToString());
    }
    List<VariableNode> paramVars = this.parameterDeclarations();
    this.expectsEndOfLine();
    Token.TokenType peekRet = this.peek(0);
    if (!(peekRet == Token.TokenType.VARIABLES
        || peekRet == Token.TokenType.CONSTANTS
        || peekRet == Token.TokenType.INDENT)) {
      throw new SyntaxErrorException(
          SyntaxErrorException.ExcType.FUNCTION_ERROR,
          "variables, constants or indent",
          this.peekToString());
    }

    // DECLARATIONS

    List<Token> varNamesVariables;
    int lineNum;
    ArrayDeclaration arrayDec;
    VariableRange varRange = null;
    int from, to;
    boolean isArray = false;
    float realFrom, realTo;
    realFrom = realTo = -1;
    VariableNode.VariableType varType;
    List<VariableNode> variableVars = new ArrayList<VariableNode>();
    List<VariableNode> constVars = new ArrayList<VariableNode>();
    List<ConstantDeclaration> constDecs = new ArrayList<ConstantDeclaration>();
    VariableNode constVar;
    // Declarations parsing loop
    while (peekRet != Token.TokenType.INDENT) {
      lineNum = this.peekToken(0).getTokenLineNum();

      // VARIABLES

      if (peekRet == Token.TokenType.VARIABLES) {
        this.matchAndRemove(Token.TokenType.VARIABLES);
        varNamesVariables = this.parseVarNames();
        peekRet = this.peek(0);
        if (!(peekRet == Token.TokenType.ARRAY || this.isTokenTypeDataType(peekRet))) {
          throw new SyntaxErrorException(
              SyntaxErrorException.ExcType.FUNCTION_ERROR,
              lineNum,
              "expected data type in variables declaration, found " + this.peekToken(0));
        }
        if (peekRet == Token.TokenType.ARRAY) {
          isArray = true;
          arrayDec = this.parseArrayDec();
          varType = arrayDec.getType();
          from = arrayDec.getFrom();
          to = arrayDec.getTo();
        } else {
          varType = this.getVarTypeFromTokenType(peekRet);
          this.matchAndRemove(peekRet);
          if (this.peek(0) == Token.TokenType.FROM) {
            if (peekRet != Token.TokenType.STRING
                && peekRet != Token.TokenType.REAL
                && peekRet != Token.TokenType.INTEGER) {
              throw new Exception(
                  "Variable range not supported for type "
                      + peekRet
                      + " -- line "
                      + this.peekToken(0).getTokenLineNum());
            }
            varRange = this.parseVarRange(peekRet);
            from = varRange.getFrom();
            to = varRange.getTo();
            realFrom = varRange.getRealFrom();
            realTo = varRange.getRealTo();
          } else {
            from = to = -1;
          }
        }
        for (Token vn : varNamesVariables) {
          variableVars.add(
              new VariableNode(
                  vn.getValueString(), varType, true, from, to, isArray, realFrom, realTo));
        }

        // CONSTANTS

      } else {
        // Match and remove CONSTANTS token
        this.matchAndRemove(peekRet);
        do {
          constDecs.add(this.parseConstant());
          peekRet = this.peek(0);
          if (peekRet == Token.TokenType.COMMA) {
            this.matchAndRemove(peekRet);
            peekRet = this.peek(0);
          }
        } while (peekRet != Token.TokenType.ENDOFLINE);
        for (ConstantDeclaration cd : constDecs) {
          constVar = new VariableNode(cd.getName(), cd.getType(), false, -1, -1, false);
          constVar.setVal(cd.getVal());
          constVars.add(constVar);
        }
      }
      this.expectsEndOfLine();
      peekRet = this.peek(0);
    }

    // FUNCTION BODY

    return new FunctionNode(
        funcName.getValueString(), paramVars, variableVars, constVars, this.statements());
  }

  /**
   * TODO: write docstring
   *
   * @param
   * @return
   */
  private ConstantDeclaration parseConstant() throws SyntaxErrorException {
    Token name = this.matchAndRemove(Token.TokenType.IDENTIFIER);
    if (name == null) {
      throw new SyntaxErrorException(
          SyntaxErrorException.ExcType.FUNCTION_ERROR,
          this.peekToken(0).getTokenLineNum(),
          "found constants but no name");
    }
    if (this.matchAndRemove(Token.TokenType.EQUALS) == null) {
      throw new SyntaxErrorException(
          SyntaxErrorException.ExcType.FUNCTION_ERROR,
          this.peekToken(0).getTokenLineNum(),
          "found constants then name then not equals");
    }
    Token.TokenType peekRet = this.peek(0);
    VariableNode.VariableType varType;
    String minusIfPresent = "";
    switch (peekRet) {
      case NUMBER:
        varType = VariableNode.VariableType.INTEGER;
        break;
      case NUMBER_DECIMAL:
        varType = VariableNode.VariableType.REAL;
        break;
      case MINUS:
        if (this.peek(1) == Token.TokenType.NUMBER) {
          varType = VariableNode.VariableType.INTEGER;
        } else if (this.peek(1) == Token.TokenType.NUMBER_DECIMAL) {
          varType = VariableNode.VariableType.REAL;
        } else {
          throw new SyntaxErrorException(
              SyntaxErrorException.ExcType.FUNCTION_ERROR,
              this.peekToken(0).getTokenLineNum(),
              "found invalid constants declaration");
        }
        minusIfPresent = "-";
        break;
      case STRINGLITERAL:
        varType = VariableNode.VariableType.STRING;
        break;
      case CHARACTERLITERAL:
        varType = VariableNode.VariableType.CHARACTER;
        break;
      case TRUE:
        varType = VariableNode.VariableType.BOOLEAN;
        break;
      case FALSE:
        varType = VariableNode.VariableType.BOOLEAN;
        break;

      default:
        throw new SyntaxErrorException(
            SyntaxErrorException.ExcType.FUNCTION_ERROR,
            this.peekToken(0).getTokenLineNum(),
            "found invalid constants declaration");
    }
    return (new ConstantDeclaration(
        name.getValueString(),
        varType,
        minusIfPresent + this.matchAndRemove(peekRet).getValueString()));
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
    Token varNameToken = this.matchAndRemove(Token.TokenType.IDENTIFIER);
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
      this.matchAndRemove(Token.TokenType.COMMA);
      varNameToken = this.matchAndRemove(Token.TokenType.IDENTIFIER);
      if (varNameToken == null) {
        throw new SyntaxErrorException(
            SyntaxErrorException.ExcType.PARAMETERS_ERROR,
            this.peekToken(0).getTokenLineNum(),
            "found variable name identifier then comma then not variable name identifier");
      }
      varNameTokens.add(varNameToken);
      commaOrColon = this.peek(0);
    }
    this.matchAndRemove(Token.TokenType.COLON);
    return varNameTokens;
  }

  /**
   * Processes an array declaration outside parameters (has from and to)
   *
   * @return an ArrayDeclaration instance representing the declaration
   */
  private ArrayDeclaration parseArrayDec() throws SyntaxErrorException {
    this.matchAndRemove(Token.TokenType.ARRAY);
    if (this.matchAndRemove(Token.TokenType.FROM) == null) {
      throw new SyntaxErrorException(
          SyntaxErrorException.ExcType.VARIABLES_ERROR,
          this.peekToken(0).getTokenLineNum(),
          "found incomplete array type declaration");
    }
    int from, to;
    Token fromNumToken = this.matchAndRemove(Token.TokenType.NUMBER);
    if (fromNumToken == null) {
      throw new SyntaxErrorException(
          SyntaxErrorException.ExcType.VARIABLES_ERROR,
          this.peekToken(0).getTokenLineNum(),
          "found incomplete array type declaration");
    }
    from = Integer.parseInt(fromNumToken.getValueString());
    if (this.matchAndRemove(Token.TokenType.TO) == null) {
      throw new SyntaxErrorException(
          SyntaxErrorException.ExcType.VARIABLES_ERROR,
          this.peekToken(0).getTokenLineNum(),
          "found incomplete array type declaration");
    }
    Token toNumToken = this.matchAndRemove(Token.TokenType.NUMBER);
    if (toNumToken == null) {
      throw new SyntaxErrorException(
          SyntaxErrorException.ExcType.VARIABLES_ERROR,
          this.peekToken(0).getTokenLineNum(),
          "found incomplete array type declaration");
    }
    to = Integer.parseInt(toNumToken.getValueString());
    if (this.matchAndRemove(Token.TokenType.OF) == null) {
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
    this.matchAndRemove(peekRet);
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
    this.matchAndRemove(Token.TokenType.FROM);
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
    this.matchAndRemove(peekRet);
    if (this.matchAndRemove(Token.TokenType.TO) == null) {
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
    this.matchAndRemove(peekRet);
    return new VariableRange(from, to, realFrom, realTo);
  }

  /**
   * Processes array declarations in parameters (no from or to)
   *
   * <p>TODO: finish docstring
   *
   * @param
   * @return
   */
  private Token.TokenType parseArrayDecParams() throws SyntaxErrorException {
    this.matchAndRemove(Token.TokenType.ARRAY);
    if (this.matchAndRemove(Token.TokenType.OF) == null) {
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
    return this.matchAndRemove(peekRet).getTokenType();
  }

  /**
   * Processes the parameters and returns a collection of VariableNode to function().
   *
   * <p>(var) identifier comma identifier...
   *
   * <p>colon type (semicolon or right paren)
   *
   * <p>TODO: finish docstring
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
      changeable = this.matchAndRemove(Token.TokenType.VAR) == null ? false : true;
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
              : this.matchAndRemove(peekRet).getTokenType();
      this.matchAndRemove(Token.TokenType.SEMICOLON);
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
                isArray));
      }
    }
    this.matchAndRemove(Token.TokenType.RPAREN);
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
      if (ct.toString() == tt.toString()) {
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
    Node expression1;
    Token compOp;
    BooleanCompareNode.CompareType compareType;

    expression1 = this.expression();
    Token.TokenType peekRet = this.peek(0);
    if (!this.isCompOp(peekRet)) {
      return expression1;
    }
    compOp = this.matchAndRemove(peekRet);
    compareType = this.getCompTypeFromTokenType(compOp.getTokenType());
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
    Token nameToken = this.matchAndRemove(Token.TokenType.IDENTIFIER);
    String name = nameToken.getValueString();
    Token.TokenType peekRet = this.peek(0);
    if (peekRet == Token.TokenType.LSQUARE) {
      this.matchAndRemove(Token.TokenType.LSQUARE);
      VariableReferenceNode ret = new VariableReferenceNode(name, this.expression());
      if (this.matchAndRemove(Token.TokenType.RSQUARE) == null) {
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
    if (this.matchAndRemove(Token.TokenType.ASSIGN) == null) {
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
        this.matchAndRemove(Token.TokenType.DEDENT);
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
    if (this.matchAndRemove(Token.TokenType.INDENT) == null) {
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
    this.matchAndRemove(Token.TokenType.FOR);
    if (this.peek(0) != Token.TokenType.IDENTIFIER) {
      throw new SyntaxErrorException(
          SyntaxErrorException.ExcType.FOR_ERROR, "variable reference", this.peekToString());
    }
    // TODO: Should this be a variable reference, since we wouldn't want it to have an
    // array subscript? Job for semantic analysis?
    VariableReferenceNode varRef = this.parseVarRef();
    if (this.matchAndRemove(Token.TokenType.FROM) == null) {
      throw new SyntaxErrorException(
          SyntaxErrorException.ExcType.FOR_ERROR, "from", this.peekToString());
    }
    Node fromExp = this.expression();
    if (this.matchAndRemove(Token.TokenType.TO) == null) {
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
    this.matchAndRemove(ifOrElsifOrElse);
    Node condition;
    List<StatementNode> statements;
    Token.TokenType peekRet;
    if (ifOrElsifOrElse != Token.TokenType.ELSE) {
      condition = this.boolCompare();
      // TODO: Test here if condition is null
      if (this.matchAndRemove(Token.TokenType.THEN) == null) {
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
    this.matchAndRemove(Token.TokenType.WHILE);
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
    this.matchAndRemove(Token.TokenType.REPEAT);
    if (this.matchAndRemove(Token.TokenType.UNTIL) == null) {
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
    if (this.matchAndRemove(Token.TokenType.VAR) != null) {
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
    Token funcName = this.matchAndRemove(Token.TokenType.IDENTIFIER);
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
    while (this.matchAndRemove(Token.TokenType.COMMA) != null) {
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
