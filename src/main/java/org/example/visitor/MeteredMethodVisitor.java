package org.example.visitor;

import org.example.model.AbcMeterRegistry;
import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

public class MeteredMethodVisitor extends MethodVisitor {
    private AbcMeterRegistry abcMeterRegistry;

    protected MeteredMethodVisitor(int api, MethodVisitor methodVisitor, AbcMeterRegistry abcMeterRegistry) {
        super(api, methodVisitor);
        this.abcMeterRegistry = abcMeterRegistry;
    }

    @Override
    public void visitVarInsn(int opcode, int varIndex) {
        // A - Assignment
        if (opcode >= Opcodes.ISTORE && opcode <= Opcodes.ASTORE) {
            abcMeterRegistry.trackAssignment();
        }
    }

    @Override
    public void visitJumpInsn(int opcode, Label label) {
        abcMeterRegistry.trackBranch();

        if ((opcode >= Opcodes.IFEQ && opcode <= Opcodes.IF_ACMPNE)
                || opcode == Opcodes.IFNULL
                || opcode == Opcodes.IFNONNULL) {

            abcMeterRegistry.trackCondition();
        }
    }
}
