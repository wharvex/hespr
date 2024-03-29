package com.wharvex.hespr.parser.builtins;

import com.wharvex.hespr.interpreter.IntegerDataType;
import com.wharvex.hespr.interpreter.InterpreterDataType;
import com.wharvex.hespr.interpreter.StringDataType;
import com.wharvex.hespr.parser.ParserHelper;
import com.wharvex.hespr.parser.nodes.VariableNode;
import com.wharvex.hespr.parser.VariableType;
import java.util.Arrays;
import java.util.List;

public class BuiltinSubstring extends BuiltinBase {

  /* someString, index, length, var resultString */
  public BuiltinSubstring() {
    super(
        "Substring",
        Arrays.asList(
            new VariableNode("someString", VariableType.STRING, false, false, -1),
            new VariableNode("index", VariableType.INTEGER, false, false, -1),
            new VariableNode("length", VariableType.INTEGER, false, false, -1),
            new VariableNode(
                "resultString", VariableType.STRING, true, false, -1)));
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
    return this.getName() + " {\n  Params:\n    " + ParserHelper.listToString(this.getParams()) + "\n}\n";
  }
}
