package com.wharvex.hespr;

import com.wharvex.hespr.interpreter.Interpreter;
import com.wharvex.hespr.lexer.Lexer;
import com.wharvex.hespr.parser.Parser;
import com.wharvex.hespr.parser.nodes.ProgramNode;
import com.wharvex.hespr.semantic.SemanticAnalysis;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

public class Hespr {

  public static void main(String[] args) throws Exception {
    if (args.length != 1) {
      throw new Exception(
          "Please call the program with exactly one argument (the input filename).");
    }
    Path myPath = Paths.get(args[0]);
    List<String> lines = Files.readAllLines(myPath, StandardCharsets.UTF_8);
    Lexer lexer = new Lexer(lines.size());
    try {
      for (String line : lines) {
        lexer.lex(line);
      }
      System.out.println("\nLEXER OUTPUT:\n");
      lexer.printTokens();
      Parser parser = new Parser(lexer.stateMachine.tokens);
      System.out.println("\nPARSER OUTPUT:\n");
      ProgramNode program = parser.parse();
      SemanticAnalysis sa = new SemanticAnalysis(program);
      sa.checkAssignments();
      Interpreter interpreter = new Interpreter(program);
      System.out.println("\nINTERPRETER OUTPUT\n");
      interpreter.startProgram();
    } catch (Exception e) {
      System.out.println(e.getMessage());
      e.printStackTrace();
    }
  }
}
