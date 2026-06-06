package com.zzhalex233.alexscaves.client;

import com.zzhalex233.alexscaves.client.gui.book.CaveBookScreen;
import com.zzhalex233.alexscaves.client.gui.NuclearFurnaceGui;
import com.zzhalex233.alexscaves.client.particle.AmberMonolithParticle;
import com.zzhalex233.alexscaves.client.particle.MagneticTrailParticle;
import com.zzhalex233.alexscaves.client.particle.NuclearSirenSonarParticle;
import com.zzhalex233.alexscaves.client.particle.QuarryBorderLightningParticle;
import com.zzhalex233.alexscaves.client.particle.TeslaBulbLightningParticle;
import com.zzhalex233.alexscaves.client.sound.HologramProjectorSound;
import com.zzhalex233.alexscaves.client.sound.GalenaGauntletSound;
import com.zzhalex233.alexscaves.client.sound.MagnetSound;
import com.zzhalex233.alexscaves.client.sound.NuclearFurnaceSound;
import com.zzhalex233.alexscaves.client.sound.NuclearSirenSound;
import com.zzhalex233.alexscaves.client.sound.QuarrySmasherSound;
import com.zzhalex233.alexscaves.client.sound.RaygunSound;
import com.zzhalex233.alexscaves.client.sound.ResistorShieldSound;
import com.zzhalex233.alexscaves.client.sound.SubmarineSound;
import com.zzhalex233.alexscaves.client.render.entity.BurrowingArrowRenderer;
import com.zzhalex233.alexscaves.client.render.entity.AlexsCavesBoatRenderer;
import com.zzhalex233.alexscaves.client.render.entity.BrainiacRenderer;
import com.zzhalex233.alexscaves.client.render.entity.BoundroidRenderer;
import com.zzhalex233.alexscaves.client.render.entity.BoundroidWinchRenderer;
import com.zzhalex233.alexscaves.client.render.entity.CaniacRenderer;
import com.zzhalex233.alexscaves.client.render.entity.CandicornRenderer;
import com.zzhalex233.alexscaves.client.render.entity.CandyCaneHookRenderer;
import com.zzhalex233.alexscaves.client.render.entity.CaramelCubeRenderer;
import com.zzhalex233.alexscaves.client.render.entity.CorrodentRenderer;
import com.zzhalex233.alexscaves.client.render.entity.CorrodentTailRenderer;
import com.zzhalex233.alexscaves.client.render.entity.DarkArrowRenderer;
import com.zzhalex233.alexscaves.client.render.entity.DeepOneKnightRenderer;
import com.zzhalex233.alexscaves.client.render.entity.DeepOneMageRenderer;
import com.zzhalex233.alexscaves.client.render.entity.DeepOneRenderer;
import com.zzhalex233.alexscaves.client.render.entity.DesolateDaggerRenderer;
import com.zzhalex233.alexscaves.client.render.entity.DinosaurSpiritRenderer;
import com.zzhalex233.alexscaves.client.render.entity.ExtinctionSpearRenderer;
import com.zzhalex233.alexscaves.client.render.entity.FerrouslimeRenderer;
import com.zzhalex233.alexscaves.client.render.entity.FloaterRenderer;
import com.zzhalex233.alexscaves.client.render.entity.ForsakenRenderer;
import com.zzhalex233.alexscaves.client.render.entity.GammaroachRenderer;
import com.zzhalex233.alexscaves.client.render.entity.GloomothRenderer;
import com.zzhalex233.alexscaves.client.render.entity.GossamerWormRenderer;
import com.zzhalex233.alexscaves.client.render.entity.GossamerWormPartRenderer;
import com.zzhalex233.alexscaves.client.render.entity.GingerbreadManRenderer;
import com.zzhalex233.alexscaves.client.render.entity.GrottoceratopsRenderer;
import com.zzhalex233.alexscaves.client.render.entity.GummyBearRenderer;
import com.zzhalex233.alexscaves.client.render.entity.GumballRenderer;
import com.zzhalex233.alexscaves.client.render.entity.GumbeeperRenderer;
import com.zzhalex233.alexscaves.client.render.entity.GumWormRenderer;
import com.zzhalex233.alexscaves.client.render.entity.GumWormSegmentRenderer;
import com.zzhalex233.alexscaves.client.render.entity.HullbreakerPartRenderer;
import com.zzhalex233.alexscaves.client.render.entity.HullbreakerRenderer;
import com.zzhalex233.alexscaves.client.render.entity.LanternfishRenderer;
import com.zzhalex233.alexscaves.client.render.entity.LimestoneSpearRenderer;
import com.zzhalex233.alexscaves.client.render.entity.MagneticWeaponRenderer;
import com.zzhalex233.alexscaves.client.render.entity.MagnetronRenderer;
import com.zzhalex233.alexscaves.client.render.entity.MeltedCaramelRenderer;
import com.zzhalex233.alexscaves.client.render.entity.MovingMetalBlockRenderer;
import com.zzhalex233.alexscaves.client.render.entity.FrostmintSpearRenderer;
import com.zzhalex233.alexscaves.client.render.entity.MineGuardianAnchorRenderer;
import com.zzhalex233.alexscaves.client.render.entity.MineGuardianRenderer;
import com.zzhalex233.alexscaves.client.render.entity.NucleeperRenderer;
import com.zzhalex233.alexscaves.client.render.entity.NuclearExplosionRenderer;
import com.zzhalex233.alexscaves.client.render.entity.NotorRenderer;
import com.zzhalex233.alexscaves.client.render.entity.QuarrySmasherRenderer;
import com.zzhalex233.alexscaves.client.render.entity.RadgillRenderer;
import com.zzhalex233.alexscaves.client.render.entity.RaycatRenderer;
import com.zzhalex233.alexscaves.client.render.entity.RelicheirusRenderer;
import com.zzhalex233.alexscaves.client.render.entity.SeaPigRenderer;
import com.zzhalex233.alexscaves.client.render.entity.SeekingArrowRenderer;
import com.zzhalex233.alexscaves.client.render.entity.SpinningPeppermintRenderer;
import com.zzhalex233.alexscaves.client.render.entity.SubmarineRenderer;
import com.zzhalex233.alexscaves.client.render.entity.SubterranodonRenderer;
import com.zzhalex233.alexscaves.client.render.entity.SugarStaffHexRenderer;
import com.zzhalex233.alexscaves.client.render.entity.SweetishFishRenderer;
import com.zzhalex233.alexscaves.client.render.entity.TeletorRenderer;
import com.zzhalex233.alexscaves.client.render.entity.ThrownWasteDrumRenderer;
import com.zzhalex233.alexscaves.client.render.entity.TripodfishRenderer;
import com.zzhalex233.alexscaves.client.render.entity.TrilocarisRenderer;
import com.zzhalex233.alexscaves.client.render.entity.UnderzealotRenderer;
import com.zzhalex233.alexscaves.client.render.entity.VallumraptorRenderer;
import com.zzhalex233.alexscaves.client.render.entity.VesperRenderer;
import com.zzhalex233.alexscaves.client.render.entity.WatcherRenderer;
import com.zzhalex233.alexscaves.client.render.entity.WaterBoltRenderer;
import com.zzhalex233.alexscaves.client.render.entity.WaveRenderer;
import com.zzhalex233.alexscaves.client.render.block.SirenLightTileEntityRenderer;
import com.zzhalex233.alexscaves.client.render.block.ACSignTileEntityRenderer;
import com.zzhalex233.alexscaves.client.render.block.AbyssalAltarTileEntityRenderer;
import com.zzhalex233.alexscaves.client.render.block.AmberMonolithTileEntityRenderer;
import com.zzhalex233.alexscaves.client.render.block.AmbersolTileEntityRenderer;
import com.zzhalex233.alexscaves.client.render.block.ConversionCrucibleTileEntityRenderer;
import com.zzhalex233.alexscaves.client.render.block.HologramProjectorTileEntityRenderer;
import com.zzhalex233.alexscaves.client.render.block.MagnetTileEntityRenderer;
import com.zzhalex233.alexscaves.client.render.block.QuarryTileEntityRenderer;
import com.zzhalex233.alexscaves.client.render.block.TeslaBulbTileEntityRenderer;
import com.zzhalex233.alexscaves.client.render.item.GalenaGauntletItemStackRenderer;
import com.zzhalex233.alexscaves.client.render.item.RaygunItemStackRenderer;
import com.zzhalex233.alexscaves.client.render.item.ResistorShieldItemStackRenderer;
import com.zzhalex233.alexscaves.client.render.item.ShotGumItemStackRenderer;
import com.zzhalex233.alexscaves.client.render.item.SirenLightItemStackRenderer;
import com.zzhalex233.alexscaves.client.gui.SpelunkeryTableGui;
import com.zzhalex233.alexscaves.server.CommonProxy;
import com.zzhalex233.alexscaves.server.entity.item.BurrowingArrowEntity;
import com.zzhalex233.alexscaves.server.entity.item.AlexsCavesBoatEntity;
import com.zzhalex233.alexscaves.server.entity.item.CinderBrickEntity;
import com.zzhalex233.alexscaves.server.entity.item.CandyCaneHookEntity;
import com.zzhalex233.alexscaves.server.entity.item.DarkArrowEntity;
import com.zzhalex233.alexscaves.server.entity.item.DepthChargeEntity;
import com.zzhalex233.alexscaves.server.entity.item.DesolateDaggerEntity;
import com.zzhalex233.alexscaves.server.entity.item.DinosaurSpiritEntity;
import com.zzhalex233.alexscaves.server.entity.item.ExtinctionSpearEntity;
import com.zzhalex233.alexscaves.server.entity.item.FallingFrostmintEntity;
import com.zzhalex233.alexscaves.server.entity.item.FallingGuanoEntity;
import com.zzhalex233.alexscaves.server.entity.item.FloaterEntity;
import com.zzhalex233.alexscaves.server.entity.item.FrostmintSpearEntity;
import com.zzhalex233.alexscaves.server.entity.item.GumballEntity;
import com.zzhalex233.alexscaves.server.entity.item.GuanoEntity;
import com.zzhalex233.alexscaves.server.entity.item.InkBombEntity;
import com.zzhalex233.alexscaves.server.entity.item.LimestoneSpearEntity;
import com.zzhalex233.alexscaves.server.entity.item.MagneticWeaponEntity;
import com.zzhalex233.alexscaves.server.entity.item.MeltedCaramelEntity;
import com.zzhalex233.alexscaves.server.entity.item.MineGuardianAnchorEntity;
import com.zzhalex233.alexscaves.server.entity.item.MovingMetalBlockEntity;
import com.zzhalex233.alexscaves.server.entity.item.NuclearExplosionEntity;
import com.zzhalex233.alexscaves.server.entity.item.QuarrySmasherEntity;
import com.zzhalex233.alexscaves.server.entity.item.SeekingArrowEntity;
import com.zzhalex233.alexscaves.server.entity.item.SodaBottleRocketEntity;
import com.zzhalex233.alexscaves.server.entity.item.SpinningPeppermintEntity;
import com.zzhalex233.alexscaves.server.entity.item.SubmarineEntity;
import com.zzhalex233.alexscaves.server.entity.item.SugarStaffHexEntity;
import com.zzhalex233.alexscaves.server.entity.item.ThrownIceCreamScoopEntity;
import com.zzhalex233.alexscaves.server.entity.item.ThrownWasteDrumEntity;
import com.zzhalex233.alexscaves.server.entity.item.WaterBoltEntity;
import com.zzhalex233.alexscaves.server.entity.item.WaveEntity;
import com.zzhalex233.alexscaves.server.entity.living.BrainiacEntity;
import com.zzhalex233.alexscaves.server.entity.living.BoundroidEntity;
import com.zzhalex233.alexscaves.server.entity.living.BoundroidWinchEntity;
import com.zzhalex233.alexscaves.server.entity.living.CaniacEntity;
import com.zzhalex233.alexscaves.server.entity.living.CandicornEntity;
import com.zzhalex233.alexscaves.server.entity.living.CaramelCubeEntity;
import com.zzhalex233.alexscaves.server.entity.living.CorrodentEntity;
import com.zzhalex233.alexscaves.server.entity.living.CorrodentTailEntity;
import com.zzhalex233.alexscaves.server.entity.living.DeepOneEntity;
import com.zzhalex233.alexscaves.server.entity.living.DeepOneKnightEntity;
import com.zzhalex233.alexscaves.server.entity.living.DeepOneMageEntity;
import com.zzhalex233.alexscaves.server.entity.living.FerrouslimeEntity;
import com.zzhalex233.alexscaves.server.entity.living.ForsakenEntity;
import com.zzhalex233.alexscaves.server.entity.living.GammaroachEntity;
import com.zzhalex233.alexscaves.server.entity.living.GloomothEntity;
import com.zzhalex233.alexscaves.server.entity.living.GossamerWormEntity;
import com.zzhalex233.alexscaves.server.entity.living.GossamerWormPartEntity;
import com.zzhalex233.alexscaves.server.entity.living.GingerbreadManEntity;
import com.zzhalex233.alexscaves.server.entity.living.GrottoceratopsEntity;
import com.zzhalex233.alexscaves.server.entity.living.GummyBearEntity;
import com.zzhalex233.alexscaves.server.entity.living.GumbeeperEntity;
import com.zzhalex233.alexscaves.server.entity.living.GumWormEntity;
import com.zzhalex233.alexscaves.server.entity.living.GumWormSegmentEntity;
import com.zzhalex233.alexscaves.server.entity.living.HullbreakerEntity;
import com.zzhalex233.alexscaves.server.entity.living.HullbreakerPartEntity;
import com.zzhalex233.alexscaves.server.entity.living.LanternfishEntity;
import com.zzhalex233.alexscaves.server.entity.living.MagnetronEntity;
import com.zzhalex233.alexscaves.server.entity.living.MineGuardianEntity;
import com.zzhalex233.alexscaves.server.entity.living.NucleeperEntity;
import com.zzhalex233.alexscaves.server.entity.living.NotorEntity;
import com.zzhalex233.alexscaves.server.entity.living.RadgillEntity;
import com.zzhalex233.alexscaves.server.entity.living.RaycatEntity;
import com.zzhalex233.alexscaves.server.entity.living.RelicheirusEntity;
import com.zzhalex233.alexscaves.server.entity.living.SeaPigEntity;
import com.zzhalex233.alexscaves.server.entity.living.SubterranodonEntity;
import com.zzhalex233.alexscaves.server.entity.living.SweetishFishEntity;
import com.zzhalex233.alexscaves.server.entity.living.TeletorEntity;
import com.zzhalex233.alexscaves.server.entity.living.TripodfishEntity;
import com.zzhalex233.alexscaves.server.entity.living.TrilocarisEntity;
import com.zzhalex233.alexscaves.server.entity.living.UnderzealotEntity;
import com.zzhalex233.alexscaves.server.entity.living.VallumraptorEntity;
import com.zzhalex233.alexscaves.server.entity.living.VesperEntity;
import com.zzhalex233.alexscaves.server.entity.living.WatcherEntity;
import com.zzhalex233.alexscaves.server.inventory.SpelunkeryTableContainer;
import com.zzhalex233.alexscaves.server.block.ACBlockRegistry;
import com.zzhalex233.alexscaves.server.block.entity.NuclearFurnaceTileEntity;
import com.zzhalex233.alexscaves.server.block.entity.ACSignTileEntity;
import com.zzhalex233.alexscaves.server.block.entity.NuclearSirenTileEntity;
import com.zzhalex233.alexscaves.server.block.entity.SirenLightTileEntity;
import com.zzhalex233.alexscaves.server.block.entity.TeslaBulbTileEntity;
import com.zzhalex233.alexscaves.server.block.entity.AmberMonolithTileEntity;
import com.zzhalex233.alexscaves.server.block.entity.AmbersolTileEntity;
import com.zzhalex233.alexscaves.server.block.entity.AbyssalAltarTileEntity;
import com.zzhalex233.alexscaves.server.block.entity.ConversionCrucibleTileEntity;
import com.zzhalex233.alexscaves.server.block.entity.HologramProjectorTileEntity;
import com.zzhalex233.alexscaves.server.block.entity.MagnetTileEntity;
import com.zzhalex233.alexscaves.server.block.entity.QuarryTileEntity;
import com.zzhalex233.alexscaves.server.inventory.NuclearFurnaceContainer;
import com.zzhalex233.alexscaves.server.item.ACItemRegistry;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.entity.RenderFallingBlock;
import net.minecraft.client.renderer.entity.RenderSnowball;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.fml.client.registry.RenderingRegistry;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

