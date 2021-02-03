package com.charles445.simpledifficulty.client.particle;

import net.minecraft.client.Minecraft;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.entity.Entity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.fml.client.FMLClientHandler;

public abstract class ParticleTemperature extends Particle
{
	private static final ResourceLocation PARTICLES = new ResourceLocation("simpledifficulty:textures/particles/particles.png");
	
	abstract int getFrameMax();
	abstract int getParticleTextureY();
	
	public ParticleTemperature(World world, double xPos, double yPos, double zPos, double motionInX, double motionInY, double motionInZ)
	{
		super(world, xPos, yPos, zPos);
		this.motionX = this.motionX * 0.01D + motionInX;
		this.motionY = this.motionY * 0.01D + motionInY;
		this.motionZ = this.motionZ * 0.01D + motionInZ;
		this.motionX += (Math.random()*0.02D) - 0.01D;
		this.motionY += (Math.random()*0.02D) - 0.01D;
		this.motionZ += (Math.random()*0.02D) - 0.01D;
		
		this.particleRed = 1.0F;
		this.particleGreen = 1.0F;
		this.particleBlue = 1.0F;
		this.particleMaxAge = (int)(8.0D / (Math.random() * 0.8D + 0.2D)) + 4;
		this.canCollide = false;
		this.particleGravity = 0.015f;
		this.particleScale = 0.5f;
	}

	@Override
	public void renderParticle(BufferBuilder buffer, Entity entityIn, float partialTicks, float rotationX, float rotationZ, float rotationYZ, float rotationXY, float rotationXZ)
	{
		Minecraft.getMinecraft().getTextureManager().bindTexture(PARTICLES);
		super.renderParticle(buffer, entityIn, partialTicks, rotationX, rotationZ, rotationYZ, rotationXY, rotationXZ);
	}
	
	@Override
	public int getFXLayer()
	{
		//FX 3 crashes, but FX 2 doesn't
		//Sure, whatever man
		return 2;
	}
	
	@Override
	public void onUpdate()
	{
		super.onUpdate();
		this.particleTextureIndexX = getFrame();
		this.particleTextureIndexY = getParticleTextureY();
	}
	
	private int getFrame()
	{
		return (int)(getAgeFactor() * (double)getFrameMax());
	}
	
	private double getAgeFactor()
	{
		return (double)(this.particleMaxAge - this.particleAge) / (double)this.particleMaxAge;
	}
}
