package com.charles445.simpledifficulty.potion;

import com.charles445.simpledifficulty.api.SDDamageSources;
import com.charles445.simpledifficulty.config.ModConfig;
import com.charles445.simpledifficulty.util.DamageUtil;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.MobEffects;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.DamageSource;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;

public class PotionParasites extends PotionBase
{
	private final ResourceLocation texture;
	
	private int savedDuration = 0;

	public PotionParasites()
	{
		super(false, 0xFFE1B7);
		xOffset = 0;
		yOffset = 0;
		texture = formatTexture("parasites");
		drawHUD=false;
	}
	
	@Override
	public ResourceLocation getTexture()
	{
		return texture;
	}
	
	@Override
	public void performEffect(EntityLivingBase entity, int amplifier)
	{
		if(entity instanceof EntityPlayer)
		{
			World world = entity.getEntityWorld();
			EntityPlayer player = (EntityPlayer) entity;
			
			double hunger = ModConfig.server.thirst.thirstParasitesHunger;
			
			//Hunger
			if(hunger > 0.0d)
				player.addExhaustion((float)hunger * (float)(amplifier + 1));
			
			if(DamageUtil.isModDangerous(world) && DamageUtil.healthAboveDifficulty(world, player))
			{
				double poison = ModConfig.server.thirst.thirstParasitesDamage;
			
				//Damage
				if(poison > 0.0d && isReadyVar(savedDuration, amplifier, 25) && player.getRNG().nextDouble() < poison)
				{
					player.attackEntityFrom(SDDamageSources.PARASITES, 1.0F);
				}
			}
		}
	}
	
	@Override
	public boolean isReady(int duration, int amplifier)
	{
		//Save a copy of the current duration for later use
		savedDuration = duration;
		
		return true;
	}
}
