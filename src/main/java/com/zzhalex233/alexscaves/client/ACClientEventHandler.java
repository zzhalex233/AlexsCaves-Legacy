package com.zzhalex233.alexscaves.client;

import com.zzhalex233.alexscaves.AlexsCaves;
import com.zzhalex233.alexscaves.client.render.item.RaygunRenderHelper;
import com.zzhalex233.alexscaves.server.item.ACItemRegistry;
import com.zzhalex233.alexscaves.server.item.ShotGumItem;

import net.minecraft.client.Minecraft;
import net.minecraft.client.model.ModelBiped;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.client.renderer.entity.RenderPlayer;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumHandSide;
import net.minecraftforge.client.event.RenderPlayerEvent;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.lwjgl.input.Keyboard;

@SideOnly(Side.CLIENT)
@Mod.EventBusSubscriber(modid = AlexsCaves.MODID, value = Side.CLIENT)
public class ACClientEventHandler {
    public static final KeyBinding SUBMARINE_FLOODLIGHTS = new KeyBinding("key.alexscaves.submarine_floodlights", Keyboard.KEY_L, "key.categories.alexscaves");

    public static void registerKeyBindings() {
        ClientRegistry.registerKeyBinding(SUBMARINE_FLOODLIGHTS);
    }

    @SubscribeEvent
    public static void onRenderPlayerPre(RenderPlayerEvent.Pre event) {
        EntityPlayer player = event.getEntityPlayer();
        ModelBiped model = event.getRenderer().getMainModel();
        if (isUprightShotGum(player.getHeldItemMainhand())) {
            poseShotGum(model, player.getPrimaryHand() == EnumHandSide.RIGHT);
        }
        if (isUprightShotGum(player.getHeldItemOffhand())) {
            poseShotGum(model, player.getPrimaryHand() == EnumHandSide.LEFT);
        }
    }

    @SubscribeEvent
    public static void onRenderWorldLast(RenderWorldLastEvent event) {
        RaygunRenderHelper.renderWorldRays(Minecraft.getMinecraft().world, event.getPartialTicks());
    }

    private static boolean isUprightShotGum(ItemStack stack) {
        return stack.getItem() == ACItemRegistry.SHOT_GUM.item() && ShotGumItem.shouldBeHeldUpright(stack);
    }

    private static void poseShotGum(ModelBiped model, boolean rightHand) {
        if (rightHand) {
            model.bipedRightArm.rotateAngleX = model.bipedHead.rotateAngleX - (float) Math.toRadians(70.0F);
            model.bipedRightArm.rotateAngleY = model.bipedHead.rotateAngleY;
            model.bipedRightArm.rotateAngleZ = 0.0F;
            model.bipedLeftArm.rotateAngleX = model.bipedHead.rotateAngleX - (float) Math.toRadians(70.0F);
            model.bipedLeftArm.rotateAngleY = model.bipedHead.rotateAngleY + (float) Math.toRadians(40.0F);
            model.bipedLeftArm.rotateAngleZ = (float) Math.toRadians(20.0F);
        } else {
            model.bipedLeftArm.rotateAngleX = model.bipedHead.rotateAngleX - (float) Math.toRadians(70.0F);
            model.bipedLeftArm.rotateAngleY = model.bipedHead.rotateAngleY;
            model.bipedLeftArm.rotateAngleZ = 0.0F;
            model.bipedRightArm.rotateAngleX = model.bipedHead.rotateAngleX - (float) Math.toRadians(70.0F);
            model.bipedRightArm.rotateAngleY = model.bipedHead.rotateAngleY - (float) Math.toRadians(40.0F);
            model.bipedRightArm.rotateAngleZ = -(float) Math.toRadians(20.0F);
        }
    }
}
