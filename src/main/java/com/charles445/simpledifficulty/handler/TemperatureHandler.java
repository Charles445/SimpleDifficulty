package com.charles445.simpledifficulty.handler;

import java.util.List;

import com.charles445.simpledifficulty.api.SDCapabilities;
import com.charles445.simpledifficulty.api.config.JsonConfig;
import com.charles445.simpledifficulty.api.config.QuickConfig;
import com.charles445.simpledifficulty.api.config.json.JsonConsumableTemperature;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraftforge.event.entity.living.LivingEntityUseItemEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class TemperatureHandler
{
	//Both Sides
	@SubscribeEvent
	public void onLivingEntityUseItemFinish(LivingEntityUseItemEvent.Finish event)
	{
		if(!QuickConfig.isTemperatureEnabled())
			return;
		
		if(event.getEntityLiving() instanceof EntityPlayer)
		{
			EntityPlayer player = (EntityPlayer)event.getEntityLiving();
			
			if(player.world.isRemote)
				return;
			
			//Server Side
			
			ItemStack stack = event.getItem();
			
			List<JsonConsumableTemperature> consumableList = JsonConfig.consumableTemperature.get(stack.getItem().getRegistryName().toString());
			
			if(consumableList!=null)
			{
				for(JsonConsumableTemperature jct : consumableList)
				{
					if(jct==null)
						continue;
					
					if(jct.matches(stack))
					{
						SDCapabilities.getTemperatureData(player).setTemporaryModifier(jct.group, jct.temperature, jct.duration);
						break;
					}
				}
			}
			else
			{
				//Alternative
			}
		}
		
	}
}
