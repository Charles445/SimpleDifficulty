package com.charles445.simpledifficulty.potion;

import com.charles445.simpledifficulty.api.SDCapabilities;
import com.charles445.simpledifficulty.api.thirst.IThirstCapability;
import com.charles445.simpledifficulty.capability.ThirstCapability;
import com.charles445.simpledifficulty.config.ModConfig;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ResourceLocation;

public class PotionThirsty extends PotionBase
{
	private final ResourceLocation texture;
	
	public PotionThirsty()
	{
		super(true, 0x2B9500);
		xOffset = 0;
		yOffset = 0;
		texture = formatTexture("thirsty");
		drawHUD=false;
	}
	
	@Override
	public ResourceLocation getTexture()
	{
		return texture;
	}
	
	@Override
	public boolean isReady(int duration, int amplifier)
	{
		//Apply every tick
		return true;
	}
	
	@Override
	public void performEffect(EntityLivingBase entityLivingBaseIn, int amplifier)
	{
		if(entityLivingBaseIn instanceof EntityPlayer)
		{
			EntityPlayer player = (EntityPlayer)entityLivingBaseIn;
			IThirstCapability capability = SDCapabilities.getThirstData(player);
			capability.addThirstExhaustion((float)ModConfig.server.thirst.thirstyStrength * (1 + amplifier));
		}
	}

	
}
