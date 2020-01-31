package com.charles445.simpledifficulty.handler;

import java.text.DecimalFormat;

import com.charles445.simpledifficulty.api.temperature.TemperatureUtil;

import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class TooltipHandler
{
	//CLIENT only
	
	private final DecimalFormat df = new DecimalFormat("#.##");

	
	@SubscribeEvent
	public void onItemTooltipEvent(ItemTooltipEvent event)
	{
		if(TemperatureUtil.getArmorTemperatureTag(event.getItemStack())!=0.0f)
		{
			//Has armor temperature tag
			float tempTag = TemperatureUtil.getArmorTemperatureTag(event.getItemStack());
			
			if(tempTag>0.0f)
			{
				event.getToolTip().add(TextFormatting.DARK_RED + " Temperature +" + df.format(tempTag));
			}
			else
			{
				event.getToolTip().add(TextFormatting.DARK_BLUE + " Temperature " + df.format(tempTag));
			}
		}
	}
}
