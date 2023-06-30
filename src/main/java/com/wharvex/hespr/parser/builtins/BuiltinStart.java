package com.wharvex.hespr.parser.builtins;

import com.wharvex.hespr.interpreter.ArrayDataType;
import com.wharvex.hespr.interpreter.IntegerDataType;
import com.wharvex.hespr.interpreter.InterpreterDataType;
import com.wharvex.hespr.parser.ParserHelper;
import com.wharvex.hespr.parser.nodes.VariableNode;
import com.wharvex.hespr.parser.VariableType;
import java.util.Arrays;
import java.util.List;

public class BuiltinStart extends BuiltinBase {

  /* start = the first index of this array */
  public BuiltinStart() {
    super(
        "Start",
        Arrays.asList(
            new VariableNode("someArray", VariableType.ANY, false, false, -1),
            new VariableNode("start", VariableType.INTEGER, true, false, -1)));
  }

  @Override
  public void execute(List<InterpreterDataType> args) {
    ((IntegerDataType) args.get(1)).setStoredVal(((ArrayDataType) args.get(0)).getFrom());
  }

  @Override
  public String toString() {
    return this.getName() + " {\n  Params:\n    " + ParserHelper.listToString(this.getParams()) + "\n}\n";
  }
}
