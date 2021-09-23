package compiler.parser;

enum AST_NODE_TYPE {
  PROGRAM, VAR_DEF, VAR_ASSIGN,
  BIN_EXP, NUM_LITERAL, FUNC_CALL,
}
