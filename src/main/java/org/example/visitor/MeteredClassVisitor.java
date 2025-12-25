package org.example.visitor;

import org.example.model.JarMeterRegistry;
import org.objectweb.asm.*;

import static org.objectweb.asm.Opcodes.ASM8;

public class MeteredClassVisitor extends ClassVisitor {
    private JarMeterRegistry jarMeterRegistry;
    private String className;

    public MeteredClassVisitor(
            JarMeterRegistry jarMeterRegistry
    ) {
        super(ASM8);
        this.jarMeterRegistry = jarMeterRegistry;
    }

    public void visit(int version, int access, String name, String signature, String superName, String[] interfaces) {
        System.out.println("\n" + name + " extends " + superName + " {");
        className = name;
        jarMeterRegistry.addUV(name, superName);
        super.visit(version, access, name, signature, superName, interfaces);
    }

    public FieldVisitor visitField(int access, String name, String desc, String signature, Object value) {
        System.out.println(" " + desc + " " + name);
        jarMeterRegistry.trackFieldVisit(className);
        return super.visitField(access, name, desc, signature, value);
    }

    public void visitEnd() {
        System.out.println("}");
    }

    public MethodVisitor visitMethod(int access, String name, String desc, String signature, String[] exceptions) {
        System.out.println(" name: " + name + ", desc: " + desc);
        jarMeterRegistry.trackMethodVisit(className, name + desc);
        return new MeteredMethodVisitor(
                api,
                super.visitMethod(access, name, desc, signature, exceptions),
                jarMeterRegistry.getAbcMeterRegistry()
        );
    }

    public void visitSource(String source, String debug) {
    }

    public void visitOuterClass(String owner, String name, String desc) {
    }

    public AnnotationVisitor visitAnnotation(String desc, boolean visible) {
        return null;
    }

    public void visitAttribute(Attribute attr) {
    }

    public void visitInnerClass(String name, String outerName, String innerName, int access) {
    }
}

