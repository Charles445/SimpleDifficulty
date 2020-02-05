package com.charles445.simpledifficulty.temperature;

import java.util.List;

import com.charles445.simpledifficulty.api.config.JsonConfig;
import com.charles445.simpledifficulty.api.config.json.JsonConsumableTemperature;
import com.charles445.simpledifficulty.api.config.json.JsonTemperatureMetadata;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;

public class ModifierHeldItems extends ModifierBase
{
	public ModifierHeldItems()
	{
		super("HeldItems");
	}
	
	@Override
	public float getPlayerInfluence(EntityPlayer player)
	{
		ItemStack mainHand = player.getHeldItemMainhand();
		ItemStack offHand = player.getHeldItemOffhand();
		
		float sum = 0.0f;
		
		if(!mainHand.isEmpty())
		{
			sum += processStack(mainHand);
		}
		
		if(!offHand.isEmpty())
		{
			sum += processStack(offHand);
		}
		
		return sum;
	}
	
	private float processStack(ItemStack stack)
	{
		List<JsonTemperatureMetadata> heldItemList = JsonConfig.heldItemTemperatures.get(stack.getItem().getRegistryName().toString());
		
		if(heldItemList!=null)
		{
			for(JsonTemperatureMetadata jtm : heldItemList)
			{
				if(jtm==null)
					continue;
				
				if(jtm.matches(stack))
				{
					return jtm.temperature;
				}
			}
		}
		
		return 0.0f;
	}
	
}