import java.util.HashMap;
import java.util.Map;

public class ClientProxy extends CommonProxy {
    private final Map<Integer, QuarrySmasherSound> quarrySmasherSounds = new HashMap<>();
    private final Map<BlockPos, HologramProjectorSound> hologramProjectorSounds = new HashMap<>();
    private final Map<BlockPos, MagnetSound> magnetSounds = new HashMap<>();
    private final Map<BlockPos, NuclearFurnaceSound> nuclearFurnaceSounds = new HashMap<>();
    private final Map<BlockPos, NuclearSirenSound> nuclearSirenSounds = new HashMap<>();
    private final Map<Integer, GalenaGauntletSound> galenaGauntletSounds = new HashMap<>();
    private final Map<Integer, ResistorShieldSound> resistorShieldSounds = new HashMap<>();
    private final Map<Integer, RaygunSound> raygunSounds = new HashMap<>();
    private final Map<Integer, SubmarineSound> submarineSounds = new HashMap<>();

    @Override
    public void preInit(FMLPreInitializationEvent event) {
        RenderingRegistry.registerEntityRenderingHandler(TrilocarisEntity.class, TrilocarisRenderer::new);
        RenderingRegistry.registerEntityRenderingHandler(LanternfishEntity.class, LanternfishRenderer::new);
        RenderingRegistry.registerEntityRenderingHandler(TripodfishEntity.class, TripodfishRenderer::new);
        RenderingRegistry.registerEntityRenderingHandler(SeaPigEntity.class, SeaPigRenderer::new);
        RenderingRegistry.registerEntityRenderingHandler(GossamerWormEntity.class, GossamerWormRenderer::new);
        RenderingRegistry.registerEntityRenderingHandler(GossamerWormPartEntity.class, GossamerWormPartRenderer::new);
        RenderingRegistry.registerEntityRenderingHandler(SweetishFishEntity.class, SweetishFishRenderer::new);
        RenderingRegistry.registerEntityRenderingHandler(SeekingArrowEntity.class, SeekingArrowRenderer::new);
        RenderingRegistry.registerEntityRenderingHandler(GrottoceratopsEntity.class, GrottoceratopsRenderer::new);
        RenderingRegistry.registerEntityRenderingHandler(RelicheirusEntity.class, RelicheirusRenderer::new);
        RenderingRegistry.registerEntityRenderingHandler(SubterranodonEntity.class, SubterranodonRenderer::new);
        RenderingRegistry.registerEntityRenderingHandler(VallumraptorEntity.class, VallumraptorRenderer::new);
        RenderingRegistry.registerEntityRenderingHandler(AlexsCavesBoatEntity.class, AlexsCavesBoatRenderer::new);
        RenderingRegistry.registerEntityRenderingHandler(DeepOneEntity.class, DeepOneRenderer::new);
        RenderingRegistry.registerEntityRenderingHandler(DeepOneKnightEntity.class, DeepOneKnightRenderer::new);
        RenderingRegistry.registerEntityRenderingHandler(DeepOneMageEntity.class, DeepOneMageRenderer::new);
        RenderingRegistry.registerEntityRenderingHandler(HullbreakerEntity.class, HullbreakerRenderer::new);
        RenderingRegistry.registerEntityRenderingHandler(HullbreakerPartEntity.class, HullbreakerPartRenderer::new);
        RenderingRegistry.registerEntityRenderingHandler(MineGuardianEntity.class, MineGuardianRenderer::new);
        RenderingRegistry.registerEntityRenderingHandler(MineGuardianAnchorEntity.class, MineGuardianAnchorRenderer::new);
        RenderingRegistry.registerEntityRenderingHandler(FloaterEntity.class, FloaterRenderer::new);
        RenderingRegistry.registerEntityRenderingHandler(SubmarineEntity.class, SubmarineRenderer::new);
        RenderingRegistry.registerEntityRenderingHandler(CandicornEntity.class, CandicornRenderer::new);
        RenderingRegistry.registerEntityRenderingHandler(GummyBearEntity.class, GummyBearRenderer::new);
        RenderingRegistry.registerEntityRenderingHandler(CaniacEntity.class, CaniacRenderer::new);
        RenderingRegistry.registerEntityRenderingHandler(CaramelCubeEntity.class, CaramelCubeRenderer::new);
        RenderingRegistry.registerEntityRenderingHandler(GumbeeperEntity.class, GumbeeperRenderer::new);
        RenderingRegistry.registerEntityRenderingHandler(GingerbreadManEntity.class, GingerbreadManRenderer::new);
        RenderingRegistry.registerEntityRenderingHandler(GumWormEntity.class, GumWormRenderer::new);
        RenderingRegistry.registerEntityRenderingHandler(GumWormSegmentEntity.class, GumWormSegmentRenderer::new);
        RenderingRegistry.registerEntityRenderingHandler(NotorEntity.class, NotorRenderer::new);
        RenderingRegistry.registerEntityRenderingHandler(TeletorEntity.class, TeletorRenderer::new);
        RenderingRegistry.registerEntityRenderingHandler(BoundroidEntity.class, BoundroidRenderer::new);
        RenderingRegistry.registerEntityRenderingHandler(BoundroidWinchEntity.class, BoundroidWinchRenderer::new);
        RenderingRegistry.registerEntityRenderingHandler(FerrouslimeEntity.class, FerrouslimeRenderer::new);
        RenderingRegistry.registerEntityRenderingHandler(MagnetronEntity.class, MagnetronRenderer::new);
        RenderingRegistry.registerEntityRenderingHandler(RadgillEntity.class, RadgillRenderer::new);
        RenderingRegistry.registerEntityRenderingHandler(GammaroachEntity.class, GammaroachRenderer::new);
        RenderingRegistry.registerEntityRenderingHandler(RaycatEntity.class, RaycatRenderer::new);
        RenderingRegistry.registerEntityRenderingHandler(NucleeperEntity.class, NucleeperRenderer::new);
        RenderingRegistry.registerEntityRenderingHandler(BrainiacEntity.class, BrainiacRenderer::new);
        RenderingRegistry.registerEntityRenderingHandler(CorrodentEntity.class, CorrodentRenderer::new);
        RenderingRegistry.registerEntityRenderingHandler(CorrodentTailEntity.class, CorrodentTailRenderer::new);
        RenderingRegistry.registerEntityRenderingHandler(VesperEntity.class, VesperRenderer::new);
        RenderingRegistry.registerEntityRenderingHandler(GloomothEntity.class, GloomothRenderer::new);
        RenderingRegistry.registerEntityRenderingHandler(WatcherEntity.class, WatcherRenderer::new);
        RenderingRegistry.registerEntityRenderingHandler(BurrowingArrowEntity.class, BurrowingArrowRenderer::new);
        RenderingRegistry.registerEntityRenderingHandler(DarkArrowEntity.class, DarkArrowRenderer::new);
        RenderingRegistry.registerEntityRenderingHandler(UnderzealotEntity.class, UnderzealotRenderer::new);
        RenderingRegistry.registerEntityRenderingHandler(ForsakenEntity.class, ForsakenRenderer::new);
        RenderingRegistry.registerEntityRenderingHandler(NuclearExplosionEntity.class, NuclearExplosionRenderer::new);
        RenderingRegistry.registerEntityRenderingHandler(ThrownWasteDrumEntity.class, ThrownWasteDrumRenderer::new);
        RenderingRegistry.registerEntityRenderingHandler(CinderBrickEntity.class, manager -> new RenderSnowball<>(manager, ACItemRegistry.CINDER_BRICK.item(), Minecraft.getMinecraft().getRenderItem()));
        RenderingRegistry.registerEntityRenderingHandler(GuanoEntity.class, manager -> new RenderSnowball<>(manager, ACItemRegistry.GUANO.item(), Minecraft.getMinecraft().getRenderItem()));
        RenderingRegistry.registerEntityRenderingHandler(SodaBottleRocketEntity.class, manager -> new RenderSnowball<>(manager, ACItemRegistry.PURPLE_SODA_BOTTLE_ROCKET.item(), Minecraft.getMinecraft().getRenderItem()));
        RenderingRegistry.registerEntityRenderingHandler(DepthChargeEntity.class, manager -> new RenderSnowball<>(manager, ACItemRegistry.DEPTH_CHARGE.item(), Minecraft.getMinecraft().getRenderItem()));
        RenderingRegistry.registerEntityRenderingHandler(InkBombEntity.class, manager -> new RenderSnowball<>(manager, ACItemRegistry.INK_BOMB.item(), Minecraft.getMinecraft().getRenderItem()));
        RenderingRegistry.registerEntityRenderingHandler(WaterBoltEntity.class, WaterBoltRenderer::new);
        RenderingRegistry.registerEntityRenderingHandler(WaveEntity.class, WaveRenderer::new);
        RenderingRegistry.registerEntityRenderingHandler(ThrownIceCreamScoopEntity.class, manager -> new RenderSnowball<>(manager, ACItemRegistry.VANILLA_ICE_CREAM_SCOOP.item(), Minecraft.getMinecraft().getRenderItem()));
        RenderingRegistry.registerEntityRenderingHandler(CandyCaneHookEntity.class, CandyCaneHookRenderer::new);
        RenderingRegistry.registerEntityRenderingHandler(SpinningPeppermintEntity.class, SpinningPeppermintRenderer::new);
        RenderingRegistry.registerEntityRenderingHandler(SugarStaffHexEntity.class, SugarStaffHexRenderer::new);
        RenderingRegistry.registerEntityRenderingHandler(FallingGuanoEntity.class, RenderFallingBlock::new);
        RenderingRegistry.registerEntityRenderingHandler(FallingFrostmintEntity.class, RenderFallingBlock::new);
        RenderingRegistry.registerEntityRenderingHandler(LimestoneSpearEntity.class, LimestoneSpearRenderer::new);
        RenderingRegistry.registerEntityRenderingHandler(ExtinctionSpearEntity.class, ExtinctionSpearRenderer::new);
        RenderingRegistry.registerEntityRenderingHandler(DinosaurSpiritEntity.class, DinosaurSpiritRenderer::new);
        RenderingRegistry.registerEntityRenderingHandler(FrostmintSpearEntity.class, FrostmintSpearRenderer::new);
        RenderingRegistry.registerEntityRenderingHandler(DesolateDaggerEntity.class, DesolateDaggerRenderer::new);
        RenderingRegistry.registerEntityRenderingHandler(MeltedCaramelEntity.class, MeltedCaramelRenderer::new);
        RenderingRegistry.registerEntityRenderingHandler(MagneticWeaponEntity.class, MagneticWeaponRenderer::new);
        RenderingRegistry.registerEntityRenderingHandler(MovingMetalBlockEntity.class, MovingMetalBlockRenderer::new);
        RenderingRegistry.registerEntityRenderingHandler(GumballEntity.class, GumballRenderer::new);
        RenderingRegistry.registerEntityRenderingHandler(QuarrySmasherEntity.class, QuarrySmasherRenderer::new);
        ClientRegistry.bindTileEntitySpecialRenderer(SirenLightTileEntity.class, new SirenLightTileEntityRenderer());
        ClientRegistry.bindTileEntitySpecialRenderer(AmbersolTileEntity.class, new AmbersolTileEntityRenderer());
        ClientRegistry.bindTileEntitySpecialRenderer(AmberMonolithTileEntity.class, new AmberMonolithTileEntityRenderer());
        ClientRegistry.bindTileEntitySpecialRenderer(HologramProjectorTileEntity.class, new HologramProjectorTileEntityRenderer());
        ClientRegistry.bindTileEntitySpecialRenderer(TeslaBulbTileEntity.class, new TeslaBulbTileEntityRenderer());
        ClientRegistry.bindTileEntitySpecialRenderer(MagnetTileEntity.class, new MagnetTileEntityRenderer());
        ClientRegistry.bindTileEntitySpecialRenderer(QuarryTileEntity.class, new QuarryTileEntityRenderer());
        ClientRegistry.bindTileEntitySpecialRenderer(AbyssalAltarTileEntity.class, new AbyssalAltarTileEntityRenderer());
        ClientRegistry.bindTileEntitySpecialRenderer(ConversionCrucibleTileEntity.class, new ConversionCrucibleTileEntityRenderer());
        ClientRegistry.bindTileEntitySpecialRenderer(ACSignTileEntity.class, new ACSignTileEntityRenderer());
        ACItemRegistry.GALENA_GAUNTLET.item().setTileEntityItemStackRenderer(new GalenaGauntletItemStackRenderer());
        ACItemRegistry.RESISTOR_SHIELD.item().setTileEntityItemStackRenderer(new ResistorShieldItemStackRenderer());
        ACItemRegistry.RAYGUN.item().setTileEntityItemStackRenderer(new RaygunItemStackRenderer());
        ACItemRegistry.SHOT_GUM.item().setTileEntityItemStackRenderer(new ShotGumItemStackRenderer());
        ACBlockRegistry.SIREN_LIGHT.item().setTileEntityItemStackRenderer(new SirenLightItemStackRenderer());
        ACClientEventHandler.registerKeyBindings();
    }

