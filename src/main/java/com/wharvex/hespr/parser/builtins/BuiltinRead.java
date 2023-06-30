package com.wharvex.hespr.parser.builtins;

import java.util.List;
import com.wharvex.hespr.interpreter.InterpreterDataType;
import java.util.Scanner;

public class BuiltinRead extends BuiltinBase {

  public BuiltinRead() {
    super("Read", null);
  }

  @Override
  public void execute(List<InterpreterDataType> args) {
    Scanner s = new Scanner(System.in);
    System.out.println();
    for (InterpreterDataType arg : args) {
      arg.fromString(s.next());
    }
    s.close();
  }

  @Override
  public boolean isVariadic() {
    return true;
  }

  @Override
  public boolean variadicNeedsVar() {
    return true;
  }

  @Override
  public String toString() {
    return this.getName() + " {}\n";
  }
}
