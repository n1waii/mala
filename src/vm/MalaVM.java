package vm;

public class MalaVM {
  private BytecodeStream stream;
  private InstructionTable instructionTable;
  private int streamIndex = 0;

  public MalaVM(BytecodeStream stream, InstructionTable instructionTable) {
    this.stream = stream;
    this.instructionTable = instructionTable;
  }

  public void step() {
    int bytecode = this.stream.getBytecode(streamIndex);
    Instruction instruction = this.instructionTable.getInstruction(bytecode);

    if (instruction == null) {
      new Error("Expected instruction operation code. Got bytecode '" + bytecode + "' instead");
    }

    switch (instruction.getOpName()) {
      case OP_CONSTANT:
        break;

      case ADD:
        break;

      case HALT:
        break;
    }
  }
}
