package parser;

import parser.AST_NODE_TYPE;

interface IASTNode {
  AST_NODE_TYPE getType();
  Object getValue();
  IASTNode getRightNode();
  IASTNode getLeftNode();
}
