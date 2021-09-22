package parser;

import parser.IASTNode;

public abstract class NumLiteralNode implements IASTNode {
  private static AST_NODE_TYPE type = AST_NODE_TYPE.NUM_LITERAL;
  private Integer value;

  public NumLiteralNode(Integer value) {
    this.value = value;
  }

  @Override
  public AST_NODE_TYPE getType() {
    return type;
  }

  @Override
  public Integer getValue() {
    return this.value;
  }

  public String toString() {
    return String.format(
      "{%nType: %s,%nValue: %s%n}%n",
      this.getType().name(), this.getValue()
    );
  }
}
