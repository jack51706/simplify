package org.cf.smalivm.op_handler;

import org.cf.smalivm.context.MethodContext;
import org.jf.dexlib2.iface.instruction.Instruction;
import org.jf.dexlib2.iface.instruction.OffsetInstruction;
import org.jf.dexlib2.iface.instruction.formats.Instruction31t;

public class FillArrayDataOp extends Op {

    static FillArrayDataOp create(Instruction instruction, int address) {
        String opName = instruction.getOpcode().name;
        int returnAddress = address + instruction.getCodeUnits();
        int branchOffset = ((OffsetInstruction) instruction).getCodeOffset();
        int childAddress = address + branchOffset;

        Instruction31t instr = (Instruction31t) instruction;
        int register = instr.getRegisterA();

        return new FillArrayDataOp(address, opName, childAddress, returnAddress, register);
    }

    private final int register;
    private final int returnAddress;

    private FillArrayDataOp(int address, String opName, int childAddress, int returnAddress, int register) {
        super(address, opName, childAddress);

        this.returnAddress = returnAddress;
        this.register = register;
    }

    @Override
    public int[] execute(MethodContext mctx) {
        Object value = mctx.readRegister(register);

        // Payload handler will look at its parent (this op) and determine the
        // target register by looking at what's assigned here.
        mctx.assignRegister(register, value);

        // But it still needs to know the return address when it's done.
        mctx.setPseudoInstructionReturnAddress(returnAddress);

        return getPossibleChildren();
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder(getOpName());

        sb.append(" r").append(register).append(", #").append(getPossibleChildren()[0]);

        return sb.toString();
    }

}