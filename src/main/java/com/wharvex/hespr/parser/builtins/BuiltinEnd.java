package com.wharvex.hespr.parser.builtins;

import com.wharvex.hespr.interpreter.ArrayDataType;
import com.wharvex.hespr.interpreter.IntegerDataType;
import com.wharvex.hespr.interpreter.InterpreterDataType;
import com.wharvex.hespr.parser.ParserHelper;
import com.wharvex.hespr.parser.nodes.VariableNode;
import com.wharvex.hespr.parser.VariableType;
import java.util.Arrays;
import java.util.List;

public class BuiltinEnd extends BuiltinBase {

  /* end = the last index of this array */
  public BuiltinEnd() {
    super(
        "End",
        Arrays.asList(
            new VariableNode("someArray", VariableType.ANY, false, true, -1),
            new VariableNode("end", VariableType.INTEGER, true, false, -1)));
  }

  public void execute(List<InterpreterDataType> args) {
    ((IntegerDataType) args.get(1)).setStoredVal(((ArrayDataType) args.get(0)).getTo());
  }

  public String toString() {
    return this.getName() + " {\n  Params:\n    " + ParserHelper.listToString(this.getParams()) + "\n}\n";
  }
}
