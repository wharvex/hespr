package com.wharvex.shank.parser.builtins;

import static org.junit.Assert.*;

import com.wharvex.shank.interpreter.ArrayDataType;
import com.wharvex.shank.interpreter.IntegerDataType;
import com.wharvex.shank.parser.VariableType;
import java.util.Arrays;
import org.junit.Test;

public class BuiltinEndTest {

  @Test
  public void execute() {
    var end = new BuiltinEnd();
    var endRet = new IntegerDataType(0);
    end.execute(Arrays.asList(new ArrayDataType(VariableType.INTEGER, 0, 1), endRet));
    assertEquals(1, endRet.getStoredVal());
  }
}