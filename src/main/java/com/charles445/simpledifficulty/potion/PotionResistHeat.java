package com.charles445.simpledifficulty.potion;

import com.charles445.simpledifficulty.api.SDPotions;
import com.charles445.simpledifficulty.api.thirst.ThirstEnum;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.ResourceLocation;

public class PotionResistHeat extends PotionBase
{
	private final ResourceLocation texture;
	
	public PotionResistHeat()
	{
		super(false, 0xFFCD72);
		xOffset = 0;
		yOffset = 0;
		texture = formatTexture("resist_heat");
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
		//entity.removePotionEffect(SDPotions.hyperthermia);
		removePotionCoreEffect(entity,SDPotions.hyperthermia);
	}
}
