package com.charles445.simpledifficulty.potion;

import com.charles445.simpledifficulty.api.SDDamageSources;
import com.charles445.simpledifficulty.api.SDPotions;
import com.charles445.simpledifficulty.config.ModConfig;
import com.charles445.simpledifficulty.util.DamageUtil;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.DamageSource;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;

public class PotionHyperthermia extends PotionThermia
{
	private final ResourceLocation texture;
	
	public PotionHyperthermia()
	{
		super(true, 0xFFC85C);
		xOffset = 0;
		yOffset = 0;
		texture = formatTexture("hyperthermia");
	}
	
	@Override
	public ResourceLocation getTexture()
	{
		return texture;
	}
	
	@Override
	public void attackPlayer(EntityPlayer player, float damage)
	{
		player.attackEntityFrom(SDDamageSources.HYPERTHERMIA, damage);
	}
}
