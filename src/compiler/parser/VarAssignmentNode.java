package compiler.parser;

import compiler.parser.IASTNode;

public class VarAssignmentNode implements IASTNode {
  private static AST_NODE_TYPE type = AST_NODE_TYPE.VAR_ASSIGN;
  private String value;
  private IASTNode left;
  private IASTNode right;

  public VarAssignmentNode(String varName, IASTNode varValue) {
    this.value = varName;
    this.right = varValue;
  }

  @Override
  public AST_NODE_TYPE getType() {
    return type;
  }

  @Override
  public String getValue() {
    return this.value;
  }

  public String getVariable() {
    return this.getValue();
  }

  @Override
  public IASTNode getLeftNode() {
    return this.left;
  }

  @Override
  public IASTNode getRightNode() {
    return this.right;
  }

  public String toString() {
    return String.format(
      "{%n  Type: %s,%n  Value: %s%n}%n",
      this.getType().name(), this.getValue()
    );
  }
}
