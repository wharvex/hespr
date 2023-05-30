package com.wharvex.shank.parser.builtins;

import com.wharvex.shank.interpreter.IntegerDataType;
import com.wharvex.shank.interpreter.InterpreterDataType;
import com.wharvex.shank.interpreter.StringDataType;
import com.wharvex.shank.parser.Parser;
import com.wharvex.shank.parser.VariableNode;
import java.util.Arrays;
import java.util.List;

public class BuiltinSubstring extends BuiltinBase {

  /* someString, index, length, var resultString */
  public BuiltinSubstring() {
    super(
        "Substring",
        Arrays.asList(
            new VariableNode("someString", VariableNode.VariableType.STRING, false, -1, -1, false),
            new VariableNode("index", VariableNode.VariableType.INTEGER, false, -1, -1, false),
            new VariableNode("length", VariableNode.VariableType.INTEGER, false, -1, -1, false),
            new VariableNode(
                "resultString", VariableNode.VariableType.STRING, true, -1, -1, false)));
  }

  @Override
  public void execute(List<InterpreterDataType> args) {
    String someString = ((StringDataType) args.get(0)).getStoredVal();
    int idx = ((IntegerDataType) args.get(1)).getStoredVal();
    int len = ((IntegerDataType) args.get(2)).getStoredVal();
    StringDataType result = (StringDataType) args.get(3);
    result.setStoredVal(someString.substring(idx, idx + len));
  }

  @Override
  public String toString() {
    return this.getName() + " {\n  Params:\n    " + Parser.listToString(this.getParams()) + "\n}\n";
  }
}
