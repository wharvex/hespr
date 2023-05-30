package com.wharvex.shank.parser.builtins;

import com.wharvex.shank.interpreter.IntegerDataType;
import com.wharvex.shank.interpreter.InterpreterDataType;
import com.wharvex.shank.interpreter.StringDataType;
import com.wharvex.shank.parser.Parser;
import com.wharvex.shank.parser.VariableNode;
import java.util.Arrays;
import java.util.List;

public class BuiltinLeft extends BuiltinBase {

  /* someString, length, var resultString */
  public BuiltinLeft() {
    super(
        "Left",
        Arrays.asList(
            new VariableNode("someString", VariableNode.VariableType.STRING, false, -1, -1, false),
            new VariableNode("length", VariableNode.VariableType.INTEGER, false, -1, -1, false),
            new VariableNode(
                "resultString", VariableNode.VariableType.STRING, true, -1, -1, false)));
  }

  public void execute(List<InterpreterDataType> args) {
    ((StringDataType) args.get(2)).setStoredVal(((StringDataType) args.get(0)).getStoredVal()
        .substring(0, ((IntegerDataType) args.get(1)).getStoredVal()));
  }

  @Override
  public boolean isVariadic() {
    return false;
  }

  public String toString() {
    return this.getName() + " {\n  Params:\n    " + Parser.listToString(this.getParams()) + "\n}\n";
  }
}
