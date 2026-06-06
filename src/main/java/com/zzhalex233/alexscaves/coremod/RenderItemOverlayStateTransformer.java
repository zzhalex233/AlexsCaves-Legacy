package com.zzhalex233.alexscaves.coremod;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;

import net.minecraft.launchwrapper.IClassTransformer;

public final class RenderItemOverlayStateTransformer implements IClassTransformer, Opcodes {
    private static final String RENDER_ITEM_CLASS = "net.minecraft.client.renderer.RenderItem";
    private static final String HOOK_OWNER = "com/zzhalex233/alexscaves/coremod/hooks/ItemOverlayStateHooks";
    private static final String HOOK_DESC = "()V";

    @Override
    public byte[] transform(String name, String transformedName, byte[] basicClass) {
        if (basicClass == null || !RENDER_ITEM_CLASS.equals(transformedName)) {
            return basicClass;
        }

        ClassNode classNode = new ClassNode();
        new ClassReader(basicClass).accept(classNode, 0);

        boolean transformed = false;
        for (MethodNode method : classNode.methods) {
            if (isRenderItemOverlayIntoGui(method)) {
                transformed |= injectOverlayStateHooks(method);
            }
        }

        if (!transformed) {
            return basicClass;
        }
        ClassWriter writer = new ClassWriter(ClassWriter.COMPUTE_MAXS);
        classNode.accept(writer);
        return writer.toByteArray();
    }

    private static boolean isRenderItemOverlayIntoGui(MethodNode method) {
        return ("renderItemOverlayIntoGUI".equals(method.name) || "func_180453_a".equals(method.name))
            && "(Lnet/minecraft/client/gui/FontRenderer;Lnet/minecraft/item/ItemStack;IILjava/lang/String;)V".equals(method.desc);
    }

    private static boolean injectOverlayStateHooks(MethodNode method) {
        method.instructions.insert(createHook("beforeRenderItemOverlayIntoGUI"));
        for (AbstractInsnNode node = method.instructions.getFirst(); node != null; node = node.getNext()) {
            if (node.getOpcode() == RETURN) {
                method.instructions.insertBefore(node, createHook("afterRenderItemOverlayIntoGUI"));
            }
        }
        return true;
    }

    private static InsnList createHook(String name) {
        InsnList instructions = new InsnList();
        instructions.add(new MethodInsnNode(INVOKESTATIC, HOOK_OWNER, name, HOOK_DESC, false));
        return instructions;
    }
}
