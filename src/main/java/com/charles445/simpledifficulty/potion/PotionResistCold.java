package com.charles445.simpledifficulty.potion;

import com.charles445.simpledifficulty.api.SDPotions;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.ResourceLocation;

public class PotionResistCold extends PotionBase
{
	private final ResourceLocation texture;
	
	public PotionResistCold()
	{
		super(false, 0x8EF1FF);
		xOffset = 0;
		yOffset = 0;
		texture = formatTexture("resist_cold");
		setBeneficial();
	}

	@Override
	public ResourceLocation getTexture()
	{
		return texture;
	}
	
	@Override
	public void performEffect(EntityLivingBase entity, int amplifier)
	{
		//entity.removePotionEffect(SDPotions.hypothermia);
		removePotionCoreEffect(entity,SDPotions.hypothermia);
	}
}