    @Override
    public void openBookGUI(ItemStack stack) {
        Minecraft.getMinecraft().player.playSound(com.zzhalex233.alexscaves.server.misc.ACSoundRegistry.CAVE_BOOK_OPEN, 0.7F, 1.0F);
        Minecraft.getMinecraft().displayGuiScreen(new CaveBookScreen(stack));
    }

    @Override
    public Object getSpelunkeryTableGui(EntityPlayer player, World world, BlockPos pos) {
        return new SpelunkeryTableGui(new SpelunkeryTableContainer(player.inventory, world, pos), player.inventory);
    }

    @Override
    public Object getNuclearFurnaceGui(EntityPlayer player, World world, BlockPos pos) {
        return new NuclearFurnaceGui(new NuclearFurnaceContainer(player.inventory, (NuclearFurnaceTileEntity) world.getTileEntity(pos)), player.inventory);
    }

    @Override
    public void spawnTeslaBulbLightning(World world, Vec3d from, Vec3d to) {
        World clientWorld = world == null ? Minecraft.getMinecraft().world : world;
        if (clientWorld != null) {
            Minecraft.getMinecraft().addScheduledTask(() -> Minecraft.getMinecraft().effectRenderer.addEffect(new TeslaBulbLightningParticle(clientWorld, from, to)));
        }
    }

