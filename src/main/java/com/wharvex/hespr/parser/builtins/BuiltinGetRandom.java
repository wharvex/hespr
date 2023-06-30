package com.wharvex.hespr.parser.builtins;

import com.wharvex.hespr.interpreter.IntegerDataType;
import com.wharvex.hespr.interpreter.InterpreterDataType;
import com.wharvex.hespr.parser.ParserHelper;
import com.wharvex.hespr.parser.nodes.VariableNode;
import com.wharvex.hespr.parser.VariableType;
import java.util.Random;
import java.util.List;

public class BuiltinGetRandom extends BuiltinBase {

  /* var resultInteger */
  public BuiltinGetRandom() {
    super(
        "GetRandom",
        List.of(
            new VariableNode(
                "resultInteger", VariableType.INTEGER, true, false, -1)));
  }

  public void execute(List<InterpreterDataType> args) {
    Random r = new Random();
    ((IntegerDataType) args.get(0)).setStoredVal(r.nextInt());
  }

  public String toString() {
    return this.getName() + " {\n  Params:\n    " + ParserHelper.listToString(this.getParams()) + "\n}\n";
  }
}
