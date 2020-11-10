package com.charles445.simpledifficulty.potion;

import com.charles445.simpledifficulty.api.SDDamageSources;
import com.charles445.simpledifficulty.api.SDPotions;
import com.charles445.simpledifficulty.util.DamageUtil;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.DamageSource;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;

public class PotionHyperthermia extends PotionBase
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
	public void performEffect(EntityLivingBase entity, int amplifier)
	{
		if(entity instanceof EntityPlayer && (SDPotions.heat_resist == null || !entity.isPotionActive(SDPotions.heat_resist)))
		{
			World world = entity.getEntityWorld();
			EntityPlayer player = (EntityPlayer) entity;
			if(DamageUtil.isModDangerous(world) && DamageUtil.healthAboveDifficulty(world, player))
			{
				player.attackEntityFrom(SDDamageSources.HYPERTHERMIA, 0.5f);
			}
		}
	}
}
