package com.wharvex.shank.parser.builtins;

import com.wharvex.shank.interpreter.IntegerDataType;
import com.wharvex.shank.interpreter.InterpreterDataType;
import com.wharvex.shank.interpreter.StringDataType;
import com.wharvex.shank.parser.Parser;
import com.wharvex.shank.parser.VariableNode;
import com.wharvex.shank.semantic.SemanticErrorException;
import java.util.Arrays;
import java.util.List;

public class BuiltinLeft extends BuiltinBase {

  /* someString, length, var resultString */
  public BuiltinLeft() {
    super(
        "Left",
        Arrays.asList(
            new VariableNode("someString", VariableNode.VariableType.STRING, false, false, -1),
            new VariableNode("length", VariableNode.VariableType.INTEGER, false, false, -1),
            new VariableNode(
                "resultString", VariableNode.VariableType.STRING, true, false, -1)));
  }

  public void execute(List<InterpreterDataType> args) throws SemanticErrorException {
    StringDataType someStringDT = (StringDataType) args.get(0);
    int ssLen = someStringDT.getStoredVal().length();
    IntegerDataType resLenDT = (IntegerDataType) args.get(1);
    if (resLenDT.getStoredVal() < 0) {
      throw new SemanticErrorException("2nd arg to builtin left must be non-negative");
    }
    int resLen = Math.min(resLenDT.getStoredVal(), ssLen);
    StringDataType resultStringDT = (StringDataType) args.get(2);
    String resultString = someStringDT.getStoredVal().substring(0, resLen);
    resultStringDT.setStoredVal(resultString);
  }

  public String toString() {
    return this.getName() + " {\n  Params:\n    " + Parser.listToString(this.getParams()) + "\n}\n";
  }
}
