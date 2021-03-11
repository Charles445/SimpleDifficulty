package com.charles445.simpledifficulty.potion;

import com.charles445.simpledifficulty.api.SDDamageSources;
import com.charles445.simpledifficulty.api.SDPotions;
import com.charles445.simpledifficulty.util.DamageUtil;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.DamageSource;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;

public class PotionHypothermia extends PotionThermia
{
	private final ResourceLocation texture;
	
	public PotionHypothermia()
	{
		super(true, 0x5CEBFF);
		xOffset = 0;
		yOffset = 0;
		texture = formatTexture("hypothermia");
	}
	
	@Override
	public ResourceLocation getTexture()
	{
		return texture;
	}

	@Override
	public void attackPlayer(EntityPlayer player, float damage)
	{
		player.attackEntityFrom(SDDamageSources.HYPOTHERMIA, damage);
	}
}
