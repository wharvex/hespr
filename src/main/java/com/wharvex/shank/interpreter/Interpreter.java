package com.wharvex.shank.interpreter;

import com.wharvex.shank.lexer.Token;
import com.wharvex.shank.parser.AssignmentNode;
import com.wharvex.shank.parser.BooleanCompareNode;
import com.wharvex.shank.parser.BooleanNode;
import com.wharvex.shank.parser.ForNode;
import com.wharvex.shank.parser.FunctionCallNode;
import com.wharvex.shank.parser.FunctionNode;
import com.wharvex.shank.parser.IfNode;
import com.wharvex.shank.parser.IntegerNode;
import com.wharvex.shank.parser.MathOpNode;
import com.wharvex.shank.parser.Node;
import com.wharvex.shank.parser.ParameterNode;
import com.wharvex.shank.parser.ProgramNode;
import com.wharvex.shank.parser.RealNode;
import com.wharvex.shank.parser.RepeatNode;
import com.wharvex.shank.parser.StatementNode;
import com.wharvex.shank.parser.StringNode;
import com.wharvex.shank.parser.VariableNode;
import com.wharvex.shank.parser.VariableReferenceNode;
import com.wharvex.shank.parser.WhileNode;
import com.wharvex.shank.parser.builtins.BuiltinBase;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Interpreter {

  private ProgramNode program;

  public Interpreter(ProgramNode program) {
    this.program = program;
  }

  public ProgramNode getProgram() {
    return program;
  }

  public void testFuncs() throws Exception {
    for (FunctionNode fn : this.getProgram().getFunctions().values()) {
      if (FunctionNode.class.equals(fn.getClass())) {
        this.interpretFunction(fn, new ArrayList<>());
      }
    }
  }

  public void startProgram() throws Exception {
    this.interpretFunction(this.getFunction("start"), new ArrayList<>());
  }

  private InterpreterDataType makeDataTypeFromVarNode(VariableNode v) {
    String val = v.getVal();
    InterpreterDataType ret;
    if (v.getIsArray()) {
      ret = new ArrayDataType(v.getType(), v.getFrom(), v.getTo());
      ret.setInitialized(true);
    } else {
      switch (v.getType()) {
        case STRING -> {
          ret = new StringDataType(val);
          ret.setInitialized(false);
        }
        case CHARACTER -> {
          ret = new CharacterDataType(val != null ? val.charAt(0) : '0');
          ret.setInitialized(false);
        }
        case INTEGER -> {
          ret = new IntegerDataType(val != null ? Integer.parseInt(val) : 0);
          ret.setInitialized(false);
        }
        case REAL -> {
          ret = new RealDataType(val != null ? Float.parseFloat(val) : 0);
          ret.setInitialized(false);
        }
        case BOOLEAN -> {
          // parseBoolean returns false if its arg is null
          ret = new BooleanDataType(Boolean.parseBoolean(val));
          ret.setInitialized(false);
        }
        default -> ret = null;
      }
    }
    return ret;
  }

  public void interpretFunction(FunctionNode f, List<InterpreterDataType> args) throws Exception {
    var vars = new HashMap<String, InterpreterDataType>();
    if (f.getParams() != null) {
      List<VariableNode> theParams = f.getParams();
      for (int i = 0; i < theParams.size(); i++) {
        InterpreterDataType theArg = args.get(i);
        VariableNode theParam = theParams.get(i);
        theArg.setIsChangeable(theParam.isChangeable());
        theArg.setInitialized(true);
        vars.put(theParam.getName(), theArg);
      }
    }
    if (f.getVariables() != null) {
      for (VariableNode v : f.getVariables()) {
        if (vars.containsKey(v.getName())) {
          throw new Exception("Cannot redeclare parameter " + v.getName());
        }
        InterpreterDataType idt = this.makeDataTypeFromVarNode(v);
        idt.setIsChangeable(true);
        idt.setIsVar(false);
        vars.put(v.getName(), idt);
      }
    }
    if (f.getConstants() != null) {
      // Constant declarations are not considered statements, so this does the job of the
      // "constantNodes" function mentioned in the Interpreter 2 assignment instructions
      for (VariableNode v : f.getConstants()) {
        if (vars.containsKey(v.getName())) {
          throw new Exception("Cannot redeclare parameter/variable " + v.getName());
        }
        InterpreterDataType idt = this.makeDataTypeFromVarNode(v);
        idt.setIsChangeable(false);
        idt.setIsVar(false);
        idt.setInitialized(true);
        vars.put(v.getName(), idt);
      }
    }
//    System.out.println("\n[[Begin " + f.getName() + "]]");
    this.interpretBlock(f.getStatements(), vars);
//    System.out.println("\n\n[[End " + f.getName() + "]]");
  }

  private void expectsInitialized(InterpreterDataType idt, String varName) throws Exception {
    if (!idt.isInitialized()) {
      throw new Exception("Variable referenced before initialization: " + varName);
    }
  }

  private IntegerDataType interpretArrIdxExp(VariableReferenceNode vrn,
      HashMap<String, InterpreterDataType> vars) throws Exception {
    InterpreterDataType arrIdt = vars.get(vrn.getName());
    if (!(arrIdt instanceof ArrayDataType)) {
      throw new Exception(
          "Cannot index variable " + vrn.getName() + " -- it is " + arrIdt.getClass());
    }
    InterpreterDataType arrIdxIdt = this.expression(vrn.getArrIdxExp(), vars);
    if (!(arrIdxIdt instanceof IntegerDataType)) {
      throw new Exception("Array " + vrn.getName() + " needs integer index expression");
    }
    return (IntegerDataType) arrIdxIdt;
  }

  private InterpreterDataType interpretVarRef(
      VariableReferenceNode vrn, HashMap<String, InterpreterDataType> vars) throws Exception {
    if (vars.containsKey(vrn.getName())) {
      return vars.get(vrn.getName());
    }
    throw new Exception("Variable referenced before declaration: " + vrn.getName());
  }

  private InterpreterDataType interpretVarRefExpectsInit(
      VariableReferenceNode vrn, HashMap<String, InterpreterDataType> vars) throws Exception {
    if (vars.containsKey(vrn.getName())) {
      InterpreterDataType ret = vars.get(vrn.getName());
//      this.expectsInitialized(ret, vrn.getName());
      if (vrn.getArrIdxExp() != null) {
        IntegerDataType arrIdxIdt = this.interpretArrIdxExp(vrn, vars);
        ArrayDataType arrIdt = (ArrayDataType) vars.get(vrn.getName());
        return arrIdt.getArrElm(arrIdxIdt.getStoredVal());
      }
      return ret;
    }
    throw new Exception("Variable referenced before declaration: " + vrn.getName());
  }

  private void expectsBool(Node condition, String expecter) throws Exception {
    if (!(condition instanceof BooleanCompareNode)) {
      throw new Exception(expecter + " needs Boolean condition.");
    }
  }

  private void interpretBlock(
      List<StatementNode> statements, HashMap<String, InterpreterDataType> vars) throws Exception {
    int i = 1;
    for (StatementNode statementNode : statements) {
      if (statementNode instanceof AssignmentNode) {
        VariableReferenceNode vrnLeft = ((AssignmentNode) statementNode).getLeftSide();
        InterpreterDataType idtLeft = this.interpretVarRef(vrnLeft, vars);
        String assigneeName = vrnLeft.getName();
//        System.out.print("\n[[Assignment " + (i++) + ": " + vrnLeft + " starts as " + idtLeft);
        InterpreterDataType idtRight = this.expression(
            ((AssignmentNode) statementNode).getRightSide(), vars);
        if (vrnLeft.getArrIdxExp() != null) {
          ((ArrayDataType) idtLeft).setArrElm(interpretArrIdxExp(vrnLeft, vars).getStoredVal(),
              idtRight);
        } else {
          idtLeft.setInitialized(true);
          this.transferStoredVal(idtRight, idtLeft);
        }
//        System.out.print(", ends as " + vars.get(assigneeName) + "]]");
      } else if (statementNode instanceof ForNode) {
        InterpreterDataType fromIDT = this.expression(((ForNode) statementNode).getFrom(), vars);
        InterpreterDataType toIDT = this.expression(((ForNode) statementNode).getTo(), vars);
        if (!(fromIDT instanceof IntegerDataType) || !(toIDT instanceof IntegerDataType)) {
          throw new Exception("From and To in a For Loop must evaluate to integers");
        }
        VariableReferenceNode varRef = ((ForNode) statementNode).getVarRef();
        InterpreterDataType varRefIDT = this.interpretVarRef(varRef, vars);
        varRefIDT.setInitialized(true);
//        System.out.println("\n\n[[Begin For Loop]]");
        for (int j = ((IntegerDataType) fromIDT).getStoredVal();
            j <= ((IntegerDataType) toIDT).getStoredVal();
            j++) {
          ((IntegerDataType) varRefIDT).setStoredVal(j);
          vars.put(varRef.getName(), varRefIDT);
          this.interpretBlock(((ForNode) statementNode).getStatements(), vars);
        }
//        System.out.println("\n\n[[End For Loop]]");
      } else if (statementNode instanceof WhileNode) {
        Node condition = ((WhileNode) statementNode).getCondition();
        this.expectsBool(condition, "While");
//        System.out.println("\n\n[[Begin While Loop " + condition + "]]");
        while (this.booleanCompare((BooleanCompareNode) condition, vars)) {
          this.interpretBlock(((WhileNode) statementNode).getStatements(), vars);
        }
//        System.out.println("\n\n[[End While Loop " + condition + "]]");
      } else if (statementNode instanceof RepeatNode) {
        Node condition = ((RepeatNode) statementNode).getCondition();
        this.expectsBool(condition, "RepeatUntil");
//        System.out.println("\n\n[[Begin RepeatUntil Loop " + condition + "]]");
        while (!this.booleanCompare((BooleanCompareNode) condition, vars)) {
          this.interpretBlock(((RepeatNode) statementNode).getStatements(), vars);
        }
//        System.out.println("\n\n[[End RepeatUntil Loop " + condition + "]]");
      } else if (statementNode instanceof IfNode) {
        Node condition = ((IfNode) statementNode).getCondition();
        this.expectsBool(condition, "If");
//        System.out.println("\n\n[[Begin If Statement " + condition + "]]");
        if (this.booleanCompare((BooleanCompareNode) condition, vars)) {
          this.interpretBlock(((IfNode) statementNode).getStatements(), vars);
        } else {
          IfNode nextIf = ((IfNode) statementNode).getNextIf();
          while (nextIf != null) {
            if (nextIf.getIfOrElsifOrElse() == Token.TokenType.ELSE) {
              this.interpretBlock(nextIf.getStatements(), vars);
              break;
            } else {
              condition = nextIf.getCondition();
              this.expectsBool(condition, "If");
              if (this.booleanCompare((BooleanCompareNode) condition, vars)) {
                this.interpretBlock(nextIf.getStatements(), vars);
                break;
              } else {
                nextIf = nextIf.getNextIf();
              }
            }
          }
        }
      } else if (statementNode instanceof FunctionCallNode) {
        FunctionNode functionNode = this.getFunction(
            ((FunctionCallNode) statementNode).getFuncName());
        var newVars = new ArrayList<InterpreterDataType>();
        List<ParameterNode> args = ((FunctionCallNode) statementNode).getArgs();
        // If the below condition passes, then fn is a variadic Builtin (e.g. Write)
        // because only Builtins can be variadic
        if (functionNode.isVariadic()) {
          InterpreterDataType newVar;
          this.builtinExpectsArgs(args);
          for (ParameterNode parameterNode : args) {
            if (((BuiltinBase) functionNode).variadicNeedsVar() && !parameterNode.isVar()) {
              throw new Exception(functionNode.getName() + " must be called with var arguments");
            }
            newVar = this.expression(parameterNode.getParam(), vars);
            newVar.setIsVar(parameterNode.isVar());
            newVars.add(newVar);
          }
        } else {
          InterpreterDataType newVar;
          if (functionNode instanceof BuiltinBase) {
            this.builtinExpectsArgs(args);
          }
          List<VariableNode> funcParams = functionNode.getParams();
          if (funcParams.size() != args.size()) {
            throw new Exception(
                functionNode.getName() + " needs " + funcParams.size() + " arguments");
          }
          for (int j = 0; j < args.size(); j++) {
            if (funcParams.get(j).isChangeable() && !args.get(j).isVar()) {
              throw new Exception(
                  "Argument " + j + " to function " + functionNode.getName() + " must be var");
            }
            newVar = this.expression(args.get(j).getParam(), vars);
            typeCheckArg(funcParams.get(j), newVar, functionNode.getName(), j);
            newVar.setIsVar(args.get(j).isVar());
            newVars.add(newVar);
          }
        }
        if (functionNode instanceof BuiltinBase) {
          ((BuiltinBase) functionNode).execute(newVars);
        } else {
          this.interpretFunction(functionNode, newVars);
        }
        for (int j = 0; j < newVars.size(); j++) {
          if (newVars.get(j).getIsVar() && args.get(j).isVar()) {
            vars.put(((VariableReferenceNode) args.get(j).getParam()).getName(), newVars.get(j));
          }
        }
      }
    }
  }

  private void transferStoredVal(InterpreterDataType src, InterpreterDataType dest) {
    if (src instanceof CharacterDataType) {
      ((CharacterDataType) dest).setStoredVal(((CharacterDataType) src).getStoredVal());
    } else if (src instanceof IntegerDataType) {
      ((IntegerDataType) dest).setStoredVal(((IntegerDataType) src).getStoredVal());
    } else if (src instanceof BooleanDataType) {
      ((BooleanDataType) dest).setStoredVal(((BooleanDataType) src).getStoredVal());
    } else if (src instanceof StringDataType) {
      ((StringDataType) dest).setStoredVal(((StringDataType) src).getStoredVal());
    } else if (src instanceof RealDataType) {
      ((RealDataType) dest).setStoredVal(((RealDataType) src).getStoredVal());
    }
  }

  private void typeCheckArg(VariableNode theParam, InterpreterDataType theArg, String funcName,
      int argPos) throws Exception {
    switch (theParam.getType()) {
      case STRING -> {
        if (!(theArg instanceof StringDataType)) {
          throw new Exception(
              "Argument " + argPos + " of function " + funcName + " must be of type STRING");
        }
      }
      case CHARACTER -> {
        if (!(theArg instanceof CharacterDataType)) {
          throw new Exception(
              "Argument " + argPos + " of function " + funcName + " must be of type CHARACTER");
        }
      }
      case INTEGER -> {
        if (!(theArg instanceof IntegerDataType)) {
          throw new Exception(
              "Argument " + argPos + " of function " + funcName + " must be of type INTEGER");
        }
      }
      case REAL -> {
        if (!(theArg instanceof RealDataType)) {
          throw new Exception(
              "Argument " + argPos + " of function " + funcName + " must be of type REAL");
        }
      }
      case BOOLEAN -> {
        if (!(theArg instanceof BooleanDataType)) {
          throw new Exception(
              "Argument " + argPos + " of function " + funcName + " must be of type BOOLEAN");
        }
      }
      case ANY -> {
      }
    }
  }

  private void builtinExpectsArgs(List<ParameterNode> args) throws Exception {
    if (args == null) {
      throw new Exception("Builtin functions must be called with at least one argument");
    }
  }

  private FunctionNode getFunction(String funcName) throws Exception {
    if (!this.getProgram().getFunctions().containsKey(funcName)) {
      throw new Exception("Unknown function name " + (funcName));
    }
    return this.getProgram().getFunctions().get(funcName);
  }

  /**
   * expression() looks at the node types that could be on the left or right side of a MathOpNode
   * (MathOpNode, VariableReferenceNode, IntegerNode, FloatNode, etc.) and returns an IDT which will
   * be one of three: IntegerDataType, RealDataType or StringDataType (because we can add two
   * strings). If we call expression() on left and right, we now have a limited set of
   * possibilities. Both sides have to be the same type. If they are, add, subtract, multiply,
   * divide, modulo the values and return a new IDT with the result.
   */
  private InterpreterDataType expression(Node n, HashMap<String, InterpreterDataType> vars)
      throws Exception {
    if (!(n instanceof MathOpNode)) {
      if (n instanceof VariableReferenceNode) {
        return this.interpretVarRefExpectsInit((VariableReferenceNode) n, vars);
      } else if (n instanceof IntegerNode) {
        return new IntegerDataType(((IntegerNode) n).getVal());
      } else if (n instanceof RealNode) {
        return new RealDataType(((RealNode) n).getVal());
      } else if (n instanceof StringNode) {
        return new StringDataType(((StringNode) n).getVal());
      } else if (n instanceof BooleanNode) {
        return new BooleanDataType(((BooleanNode) n).getVal());
      }
    } else {
      InterpreterDataType leftSide = this.expression(((MathOpNode) n).getLeftSide(), vars);
      InterpreterDataType rightSide = this.expression(((MathOpNode) n).getRightSide(), vars);
      if (leftSide instanceof BooleanDataType || rightSide instanceof BooleanDataType) {
        throw new Exception("Math operations not allowed on booleans");
      }
      if (!leftSide.getClass().equals(rightSide.getClass()) && !(leftSide instanceof StringDataType)
          && !(rightSide instanceof StringDataType)) {
        throw new Exception(
            "Math operations not allowed on different data types (except strings for conversion)");
      }
      if ((leftSide instanceof StringDataType || rightSide instanceof StringDataType)
          && ((MathOpNode) n).getMathOpType() != MathOpNode.MathOpType.ADD) {
        throw new Exception("Math operations other than ADD not allowed on string data types");
      }
      if (leftSide instanceof StringDataType || rightSide instanceof StringDataType) {
        return new StringDataType(leftSide.toString() + rightSide.toString());
      } else {
        if (leftSide instanceof IntegerDataType) {
          int leftInt = ((IntegerDataType) leftSide).getStoredVal();
          int rightInt = ((IntegerDataType) rightSide).getStoredVal();
          switch (((MathOpNode) n).getMathOpType()) {
            case ADD -> {
              return new IntegerDataType(leftInt + rightInt);
            }
            case SUBTRACT -> {
              return new IntegerDataType(leftInt - rightInt);
            }
            case MULTIPLY -> {
              return new IntegerDataType(leftInt * rightInt);
            }
            case DIVIDE -> {
              return new IntegerDataType(leftInt / rightInt);
            }
            case MOD -> {
              return new IntegerDataType(leftInt % rightInt);
            }
            default -> {
            }
          }
        } else {
          float leftReal = ((RealDataType) leftSide).getStoredVal();
          float rightReal = ((RealDataType) rightSide).getStoredVal();
          switch (((MathOpNode) n).getMathOpType()) {
            case ADD -> {
              return new RealDataType(leftReal + rightReal);
            }
            case SUBTRACT -> {
              return new RealDataType(leftReal - rightReal);
            }
            case MULTIPLY -> {
              return new RealDataType(leftReal * rightReal);
            }
            case DIVIDE -> {
              return new RealDataType(leftReal / rightReal);
            }
            case MOD -> {
              return new RealDataType(leftReal % rightReal);
            }
            default -> {
            }
          }
        }
      }
    }
    return new IntegerDataType(0);
  }

  /**
   * @param bcn
   * @param vars
   * @return
   * @throws Exception
   */
  private boolean booleanCompare(BooleanCompareNode bcn, HashMap<String, InterpreterDataType> vars)
      throws Exception {
    InterpreterDataType leftSide = this.expression(bcn.getLeftSide(), vars);
    InterpreterDataType rightSide = this.expression(bcn.getRightSide(), vars);
    if (!leftSide.getClass().equals(rightSide.getClass())) {
      throw new Exception("Boolean operations not allowed on different data types");
    }
    if (leftSide instanceof BooleanDataType || leftSide instanceof StringDataType) {
      throw new Exception("Boolean operations not allowed on booleans or strings");
    }

    if (leftSide instanceof IntegerDataType) {
      int leftInt = ((IntegerDataType) leftSide).getStoredVal();
      int rightInt = ((IntegerDataType) rightSide).getStoredVal();
      switch (bcn.getCompareType()) {
        case LESSTHAN -> {
          return leftInt < rightInt;
        }
        case GREATERTHAN -> {
          return leftInt > rightInt;
        }
        case GREATEREQUAL -> {
          return leftInt >= rightInt;
        }
        case LESSEQUAL -> {
          return leftInt <= rightInt;
        }
        case EQUALS -> {
          return leftInt == rightInt;
        }
        default -> {
        }
      }
    } else {
      float leftReal = ((RealDataType) leftSide).getStoredVal();
      float rightReal = ((RealDataType) rightSide).getStoredVal();
      switch (bcn.getCompareType()) {
        case LESSTHAN -> {
          return leftReal < rightReal;
        }
        case GREATERTHAN -> {
          return leftReal > rightReal;
        }
        case GREATEREQUAL -> {
          return leftReal >= rightReal;
        }
        case LESSEQUAL -> {
          return leftReal <= rightReal;
        }
        case EQUALS -> {
          return leftReal == rightReal;
        }
        default -> {
        }
      }
    }
    return false;
  }
}
