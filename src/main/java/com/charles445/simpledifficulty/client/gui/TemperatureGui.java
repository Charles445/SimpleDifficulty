package com.charles445.simpledifficulty.client.gui;

import java.util.Random;

import com.charles445.simpledifficulty.api.SDCapabilities;
import com.charles445.simpledifficulty.api.SDItems;
import com.charles445.simpledifficulty.api.config.ClientConfig;
import com.charles445.simpledifficulty.api.config.ClientOptions;
import com.charles445.simpledifficulty.api.config.QuickConfig;
import com.charles445.simpledifficulty.api.temperature.ITemperatureCapability;
import com.charles445.simpledifficulty.api.temperature.TemperatureEnum;
import com.charles445.simpledifficulty.api.temperature.TemperatureUtil;
import com.charles445.simpledifficulty.util.RenderUtil;
import com.charles445.simpledifficulty.util.WorldUtil;

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
	public static final ResourceLocation TEMPHUD = new ResourceLocation("simpledifficulty:textures/gui/temphud.png");
	//Position on the icons sheet
	private static final int classicTexturePos_X = 0;
	private static final int classicTexturePos_Y = 32;
	//Dimensions of the icon
	private static final int classicTextureWidth = 16;
	private static final int classicTextureHeight = 16;
	
	private static final int modernTexturePos_X = 0;
	private static final int modernTexturePos_Y = 0;
	private static final int modernTextureWidth = 16;
	private static final int modernTextureHeight = 16;
	private static final int modernFeelPos_X = 0;
	private static final int modernFeelPos_Y = 16;
	private static final int modernFeelWidth = 32;
	private static final int modernFeelHeight = 32;
	private static final int modernArrowPos_X = 0;
	private static final int modernArrowPos_Y = 144;
	private static final int modernArrowFrames = 14; //0 - 14 = 15 frames
	
	private int oldTemperature = -1;
	private int frameCounterClassic = -1;
	private int frameCounterModern = -1;
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
			
			
			boolean classic = ClientConfig.instance.getBoolean(ClientOptions.CLASSICHUD_TEMPERATURE);
			
			//Bind to custom icons image
			if(classic)
				bind(ICONS);
			else
				bind(TEMPHUD);
			
			EntityPlayerSP player = Minecraft.getMinecraft().player;
			ITemperatureCapability capability = SDCapabilities.getTemperatureData(player);
			ScaledResolution resolution = event.getResolution();
			

			GlStateManager.enableBlend();
			
			//Rendering start
			if(classic)
				renderClassicTemperatureIcon(resolution.getScaledWidth(), resolution.getScaledHeight(), capability.getTemperatureLevel());
			else
				renderTemperatureIcon(resolution.getScaledWidth(), resolution.getScaledHeight(), capability.getTemperatureLevel());
			
			//Rendering end
			
			GlStateManager.disableBlend();
			//Rebind to old icons image
			bind(Gui.ICONS);
		}
		
	}
	
	private int getTempHudFeelBGX(int temperature)
	{
		//MODERN ONLY
		int bgx = 0;
		
		if(temperature < 6 || temperature >= 20)
		{
			bgx = 0;
		}
		else
		{
			bgx = ((temperature/2) - 2) * modernFeelWidth;
		}
		
		return bgx;
	}
	
	private int getTempHudFeelBGY(int temperature)
	{
		//MODERN ONLY
		if(temperature>=20)
			return modernFeelHeight;
		
		return 0;
			
	}
	
	private int getTempHudCoreBGX(int temperature)
	{
		//MODERN ONLY
		//The temperature enum thing is more trouble than it's worth
		
		int bgx = 0;
		boolean animated = false;
		
		if(temperature < 6)
		{
			bgx = 0;
			animated = true;
		}
		else if(temperature>=20)
		{
			bgx = 12 * modernTextureWidth;
			animated = true;
		}
		else
		{
			bgx = ((temperature/2) + 2) * modernTextureWidth;
		}
		
		if(animated)
		{
			//TODO adjust framerate?
			if(temperature < 12)
				bgx += (this.updateCounter % 5) * modernTextureWidth;
			else
				bgx += (this.updateCounter % 4) * modernTextureWidth;
		}
		return bgx;
		
		
	}
	
	private void renderTemperatureIcon(int width, int height, int temperature)
	{
		// ---
		
		TemperatureEnum tempEnum = TemperatureUtil.getTemperatureEnum(temperature);
		
		
		//Core
		
		//Alignment 
		int bgXOffset = getTempHudCoreBGX(temperature);
		
		int x = width / 2 - 8;
		int y = height - 54;
		
		int xOffset = 0;
		int yOffset = 0;

		int shakeFrequency = getShakeFrequency(tempEnum, temperature);
		
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
		
		if(ClientConfig.instance.getBoolean(ClientOptions.ALTERNATE_TEMP))
		{
			//TODO
			RenderUtil.drawTexturedModalRect(x - 8 + xOffset, y - 8 + yOffset, modernFeelPos_X + this.getTempHudFeelBGX(alternateTemperature), modernFeelPos_Y + this.getTempHudFeelBGY(alternateTemperature), modernFeelWidth, modernFeelHeight);
			RenderUtil.drawTexturedModalRect(x + xOffset, y + yOffset, modernTexturePos_X + bgXOffset, modernTexturePos_Y, modernTextureWidth, modernTextureHeight);
		}
		else
		{
			//TODO
			RenderUtil.drawTexturedModalRect(x + xOffset, y + yOffset, modernTexturePos_X + bgXOffset, modernTexturePos_Y, modernTextureWidth, modernTextureHeight);
		}
		
		renderTemperatureChangeAnimation(false, x, y, temperature);
		
		renderThermometer(x, y);
	}
	
	private void renderClassicTemperatureIcon(int width, int height, int temperature)
	{
		// ---
		
		TemperatureEnum tempEnum = TemperatureUtil.getTemperatureEnum(temperature);
		
		int bgXOffset = classicTextureWidth * tempEnum.ordinal();
		
		int x = width / 2 - 8;
		int y = height - 54;
		
		
		int xOffset = 0;
		int yOffset = 0;
		
		int shakeFrequency = getShakeFrequency(tempEnum, temperature);
		
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
		
		if(ClientConfig.instance.getBoolean(ClientOptions.ALTERNATE_TEMP))
		{
			//Alternate, shows core and outside feeling
			
			//Draw outside temperature first
			int outsideOffset = classicTextureWidth * TemperatureUtil.getTemperatureEnum(alternateTemperature).ordinal();
			
			//Draw bg of outside temperature
			RenderUtil.drawTexturedModalRect(x + xOffset, y + yOffset, classicTexturePos_X + outsideOffset, texturepos_Y_alt_BG, classicTextureWidth, classicTextureHeight);
			
			//Draw overlay of body temperature
			RenderUtil.drawTexturedModalRect(x + xOffset, y + yOffset, classicTexturePos_X + bgXOffset, texturepos_Y_alt_OVR, classicTextureWidth, classicTextureHeight);
		}
		else
		{
			//Classic
			RenderUtil.drawTexturedModalRect(x + xOffset, y + yOffset, classicTexturePos_X + bgXOffset, classicTexturePos_Y, classicTextureWidth, classicTextureHeight);
		}
		
		renderTemperatureChangeAnimation(true, x, y, temperature);
		
		renderThermometer(x, y);
	}
	
	private void renderTemperatureChangeAnimation(boolean classic, int x, int y, int temperature)
	{
		//Temperature Change Animation
		
		if(oldTemperature==-1)
			oldTemperature = temperature;
		
		if(oldTemperature!=temperature)
		{
			risingTemperature = oldTemperature < temperature;
			oldTemperature = temperature;
			startAnimation = true;
		}
		
		if(classic)
		{
			if(frameCounterClassic>=0)
			{
				int ovrXOffset = classicTextureWidth * frameCounterClassic;
				int ovrYOffset = (risingTemperature?1:2)*classicTextureHeight;
				
				RenderUtil.drawTexturedModalRect(x, y, classicTexturePos_X + ovrXOffset, classicTexturePos_Y + ovrYOffset, classicTextureWidth, classicTextureHeight);
			}
		}
		else
		{
			if(frameCounterModern>=0)
			{
				int ovrXOffset = (modernArrowFrames - frameCounterModern) * modernTextureWidth;
				int ovrYOffset = (risingTemperature?1:0)*modernTextureHeight;
				
				RenderUtil.drawTexturedModalRect(x, y, modernArrowPos_X + ovrXOffset, modernArrowPos_Y + ovrYOffset, modernTextureWidth, modernTextureHeight);
			}
		}
	}
	
	private void renderThermometer(int x, int y)
	{

		//Thermometer
		
		if(hasThermometer && ClientConfig.instance.getBoolean(ClientOptions.HUD_THERMOMETER) && ClientConfig.instance.getBoolean(ClientOptions.ENABLE_THERMOMETER))
		{
			int therm_position = worldThermometerTemperature - TemperatureEnum.FREEZING.getLowerBound();
			
			int therm_x = (therm_position % thermometer_per_row)*textureWidthTherm + texturepos_X_therm;
			int therm_y = (therm_position / thermometer_per_row)*textureHeightTherm + texturepos_Y_therm;
			
			
			//Configure where this thing draws
			
			int therm_xOffset = ClientConfig.instance.getInteger(ClientOptions.HUD_THERMOMETERX);
			int therm_yOffset = ClientConfig.instance.getInteger(ClientOptions.HUD_THERMOMETERY);
			
			RenderUtil.drawTexturedModalRect(x + therm_xOffset, y - 18 + therm_yOffset, therm_x, therm_y, textureWidthTherm, textureHeightTherm);
		}
		// - - -
	}
	
	private int getShakeFrequency(TemperatureEnum tempEnum, int temperature)
	{
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
		
		return shakeFrequency;
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
					if(frameCounterClassic>=0)
						frameCounterClassic--;
					
					if(frameCounterModern>=0)
						frameCounterModern--;
					
					if(startAnimation)
					{
						frameCounterClassic = 11;
						frameCounterModern = modernArrowFrames;
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
