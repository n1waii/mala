package compiler.parser;

import compiler.parser.IASTNode;
import compiler.parser.AST_NODE_TYPE;

public class BinExpNode implements IASTNode {
  private static AST_NODE_TYPE type = AST_NODE_TYPE.BIN_EXP;
  private String value;
  private IASTNode left;
  private IASTNode right;

  public BinExpNode(String value, IASTNode right, IASTNode left) {
    this.value = value;
    this.left = left;
    this.right = right;
  }

  @Override
  public AST_NODE_TYPE getType() {
    return type;
  }

  @Override
  public String getValue() {
    return this.value;
  }

  @Override
  public IASTNode getLeftNode() {
    return this.left;
  }

  @Override
  public IASTNode getRightNode() {
    return this.right;
  }

  public Object getOperator() {
    return this.getValue();
  }

  public String toString() {
    return String.format(
      "{%n  Type: %s,%n  Operator: %s,%n  Left: %s,%n  Right: %s%n}%n",
      this.getType().name(), this.getValue(), this.getLeftNode(), this.getRightNode()
    );
  }
}
