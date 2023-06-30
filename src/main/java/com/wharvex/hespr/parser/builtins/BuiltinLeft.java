package com.wharvex.hespr.parser.builtins;

import com.wharvex.hespr.interpreter.IntegerDataType;
import com.wharvex.hespr.interpreter.InterpreterDataType;
import com.wharvex.hespr.interpreter.StringDataType;
import com.wharvex.hespr.parser.ParserHelper;
import com.wharvex.hespr.parser.nodes.VariableNode;
import com.wharvex.hespr.parser.VariableType;
import com.wharvex.hespr.semantic.SemanticErrorException;
import java.util.Arrays;
import java.util.List;

public class BuiltinLeft extends BuiltinBase {

  /* someString, length, var resultString */
  public BuiltinLeft() {
    super(
        "Left",
        Arrays.asList(
            new VariableNode("someString", VariableType.STRING, false, false, -1),
            new VariableNode("length", VariableType.INTEGER, false, false, -1),
            new VariableNode(
                "resultString", VariableType.STRING, true, false, -1)));
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
    return this.getName() + " {\n  Params:\n    " + ParserHelper.listToString(this.getParams()) + "\n}\n";
  }
}