    @Override
    public void spawnQuarryBorderLightning(World world, Vec3d from, Vec3d to) {
        World clientWorld = world == null ? Minecraft.getMinecraft().world : world;
        if (clientWorld != null) {
            Minecraft.getMinecraft().addScheduledTask(() -> Minecraft.getMinecraft().effectRenderer.addEffect(new QuarryBorderLightningParticle(clientWorld, from, to)));
        }
    }

    @Override
    public void spawnMagneticFlow(World world, Vec3d from, Vec3d to, boolean azure) {
        World clientWorld = world == null ? Minecraft.getMinecraft().world : world;
        if (clientWorld != null) {
            Minecraft.getMinecraft().addScheduledTask(() -> MagneticTrailParticle.spawnFlow(clientWorld, from, to, azure));
        }
    }

    @Override
    public void spawnMagneticOrbit(World world, Vec3d center, boolean azure) {
        World clientWorld = world == null ? Minecraft.getMinecraft().world : world;
        if (clientWorld != null) {
            Minecraft.getMinecraft().addScheduledTask(() -> MagneticTrailParticle.spawnOrbit(clientWorld, center, azure));
        }
    }

    @Override
    public void spawnNuclearSirenSonar(World world, Vec3d pos, Vec3d direction) {
        World clientWorld = world == null ? Minecraft.getMinecraft().world : world;
        if (clientWorld != null) {
            Minecraft.getMinecraft().addScheduledTask(() -> NuclearSirenSonarParticle.spawn(clientWorld, pos, direction));
        }
    }

