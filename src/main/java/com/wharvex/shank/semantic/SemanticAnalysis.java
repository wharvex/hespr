package com.wharvex.shank.semantic;

import com.wharvex.shank.parser.AssignmentNode;
import com.wharvex.shank.parser.BooleanNode;
import com.wharvex.shank.parser.ForNode;
import com.wharvex.shank.parser.FunctionNode;
import com.wharvex.shank.parser.IfNode;
import com.wharvex.shank.parser.IntegerNode;
import com.wharvex.shank.parser.MathOpNode;
import com.wharvex.shank.parser.Node;
import com.wharvex.shank.parser.ProgramNode;
import com.wharvex.shank.parser.RealNode;
import com.wharvex.shank.parser.RepeatNode;
import com.wharvex.shank.parser.StatementNode;
import com.wharvex.shank.parser.StringNode;
import com.wharvex.shank.parser.VariableNode;
import com.wharvex.shank.parser.VariableReferenceNode;
import com.wharvex.shank.parser.WhileNode;
import java.util.HashMap;
import java.util.List;

public class SemanticAnalysis {

  private ProgramNode program;

  public SemanticAnalysis(ProgramNode program) {
    this.program = program;
  }

  public ProgramNode getProgram() {
    return program;
  }

  public void checkAssignments() throws Exception {
    for (FunctionNode functionNode : this.getProgram().getFunctions().values()) {
      var varTypes = new HashMap<String, VariableNode.VariableType>();
      if (functionNode.getParams() != null) {
        for (VariableNode variableNode : functionNode.getParams()) {
          varTypes.put(variableNode.getName(), variableNode.getType());
        }
      }
      if (functionNode.getVariables() != null) {
        for (VariableNode variableNode : functionNode.getVariables()) {
          varTypes.put(variableNode.getName(), variableNode.getType());
        }
      }
      if (functionNode.getConstants() != null) {
        for (VariableNode variableNode : functionNode.getConstants()) {
          varTypes.put(variableNode.getName(), variableNode.getType());
        }
      }
      this.analyzeBlock(varTypes, functionNode.getStatements(), functionNode.getName());
    }
  }

  private void analyzeBlock(HashMap<String, VariableNode.VariableType> varTypes,
      List<StatementNode> statements, String funcName)
      throws Exception {
    if (statements == null) {
      return;
    }
    for (StatementNode statement : statements) {
      if (statement instanceof AssignmentNode) {
        String leftSideName = ((AssignmentNode) statement).getLeftSide().getName();
        VariableNode.VariableType leftSideType = varTypes.get(leftSideName);
        Node rightSide = ((AssignmentNode) statement).getRightSide();
        VariableNode.VariableType rightSideType = this.expression(rightSide, varTypes);
        if (rightSideType != leftSideType && rightSideType != VariableNode.VariableType.ANY
            && leftSideType != VariableNode.VariableType.ANY) {
          throw new Exception(
              "\nSEMANTIC ANALYSIS ERROR\nWhen assigning to variable <" + leftSideName
                  + "> in function <"
                  + funcName
                  + ">\nRight side of assignment must be of type " + leftSideType + " but it is "
                  + rightSideType);
        }
      } else if (statement instanceof ForNode) {
        this.analyzeBlock(varTypes, ((ForNode) statement).getStatements(), funcName);
      } else if (statement instanceof WhileNode) {
        this.analyzeBlock(varTypes, ((WhileNode) statement).getStatements(), funcName);
      } else if (statement instanceof RepeatNode) {
        this.analyzeBlock(varTypes, ((RepeatNode) statement).getStatements(), funcName);
      } else if (statement instanceof IfNode) {
        this.analyzeBlock(varTypes, ((IfNode) statement).getStatements(), funcName);
        IfNode nextIf = ((IfNode) statement).getNextIf();
        while (nextIf != null) {
          this.analyzeBlock(varTypes, nextIf.getStatements(), funcName);
          nextIf = nextIf.getNextIf();
        }
      }
    }
  }

  private VariableNode.VariableType expression(Node node,
      HashMap<String, VariableNode.VariableType> varTypes) throws Exception {
    if (!(node instanceof MathOpNode)) {
      if (node instanceof VariableReferenceNode) {
        return varTypes.get(((VariableReferenceNode) node).getName());
      } else if (node instanceof IntegerNode) {
        return VariableNode.VariableType.INTEGER;
      } else if (node instanceof RealNode) {
        return VariableNode.VariableType.REAL;
      } else if (node instanceof StringNode) {
        return VariableNode.VariableType.STRING;
      } else if (node instanceof BooleanNode) {
        return VariableNode.VariableType.BOOLEAN;
      }
    } else {
      VariableNode.VariableType mathOpLeftSideType = this.expression(
          ((MathOpNode) node).getLeftSide(), varTypes);
      VariableNode.VariableType mathOpRightSideType = this.expression(
          ((MathOpNode) node).getRightSide(), varTypes);
      if (mathOpLeftSideType == VariableNode.VariableType.STRING
          || mathOpRightSideType == VariableNode.VariableType.STRING) {
        return VariableNode.VariableType.STRING;
      } else {
        return mathOpRightSideType;
      }
    }
    return VariableNode.VariableType.ANY;
  }
}
