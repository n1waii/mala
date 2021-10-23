package vm;

enum OP_NAME {
    OP_CONSTANT, ADD, HALT
}

interface op_handler {
  void run(MalaVM vm, int operand);
}

public class Instruction {
  private int op_code;
  private OP_NAME name;
  private op_handler handler;

  public Instruction(int op_code, OP_NAME name, op_handler handler) {
      this.op_code = op_code;
      this.name = name;
      this.handler = handler;
  }

  public int getOpCode() {
    return this.op_code;
  }

  public OP_NAME getOpName() {
    return this.name;
  }

  public void execute(MalaVM vm, int operand) {
      this.handler.run(vm, operand);
  }
}
