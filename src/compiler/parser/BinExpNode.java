package parser;

import parser.IASTNode;
import parser.IASTNodeOp;

public class BinExpNode implements IASTNodeOp {
  private static String type = "BinaryExpressionNode";
  private String operator;
  private IASTNode left;
  private IASTNode right;

  public BinExpNode(String operator, IASTNode right, IASTNode left) {
    this.operator = operator;
    this.left = left;
    this.right = right;
  }

  @Override
  public String getType() {
    return type;
  }

  @Override
  public String getOperator() {
    return this.operator;
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
      "{%nType: %s,%nOperator: %s,%nLeft: %s,%nRight: %s%n}%n",
      this.getType(), this.getOperator(), this.getLeftNode(), this.getRightNode()
    );
  }
}
