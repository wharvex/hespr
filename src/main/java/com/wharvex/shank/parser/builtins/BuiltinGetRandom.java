package com.wharvex.shank.parser.builtins;

import com.wharvex.shank.interpreter.IntegerDataType;
import com.wharvex.shank.interpreter.InterpreterDataType;
import com.wharvex.shank.parser.Parser;
import com.wharvex.shank.parser.VariableNode;
import com.wharvex.shank.parser.VariableNode.VariableType;
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
    return this.getName() + " {\n  Params:\n    " + Parser.listToString(this.getParams()) + "\n}\n";
  }
}
