package com.wharvex.shank.parser.builtins;

import static org.junit.Assert.*;

import com.wharvex.shank.interpreter.IntegerDataType;
import com.wharvex.shank.interpreter.RealDataType;
import java.util.Arrays;
import org.junit.Test;

public class BuiltinIntegerToRealTest {

  @Test
  public void execute() {
    var intToReal = new BuiltinIntegerToReal();
    var intToRealRet = new RealDataType(0);
    intToReal.execute(Arrays.asList(new IntegerDataType(1), intToRealRet));
    assertEquals(1.0, intToRealRet.getStoredVal(), 0);
  }
}