package runtime;

import java.util.HashMap;
import java.util.Stack;
import java.util.ArrayList;
import compiler.parser.*;

public class Interpreter {
	public ProgramNode root;
	public HashMap<Integer, HashMap<String, IASTNode>> scopes;
	public int currentScope;

	public Interpreter(ProgramNode programAST) {
		this.root = programAST;
		this.scopes = new HashMap<Integer, HashMap<String, IASTNode>>();
		this.currentScope = 0;
	}

  private NumLiteralNode interpret_bin_exp(BinExpNode node) {
		IASTNode left = node.getLeftNode();
		IASTNode right = node.getRightNode();

    switch (node.getValue()) {
      case "+":
        return new NumLiteralNode((double) visit(left).getValue() + (double) visit(right).getValue());
      case "-":
        return new NumLiteralNode((double) visit(left).getValue() - (double) visit(right).getValue());
      case "/":
        return new NumLiteralNode((double) visit(left).getValue() / (double) visit(right).getValue());
      case "*":
        return new NumLiteralNode((double) visit(left).getValue() * (double) visit(right).getValue());
			default: 
				return new NumLiteralNode(0.0); // added to prevent error for now
    }
  }

  private LocalVarDefNode interpret_local_var_def(LocalVarDefNode varDefNode) {
		HashMap<String, IASTNode> scope = get_scope(this.currentScope);
		scope.put(varDefNode.getVariable(), visit(varDefNode.getRightNode()));
		return new LocalVarDefNode(varDefNode.getVariable(), visit(varDefNode.getRightNode()));
	}

	private FunctionCallNode interpret_function_call(FunctionCallNode functionCallNode) {
		switch (functionCallNode.getFunctionName()) {
			case "print":
				global_function_print(functionCallNode.getArguments());
			default:
				return functionCallNode;
		}
	}

  private IASTNode visit(IASTNode node) {
		switch (node.getType()) {
				case LOCAL_VAR_DEF:
					return interpret_local_var_def((LocalVarDefNode) node);
				case BIN_EXP:
					return interpret_bin_exp((BinExpNode) node);
				case NUM_LITERAL:
					return (NumLiteralNode) node;
				default:
					return node;
    }
  }

	private void global_function_print(ArrayList<IASTNode> args) {
		for (IASTNode node : args) {
			switch (node.getType()) {
				case BIN_EXP:
					System.out.print(visit(node).getValue());
				case NUM_LITERAL:
					System.out.print(node.getValue()); 
			}
		}
		System.out.println();
	}

	private void set_scope_level(int newIndex) {
		this.currentScope = newIndex;
	}

	private int get_scope_level() {
		return this.currentScope;
	}

	private void new_scope(int index) {
		this.scopes.put(index, new HashMap<String, IASTNode>());
	}

	private HashMap<String, IASTNode> get_scope(int index) {
		return this.scopes.get(index);
	}

  public void interpret() {
		new_scope(this.currentScope);
		for (IASTNode node : this.root.getValue()) {
			switch (node.getType()) {
				case LOCAL_VAR_DEF:
					interpret_local_var_def((LocalVarDefNode) node);
			}
		}
  }

}