    @Override
    public void spawnAmberMonolithParticles(World world, Vec3d from, Vec3d to) {
        World clientWorld = world == null ? Minecraft.getMinecraft().world : world;
        if (clientWorld != null) {
            Minecraft.getMinecraft().addScheduledTask(() -> AmberMonolithParticle.spawn(clientWorld, from, to));
        }
    }

    @Override
    public void playHologramProjectorSound(HologramProjectorTileEntity hologramProjector) {
        HologramProjectorSound sound = hologramProjectorSounds.get(hologramProjector.getPos());
        if (sound == null || !sound.isSameBlockEntity(hologramProjector) || sound.isDonePlaying()) {
            sound = new HologramProjectorSound(hologramProjector);
            hologramProjectorSounds.put(hologramProjector.getPos().toImmutable(), sound);
            Minecraft.getMinecraft().getSoundHandler().playSound(sound);
        }
        hologramProjectorSounds.entrySet().removeIf(entry -> entry.getValue().isDonePlaying());
    }

    @Override
    public void playMagnetSound(MagnetTileEntity magnet) {
        MagnetSound sound = magnetSounds.get(magnet.getPos());
        if (sound == null || !sound.isSameBlockEntity(magnet) || sound.isDonePlaying()) {
            sound = new MagnetSound(magnet);
            magnetSounds.put(magnet.getPos().toImmutable(), sound);
            Minecraft.getMinecraft().getSoundHandler().playSound(sound);
        }
        magnetSounds.entrySet().removeIf(entry -> entry.getValue().isDonePlaying());
    }

