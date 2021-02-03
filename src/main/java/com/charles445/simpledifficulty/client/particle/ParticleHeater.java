package com.charles445.simpledifficulty.client.particle;

import net.minecraft.world.World;

public class ParticleHeater extends ParticleTemperature
{
	public ParticleHeater(World world, double xPos, double yPos, double zPos, double motionX, double motionY, double motionZ)
	{
		super(world, xPos, yPos, zPos, motionX, motionY, motionZ);
		
		this.particleAge = world.rand.nextInt(2);
	}

	@Override
	int getFrameMax()
	{
		return 8;
	}

	@Override
	int getParticleTextureY()
	{
		return 0;
	}
	
}
