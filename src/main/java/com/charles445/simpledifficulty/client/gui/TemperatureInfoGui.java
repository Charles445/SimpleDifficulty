package com.charles445.simpledifficulty.client.gui;

import java.util.Map;
import java.util.TreeMap;

import com.charles445.simpledifficulty.api.config.ClientConfig;
import com.charles445.simpledifficulty.api.config.ClientOptions;
import com.charles445.simpledifficulty.api.config.QuickConfig;
import com.charles445.simpledifficulty.api.temperature.ITemperatureDynamicModifier;
import com.charles445.simpledifficulty.api.temperature.ITemperatureModifier;
import com.charles445.simpledifficulty.api.temperature.TemperatureEnum;
import com.charles445.simpledifficulty.api.temperature.TemperatureRegistry;
import com.charles445.simpledifficulty.api.temperature.TemperatureUtil;
import com.charles445.simpledifficulty.config.ModConfig;
import com.charles445.simpledifficulty.util.WorldUtil;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.client.event.RenderGameOverlayEvent.ElementType;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

public class TemperatureInfoGui
{
	private final Minecraft mc = Minecraft.getMinecraft();
	
	private int updateCounter = 0;

	public int xPadding = 2;
	public int yPadding = 2;

	//public int transparency = 0xBB000000;
	public int transparency = 0xDD000000;
	public int defaultColor = 0xFFFFFF | transparency;
	public int coldColor = 0x7777FF | transparency;
	public int hotColor = 0xFF7777 | transparency;
	
	public Map<String, Float> resultMap = new TreeMap<String, Float>();
	public int resultCumulative = 0;
	
	@SubscribeEvent
	public void onPostRenderGameOverlay(RenderGameOverlayEvent.Post event)
	{
		if(event.getType() == ElementType.TEXT && QuickConfig.isTemperatureEnabled() && ModConfig.client.temperatureReadout && !mc.gameSettings.showDebugInfo)
		{
			//Check permissions 
			
			//TODO Permission Level seems to not quite work when a dimension is changed
			if(mc.player.getPermissionLevel() >= 2 || mc.isSingleplayer() || mc.player.isCreative())
			{
				ScaledResolution resolution = event.getResolution();
				displayTemperature(resolution.getScaledWidth(), resolution.getScaledHeight());
			}
		}
	}
	
	@SubscribeEvent
	public void onClientTick(TickEvent.ClientTickEvent event)
	{
		if(event.phase==TickEvent.Phase.END)
		{
			updateCounter++;
			
			if(updateCounter % 10 == 0 && ModConfig.client.temperatureReadout)
			{
				updateTemperature();
			}
		}
	}
	
	public void updateTemperature()
	{
		if(mc.world==null || mc.player==null)
			return;
		
		World world = mc.world;
		EntityPlayer player = mc.player;
		BlockPos pos = WorldUtil.getSidedBlockPos(world,player);
		
		resultMap.clear();
		
		float cumulative = 0f;
		
		for(ITemperatureModifier modifier : TemperatureRegistry.modifiers.values())
		{
			float result = modifier.getWorldInfluence(world, pos);
			result += modifier.getPlayerInfluence(player);
			resultMap.put(modifier.getName(), result);
			cumulative += result;
		}
		
		for(ITemperatureDynamicModifier modifier : TemperatureRegistry.dynamicModifiers.values())
		{
			float oldCumulative = cumulative;
			cumulative = modifier.applyDynamicWorldInfluence(world, pos, cumulative);
			cumulative = modifier.applyDynamicPlayerInfluence(player, cumulative);
			resultMap.put(modifier.getName(), cumulative-oldCumulative);
		}
		
		resultCumulative = (int)cumulative;
		
	}
	
	public void displayTemperature(int width, int height)
	{
		GlStateManager.enableBlend();
		
		int yIncrement = mc.fontRenderer.FONT_HEIGHT;
		int xOffset = 0;
		int yOffset = 0;
		
		for(Map.Entry<String, Float> entry : resultMap.entrySet())
		{
			xOffset = 0;
			
			String name = entry.getKey();
			Float value = entry.getValue();
			
			int valueColor = defaultColor;
			if(value > 0.0f)
			{
				valueColor = hotColor;
			}
			else if(value < 0.0f)
			{
				valueColor = coldColor;
			}
			
			xOffset = mc.fontRenderer.drawString(name+": ", xPadding+xOffset, yPadding+yOffset, defaultColor, true);
			xOffset = mc.fontRenderer.drawString(""+value, xPadding+xOffset, yPadding+yOffset, valueColor, true);
			
			yOffset += yIncrement;
		}
		
		int valueColor = defaultColor;
		switch(TemperatureUtil.getTemperatureEnum(resultCumulative))
		{
			case BURNING:
			case HOT:
				valueColor = hotColor;
				break;
			
			case COLD:
			case FREEZING:
				valueColor = coldColor;
				break;
			
			case NORMAL:
			default:
				break;
		
		}
		
		xOffset = 0;
		xOffset = mc.fontRenderer.drawString("---------", xPadding+xOffset, yPadding+yOffset, defaultColor,true);
		yOffset += yIncrement;

		xOffset = 0;
		xOffset = mc.fontRenderer.drawString("Result: ", xPadding+xOffset, yPadding+yOffset, defaultColor, true);
		xOffset = mc.fontRenderer.drawString(""+resultCumulative, xPadding+xOffset, yPadding+yOffset, valueColor, true);
		yOffset += yIncrement;
		
		GlStateManager.disableBlend();
	}
}