    @Override
    public void playNuclearFurnaceSound(NuclearFurnaceTileEntity furnace) {
        NuclearFurnaceSound sound = nuclearFurnaceSounds.get(furnace.getPos());
        if (sound == null || !sound.isSameBlockEntity(furnace) || sound.isDonePlaying()) {
            sound = new NuclearFurnaceSound(furnace);
            nuclearFurnaceSounds.put(furnace.getPos().toImmutable(), sound);
            Minecraft.getMinecraft().getSoundHandler().playSound(sound);
        }
        nuclearFurnaceSounds.entrySet().removeIf(entry -> entry.getValue().isDonePlaying());
    }

    @Override
    public void playNuclearSirenSound(NuclearSirenTileEntity siren) {
        NuclearSirenSound sound = nuclearSirenSounds.get(siren.getPos());
        if (sound == null || !sound.isSameBlockEntity(siren) || sound.isDonePlaying()) {
            sound = new NuclearSirenSound(siren);
            nuclearSirenSounds.put(siren.getPos().toImmutable(), sound);
            Minecraft.getMinecraft().getSoundHandler().playSound(sound);
        }
        nuclearSirenSounds.entrySet().removeIf(entry -> entry.getValue().isDonePlaying());
    }

    @Override
    public void playQuarrySmasherSound(QuarrySmasherEntity quarrySmasher) {
        QuarrySmasherSound sound = quarrySmasherSounds.get(quarrySmasher.getEntityId());
        if (sound == null || !sound.isSameEntity(quarrySmasher) || sound.isDonePlaying()) {
            sound = new QuarrySmasherSound(quarrySmasher);
            quarrySmasherSounds.put(quarrySmasher.getEntityId(), sound);
            Minecraft.getMinecraft().getSoundHandler().playSound(sound);
        }
        quarrySmasherSounds.entrySet().removeIf(entry -> entry.getValue().isDonePlaying());
    }

