package com.charles445.simpledifficulty.temperature;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import com.charles445.simpledifficulty.SimpleDifficulty;
import com.charles445.simpledifficulty.config.ModConfig;

import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class ModifierSeason extends ModifierBase
{
	//Mod Compatibility: Serene Seasons
	
	
	//Hard Dependency = 2k to 6k ns, very quick
	//Reflection = 4k to 12k ns, still very quick
	
	//Those measurements were without the underground effect applied
	
	Class SeasonHelper;
	Class ISeasonState;
	Class SubSeason;
	Method getSeasonState;
	Method getSubSeason;
	private boolean enabled;
	
	public ModifierSeason()
	{
		super("Season");
		
		try
		{
			SeasonHelper = Class.forName("sereneseasons.api.season.SeasonHelper");
			ISeasonState = Class.forName("sereneseasons.api.season.ISeasonState");
			SubSeason = Class.forName("sereneseasons.api.season.Season$SubSeason");
			
			getSeasonState = SeasonHelper.getDeclaredMethod("getSeasonState", World.class);
			getSubSeason = ISeasonState.getDeclaredMethod("getSubSeason");
			
			enabled = true;

			//Safety check to avoid bad casting later
			if(!SubSeason.isEnum())
			{
				SimpleDifficulty.logger.error("ModifierSeason reflection failed! SubSeason was not an enum! Serene Seasons compatibility is now disabled!");
				enabled = false;
			}
			
			
		}
		catch (ClassNotFoundException | NoSuchMethodException | SecurityException e)
		{
			SimpleDifficulty.logger.error("ModifierSeason reflection failed! Serene Seasons compatibility is now disabled!",e);
			enabled = false;
		}
	}
	@Override
	public float getWorldInfluence(World world, BlockPos pos)
	{
		//SeasonHelper.getSeasonState(world).getSubSeason();
		if(enabled && world.provider.isSurfaceWorld())
		{
			try
			{
				//Object seasonState = getSeasonState.invoke(null, world);
				//Object subSeason = getSubSeason.invoke(ISeasonState.cast(seasonState));
				//switch(((Enum)subSeason).name())
				switch(((Enum)getSubSeason.invoke(ISeasonState.cast(getSeasonState.invoke(null, world)))).name())
				{
					case "EARLY_AUTUMN": 	return applyUndergroundEffect((float)ModConfig.server.temperature.sereneseasons.seasonEarlyAutumn, world, pos);
					case "EARLY_SPRING": 	return applyUndergroundEffect((float)ModConfig.server.temperature.sereneseasons.seasonEarlySpring, world, pos);
					case "EARLY_SUMMER": 	return applyUndergroundEffect((float)ModConfig.server.temperature.sereneseasons.seasonEarlySummer, world, pos);
					case "EARLY_WINTER": 	return applyUndergroundEffect((float)ModConfig.server.temperature.sereneseasons.seasonEarlyWinter, world, pos);
					case "LATE_AUTUMN": 	return applyUndergroundEffect((float)ModConfig.server.temperature.sereneseasons.seasonLateAutumn, world, pos);
					case "LATE_SPRING": 	return applyUndergroundEffect((float)ModConfig.server.temperature.sereneseasons.seasonLateSpring, world, pos); 
					case "LATE_SUMMER":  	return applyUndergroundEffect((float)ModConfig.server.temperature.sereneseasons.seasonLateSummer, world, pos);
					case "LATE_WINTER":  	return applyUndergroundEffect((float)ModConfig.server.temperature.sereneseasons.seasonLateWinter, world, pos);
					case "MID_AUTUMN":  	return applyUndergroundEffect((float)ModConfig.server.temperature.sereneseasons.seasonMidAutumn, world, pos);
					case "MID_SPRING":  	return applyUndergroundEffect((float)ModConfig.server.temperature.sereneseasons.seasonMidSpring, world, pos);
					case "MID_SUMMER":  	return applyUndergroundEffect((float)ModConfig.server.temperature.sereneseasons.seasonMidSummer, world, pos);
					case "MID_WINTER":  	return applyUndergroundEffect((float)ModConfig.server.temperature.sereneseasons.seasonMidWinter, world, pos);
					default:
						return 0.0f;
				}
			}
			catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e)
			{
				SimpleDifficulty.logger.error("ModifierSeason reflection failed during getWorldInfluence! Serene Seasons compatibility is now disabled!",e);
				enabled = false;
			}
		}

		return 0.0f;
		
		/* Hard Dependency Variant
		switch(SeasonHelper.getSeasonState(world).getSubSeason())
		{
			case EARLY_AUTUMN: 	return applyUndergroundEffect((float)ModConfig.server.temperature.sereneseasons.seasonEarlyAutumn, world, pos);
			case EARLY_SPRING: 	return applyUndergroundEffect((float)ModConfig.server.temperature.sereneseasons.seasonEarlySpring, world, pos);
			case EARLY_SUMMER: 	return applyUndergroundEffect((float)ModConfig.server.temperature.sereneseasons.seasonEarlySummer, world, pos);
			case EARLY_WINTER: 	return applyUndergroundEffect((float)ModConfig.server.temperature.sereneseasons.seasonEarlyWinter, world, pos);
			case LATE_AUTUMN: 	return applyUndergroundEffect((float)ModConfig.server.temperature.sereneseasons.seasonLateAutumn, world, pos);
			case LATE_SPRING: 	return applyUndergroundEffect((float)ModConfig.server.temperature.sereneseasons.seasonLateSpring, world, pos); 
			case LATE_SUMMER:  	return applyUndergroundEffect((float)ModConfig.server.temperature.sereneseasons.seasonLateSummer, world, pos);
			case LATE_WINTER:  	return applyUndergroundEffect((float)ModConfig.server.temperature.sereneseasons.seasonLateWinter, world, pos);
			case MID_AUTUMN:  	return applyUndergroundEffect((float)ModConfig.server.temperature.sereneseasons.seasonMidAutumn, world, pos);
			case MID_SPRING:  	return applyUndergroundEffect((float)ModConfig.server.temperature.sereneseasons.seasonMidSpring, world, pos);
			case MID_SUMMER:  	return applyUndergroundEffect((float)ModConfig.server.temperature.sereneseasons.seasonMidSummer, world, pos);
			case MID_WINTER:  	return applyUndergroundEffect((float)ModConfig.server.temperature.sereneseasons.seasonMidWinter, world, pos);
			default:
				return 0.0f;
		}
		*/
	}
}
