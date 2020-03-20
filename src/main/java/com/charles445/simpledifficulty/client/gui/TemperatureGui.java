package com.charles445.simpledifficulty.client.gui;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import com.charles445.simpledifficulty.api.SDCapabilities;
import com.charles445.simpledifficulty.api.SDItems;
import com.charles445.simpledifficulty.api.config.ClientConfig;
import com.charles445.simpledifficulty.api.config.ClientOptions;
import com.charles445.simpledifficulty.api.config.QuickConfig;
import com.charles445.simpledifficulty.api.temperature.ITemperatureCapability;
import com.charles445.simpledifficulty.api.temperature.TemperatureEnum;
import com.charles445.simpledifficulty.api.temperature.TemperatureUtil;
import com.charles445.simpledifficulty.debug.DebugUtil;
import com.charles445.simpledifficulty.util.RenderUtil;
import com.charles445.simpledifficulty.util.WorldUtil;

import ibxm.Player;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.client.event.RenderGameOverlayEvent.ElementType;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

public class TemperatureGui
{
	private final Minecraft mc = Minecraft.getMinecraft();
	
	private int updateCounter = 0;
	private final Random rand = new Random();
	public static final ResourceLocation ICONS = new ResourceLocation("simpledifficulty:textures/gui/icons.png");
	//Position on the icons sheet
	private static final int texturepos_X = 0;
	private static final int texturepos_Y = 32;
	//Dimensions of the icon
	private static final int textureWidth = 16;
	private static final int textureHeight = 16;
	
	private int oldTemperature = -1;
	private int frameCounter = -1;
	private boolean risingTemperature = false;
	private boolean startAnimation = false;
	private boolean shakeSide = false;
	
	private static final int texturepos_Y_alt_OVR = 80;
	private static final int texturepos_Y_alt_BG = 96;
	private int alternateTemperature = 0;
	
	private int worldThermometerTemperature = 0;
	private boolean hasThermometer = false;
	private static final int texturepos_X_therm = 0;
	private static final int texturepos_Y_therm = 192;
	private static final int thermometer_per_row = 8;
	private static final int textureWidthTherm = 16;
	private static final int textureHeightTherm = 16;
	
	@SubscribeEvent
	public void onPreRenderGameOverlay(RenderGameOverlayEvent.Pre event)
	{
		if(event.getType()==ElementType.EXPERIENCE && QuickConfig.isTemperatureEnabled() && mc.playerController.gameIsSurvivalOrAdventure())
		{
			//Set the seed to avoid shaking during pausing
			rand.setSeed((long)(updateCounter * 445));
			
			//Bind to custom icons image
			bind(ICONS);
			
			EntityPlayerSP player = Minecraft.getMinecraft().player;
			ITemperatureCapability capability = SDCapabilities.getTemperatureData(player);
			ScaledResolution resolution = event.getResolution();
			renderTemperatureIcon(resolution.getScaledWidth(), resolution.getScaledHeight(), capability.getTemperatureLevel());
			
			//Rebind to old icons image
			bind(Gui.ICONS);
		}
		
	}
	