    @Override
    public void playGalenaGauntletSound(EntityLivingBase user) {
        GalenaGauntletSound sound = galenaGauntletSounds.get(user.getEntityId());
        if (sound == null || !sound.isSameEntity(user) || sound.isDonePlaying()) {
            sound = new GalenaGauntletSound(user);
            galenaGauntletSounds.put(user.getEntityId(), sound);
            Minecraft.getMinecraft().getSoundHandler().playSound(sound);
        }
        galenaGauntletSounds.entrySet().removeIf(entry -> entry.getValue().isDonePlaying());
    }

    @Override
    public void playResistorShieldSound(EntityLivingBase user, boolean scarlet) {
        ResistorShieldSound sound = resistorShieldSounds.get(user.getEntityId());
        if (sound == null || !sound.isSameEntity(user) || sound.isDonePlaying()) {
            sound = new ResistorShieldSound(user, scarlet);
            resistorShieldSounds.put(user.getEntityId(), sound);
            Minecraft.getMinecraft().getSoundHandler().playSound(sound);
        }
        resistorShieldSounds.entrySet().removeIf(entry -> entry.getValue().isDonePlaying());
    }

    @Override
    public void playRaygunSound(EntityLivingBase user) {
        RaygunSound sound = raygunSounds.get(user.getEntityId());
        if (sound == null || !sound.isSameEntity(user) || sound.isDonePlaying()) {
            sound = new RaygunSound(user);
            raygunSounds.put(user.getEntityId(), sound);
            Minecraft.getMinecraft().getSoundHandler().playSound(sound);
        }
        raygunSounds.entrySet().removeIf(entry -> entry.getValue().isDonePlaying());
    }

