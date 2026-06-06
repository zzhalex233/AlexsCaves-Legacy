package com.zzhalex233.alexscaves.server.block.entity;

import java.util.Random;

import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.Vec3d;

public class AmbersolTileEntity extends TileEntity {
    private Random random;
    private int lights;
    private float rotSpeed;
    private float rotOffset;

    private void init() {
        if (random != null) {
            return;
        }
        long seed = pos == null ? 0L : pos.toLong();
        random = new Random(seed);
        lights = random.nextInt(5) + 4;
        rotSpeed = (random.nextFloat() * 0.5F + 1.0F) * (float) random.nextGaussian();
        rotOffset = random.nextFloat() * 360.0F;
    }

    public int getLights() {
        init();
        return lights;
    }

    public float getRotOffset() {
        init();
        return rotOffset;
    }

    public float getRotSpeed() {
        init();
        return rotSpeed;
    }

    public float calculateShineScale(Vec3d from) {
        double maxDist = 200.0D;
        double dist = Math.min(from.distanceTo(new Vec3d(pos).add(0.5D, 0.5D, 0.5D)), maxDist);
        return (float) Math.pow(Math.sin(dist / maxDist * Math.PI), 0.5D) * 3.0F;
    }

    @Override
    public AxisAlignedBB getRenderBoundingBox() {
        return new AxisAlignedBB(pos.add(-4, -4, -4), pos.add(5, 5, 5));
    }
}