	private void renderTemperatureIcon(int width, int height, int temperature)
	{
		GlStateManager.enableBlend();
		
		// ---
		
		TemperatureEnum tempEnum = TemperatureUtil.getTemperatureEnum(temperature);
		
		int bgXOffset = textureWidth * tempEnum.ordinal();
		
		int x = width / 2 - 8;
		int y = height - 54;
		
		
		int xOffset = 0;
		int yOffset = 0;
		
		int shakeFrequency = 0;
		
		if(tempEnum == TemperatureEnum.FREEZING)
		{
			if(temperature==TemperatureEnum.FREEZING.getUpperBound())
			{
				shakeFrequency = 0;
			}
			else if(temperature > TemperatureEnum.FREEZING.getMiddle()+1)
			{
				shakeFrequency = 2;
			}
			else
			{
				shakeFrequency = 1;
			}
		}
		else if(tempEnum == TemperatureEnum.BURNING)
		{
			if(temperature==TemperatureEnum.BURNING.getLowerBound())
			{
				shakeFrequency = 0;
			}
			else if(temperature >= TemperatureEnum.BURNING.getMiddle())
			{
				shakeFrequency = 1;
			}
			else
			{
				shakeFrequency = 2;
			}
		}
		
		if(shakeFrequency > 0)
		{
			if(updateCounter % shakeFrequency == 0)
			{
				int shakeCheck = updateCounter/shakeFrequency;
				if(shakeCheck % 2 == 0)
					shakeSide=true;
				else
					shakeSide=false;
			}
			
			xOffset = shakeSide?1:-1;
		}
		
		/* Again not quite what I want
		int shakeStrength = 0;
		
		if(tempEnum == TemperatureEnum.FREEZING)
		{
			shakeStrength = (TemperatureEnum.FREEZING.getUpperBound() - temperature);
		}
		else if(tempEnum == TemperatureEnum.BURNING)
		{
			shakeStrength = (temperature - TemperatureEnum.BURNING.getLowerBound());
		}
		
		if(shakeStrength>1)
		{
			//0 should be none
			//1 should be some
			//2 should be a lot
			//
			int shakeFrequency = 5 / shakeStrength;
			if(shakeFrequency <= 0)
				shakeFrequency = 1;
			
			if(updateCounter % shakeFrequency == 0)
			{
				int shakeCheck = updateCounter/shakeFrequency;
				if(shakeCheck % 2 == 0)
					shakeSide=true;
				else
					shakeSide=false;
			}
			
			xOffset = shakeSide?1:-1;
			
		}
		*/
		
		/* This is fun but not quite what I want
		int shakeStrength = 0;
			
		if(tempEnum == TemperatureEnum.FREEZING)
		{
			shakeStrength = (TemperatureEnum.FREEZING.getUpperBound() - temperature) / 2;
		}
		else if(tempEnum == TemperatureEnum.BURNING)
		{
			shakeStrength = (temperature - TemperatureEnum.BURNING.getLowerBound()) / 2;
		}
		
		if(shakeStrength > 0)
		{
			xOffset = shakeStrength * (updateCounter%2==0?1:-1);
			shakeStrength/=2;
			if(shakeStrength > 0)
			{
				yOffset = rand.nextInt(shakeStrength * 2) - shakeStrength;
			}
		}
		*/
		
		if(ClientConfig.instance.getBoolean(ClientOptions.ALTERNATE_TEMP))
		{
			//Alternate, shows core and outside feeling
			
			//Draw outside temperature first
			int outsideOffset = textureWidth * TemperatureUtil.getTemperatureEnum(alternateTemperature).ordinal();
			
			//Draw bg of outside temperature
			RenderUtil.drawTexturedModalRect(x + xOffset, y + yOffset, texturepos_X + outsideOffset, texturepos_Y_alt_BG, textureWidth, textureHeight);
			
			//Draw overlay of body temperature
			RenderUtil.drawTexturedModalRect(x + xOffset, y + yOffset, texturepos_X + bgXOffset, texturepos_Y_alt_OVR, textureWidth, textureHeight);
		}
		else
		{
			//Classic
			RenderUtil.drawTexturedModalRect(x + xOffset, y + yOffset, texturepos_X + bgXOffset, texturepos_Y, textureWidth, textureHeight);
		}
		
		//Temperature Change Animation
		
		if(oldTemperature==-1)
			oldTemperature = temperature;
		
		if(oldTemperature!=temperature)
		{
			risingTemperature = oldTemperature < temperature;
			oldTemperature = temperature;
			startAnimation = true;
		}
		
		if(frameCounter>=0)
		{
			int ovrXOffset = textureWidth * frameCounter;
			int ovrYOffset = (risingTemperature?1:2)*textureHeight;
			
			RenderUtil.drawTexturedModalRect(x, y, texturepos_X + ovrXOffset, texturepos_Y + ovrYOffset, textureWidth, textureHeight);
		}
		
		//Thermometer
		
		if(hasThermometer && ClientConfig.instance.getBoolean(ClientOptions.HUD_THERMOMETER) && ClientConfig.instance.getBoolean(ClientOptions.ENABLE_THERMOMETER))
		{
			int therm_position = worldThermometerTemperature - TemperatureEnum.FREEZING.getLowerBound();
			
			int therm_x = (therm_position % thermometer_per_row)*textureWidthTherm + texturepos_X_therm;
			int therm_y = (therm_position / thermometer_per_row)*textureHeightTherm + texturepos_Y_therm;
			
			
			//TODO configure where this thing draws
			
			int therm_xOffset = ClientConfig.instance.getInteger(ClientOptions.HUD_THERMOMETERX);
			int therm_yOffset = ClientConfig.instance.getInteger(ClientOptions.HUD_THERMOMETERY);
			
			RenderUtil.drawTexturedModalRect(x + therm_xOffset, y - 18 + therm_yOffset, therm_x, therm_y, textureWidthTherm, textureHeightTherm);
		}
		// - - -
		
		
		GlStateManager.disableBlend();
	}
	
	@SubscribeEvent
	public void onClientTick(TickEvent.ClientTickEvent event)
	{
		if(event.phase==TickEvent.Phase.END)
		{
			//Make sure game isn't paused as the GUI shouldn't be changing
			if(!mc.isGamePaused())
			{
				updateCounter++;
				
				
				//Temperature Change Animation
				
				//if(updateCounter % 2 == 0)
				//{
					if(frameCounter>=0)
						frameCounter--;
					
					if(startAnimation)
					{
						frameCounter = 11;
						startAnimation = false;
					}
				//}
					
				if(updateCounter % 15 == 12 && QuickConfig.isTemperatureEnabled())
				{
					if(mc.player != null)
					{
						EntityPlayer player = mc.player;
						World world = player.getEntityWorld();
						
						if(ClientConfig.instance.getBoolean(ClientOptions.ALTERNATE_TEMP))
						{
							alternateTemperature = TemperatureUtil.clampTemperature(TemperatureUtil.getPlayerTargetTemperature(player));
						}
						
						if(ClientConfig.instance.getBoolean(ClientOptions.HUD_THERMOMETER) && ClientConfig.instance.getBoolean(ClientOptions.ENABLE_THERMOMETER))
						{
							
							worldThermometerTemperature = TemperatureUtil.clampTemperature((int)WorldUtil.calculateClientWorldEntityTemperature(world, player));
							
							//TODO Verify this is how you're supposed to check for items in player inventory
							hasThermometer = player.inventory.hasItemStack(new ItemStack(SDItems.thermometer));
						}
					}
				}
			}
		}
	}
	
	private void bind(ResourceLocation resource)
	{
		mc.getTextureManager().bindTexture(resource);
	}
}