    @Override
    public void playSubmarineSound(SubmarineEntity submarine) {
        SubmarineSound sound = submarineSounds.get(submarine.getEntityId());
        if (sound == null || !sound.isSameEntity(submarine) || sound.isDonePlaying()) {
            sound = new SubmarineSound(submarine);
            submarineSounds.put(submarine.getEntityId(), sound);
            Minecraft.getMinecraft().getSoundHandler().playSound(sound);
        }
        submarineSounds.entrySet().removeIf(entry -> entry.getValue().isDonePlaying());
    }

    @Override
    public void clearSoundCacheFor(Entity entity) {
        int id = entity.getEntityId();
        quarrySmasherSounds.remove(id);
        galenaGauntletSounds.remove(id);
        resistorShieldSounds.remove(id);
        raygunSounds.remove(id);
        submarineSounds.remove(id);
    }

    @Override
    public EntityPlayer getClientSidePlayer() {
        return Minecraft.getMinecraft().player;
    }

    @Override
    public boolean isKeyDown(int key) {
        Minecraft minecraft = Minecraft.getMinecraft();
        if (minecraft.gameSettings == null) {
            return false;
        }
        if (key == 0) {
            return minecraft.gameSettings.keyBindJump.isKeyDown();
        }
        if (key == 1) {
            return minecraft.gameSettings.keyBindSneak.isKeyDown();
        }
        return key == 2 && ACClientEventHandler.SUBMARINE_FLOODLIGHTS.isKeyDown();
    }

    @Override
    public float getPartialTicks() {
        return Minecraft.getMinecraft().getRenderPartialTicks();
    }

    @Override
    public int getPlayerTime() {
        EntityPlayer player = Minecraft.getMinecraft().player;
        return player == null ? 0 : player.ticksExisted;
    }

    @Override
    public void runOnClientThread(Runnable runnable) {
        Minecraft.getMinecraft().addScheduledTask(runnable);
    }
}
