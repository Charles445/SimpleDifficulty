package com.charles445.simpledifficulty.handler;

import java.util.List;

import com.charles445.simpledifficulty.api.SDCapabilities;
import com.charles445.simpledifficulty.api.SDPotions;
import com.charles445.simpledifficulty.api.config.JsonConfig;
import com.charles445.simpledifficulty.api.config.QuickConfig;
import com.charles445.simpledifficulty.api.config.json.JsonConsumableThirst;
import com.charles445.simpledifficulty.api.thirst.IThirstCapability;
import com.charles445.simpledifficulty.api.thirst.ThirstEnum;
import com.charles445.simpledifficulty.api.thirst.ThirstUtil;
import com.charles445.simpledifficulty.network.MessageDrinkWater;
import com.charles445.simpledifficulty.network.PacketHandler;
import com.charles445.simpledifficulty.util.internal.ThirstUtilInternal;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.init.PotionTypes;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.PotionType;
import net.minecraft.potion.PotionUtils;
import net.minecraft.util.EnumHand;
import net.minecraft.world.World;
import net.minecraftforge.event.entity.living.LivingEntityUseItemEvent;
import net.minecraftforge.event.entity.living.LivingEvent.LivingJumpEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.event.entity.player.AttackEntityEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.event.world.BlockEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class ThirstHandler
{
	//
	// Drink Attempt Events
	//
	
	//Both Sides
	@SubscribeEvent
	public void onLivingEntityUseItemFinish(LivingEntityUseItemEvent.Finish event)
	{
		if(!QuickConfig.isThirstEnabled())
			return;
		
		if(event.getEntityLiving() instanceof EntityPlayer)
		{
			EntityPlayer player = (EntityPlayer)event.getEntityLiving();
			
			if(player.world.isRemote)
				return;
			
			//Server Side
			
			ItemStack stack = event.getItem();
			
			
			if(stack.getItem().equals(Items.POTIONITEM))
			{
				PotionType potionType = PotionUtils.getPotionFromItem(stack);
				
				if(potionType.getRegistryName() != null)
				{
					String modDomain = potionType.getRegistryName().getResourceDomain();
					
					//Vanilla potions
					if(modDomain.equals("minecraft"))
					{
						if(potionType.equals(PotionTypes.WATER) || potionType.equals(PotionTypes.AWKWARD) || potionType.equals(PotionTypes.MUNDANE) || potionType.equals(PotionTypes.THICK))
						{
							ThirstUtil.takeDrink(player, ThirstEnum.NORMAL);
						}
						else if(!potionType.equals(PotionTypes.EMPTY))
						{
							ThirstUtil.takeDrink(player, ThirstEnum.POTION);
						}
					}
					else if(SDPotions.potionTypes.containsValue(potionType))
					{
						ThirstUtil.takeDrink(player, ThirstEnum.POTION);
					}
				}
				
				
				/*
				PotionType potionType = PotionUtils.getPotionFromItem(stack);
				if(potionType.getEffects().isEmpty())
				*/
			}
			else
			{
				//JSON
				List<JsonConsumableThirst> consumableList = JsonConfig.consumableThirst.get(stack.getItem().getRegistryName().toString());
				if(consumableList!=null)
				{
					for(JsonConsumableThirst jct : consumableList)
					{
						if(jct==null)
							continue;
						if(jct.matches(stack))
						{
							ThirstUtil.takeDrink(player, jct.amount, jct.saturation, jct.thirstyChance);
							break;
						}
					}
				}
			}
		}
	}
	
	//Both Sides
	@SubscribeEvent
	public void onRightClickBlock(PlayerInteractEvent.RightClickBlock event)
	{
		if(event.getWorld().isRemote)
		{
			//Client Side
			
			//Only run on main hand (otherwise it runs twice)
			if(event.getHand()==EnumHand.MAIN_HAND)
			{
				if(clientCheckWater(event.getEntityPlayer()))
					clientSendDrinkMessage(event.getEntityPlayer());
			}
		}
	}
	
	//Client Side
	@SubscribeEvent
	public void onRightClickEmpty(PlayerInteractEvent.RightClickEmpty event)
	{
		//Only run on main hand (otherwise it runs twice)
		if(event.getHand()==EnumHand.MAIN_HAND)
		{
			if(clientCheckWater(event.getEntityPlayer()))
				clientSendDrinkMessage(event.getEntityPlayer());
			
			//TODO check for empty water bottle? (I forgot why I wrote this todo)
		}
	}
	
	private boolean clientCheckWater(EntityPlayer player)
	{
		if(!player.isSneaking() || !QuickConfig.isThirstEnabled())
			return false;
		
		if(ThirstUtilInternal.traceWaterToDrink(player) != null)
			return true;
		
		return false;
	}
	
	private void clientSendDrinkMessage(EntityPlayer player)
	{
		//Make new message
		MessageDrinkWater message = new MessageDrinkWater();
		
		//Send to server
		PacketHandler.instance.sendToServer(message);
		
		//TODO should sound be on server?
		//Play sound and swing arm
		player.playSound(SoundEvents.ENTITY_GENERIC_DRINK, 0.5f, 1.0f);
		player.swingArm(EnumHand.MAIN_HAND);
	}
	
	//
	// Thirst Exhausting Events
	//
	
	@SubscribeEvent
	public void onAttackEntity(AttackEntityEvent event)
	{
		if(!QuickConfig.isThirstEnabled())
			return;
		
		World world = event.getEntity().world;
		if(world.isRemote)
			return;
		
		EntityPlayer player = event.getEntityPlayer();
		
		if(!shouldSkipThirst(player))
		{
			Entity monster = event.getTarget();
			if(monster.canBeAttackedWithItem() && !monster.hitByEntity(player))
			{
				addExhaustion(player, 0.3f);
			}
		}
	}
	
	@SubscribeEvent
	public void onBlockBreak(BlockEvent.BreakEvent event)
	{
		if(!QuickConfig.isThirstEnabled())
			return;
		
		World world = event.getWorld();
		if(world.isRemote)
			return;
		
		EntityPlayer player = event.getPlayer();
		
		if(!shouldSkipThirst(player))
		{
			//Check if the player is actually able to harvest the block
			if(event.getState().getBlock().canHarvestBlock(world, event.getPos(), player))
			{
				addExhaustion(player, 0.025f);
			}
		}
	}
	
	@SubscribeEvent
	public void onLivingHurt(LivingHurtEvent event)
	{
		if(!QuickConfig.isThirstEnabled())
			return;
		
		World world = event.getEntity().world;
		if(world.isRemote || event.getAmount() == 0.0f)
			return;
		
		if(event.getEntity() instanceof EntityPlayer)
		{
			EntityPlayer player = (EntityPlayer) event.getEntity();
			
			if(!shouldSkipThirst(player))
			{
				addExhaustion(player, event.getSource().getHungerDamage());
			}
		}
	}
	
	@SubscribeEvent
	public void onLivingJump(LivingJumpEvent event)
	{
		if(!QuickConfig.isThirstEnabled())
			return;
		
		World world = event.getEntity().world;
		if(world.isRemote)
			return;
		
		if(event.getEntity() instanceof EntityPlayer)
		{
			EntityPlayer player = (EntityPlayer) event.getEntity();
			
			if(!shouldSkipThirst(player))
			{
				if(player.isSprinting())
				{
					addExhaustion(player, 0.8f);
				}
				else
				{
					addExhaustion(player, 0.2f);
				}
			}
		}
	}
	
	private boolean shouldSkipThirst(EntityPlayer player)
	{
		return player.isCreative() || player.isSpectator();
	}
	
	private void addExhaustion(EntityPlayer player, float exhaustion)
	{
		IThirstCapability capability = SDCapabilities.getThirstData(player);
		capability.addThirstExhaustion(exhaustion);
	}
}
