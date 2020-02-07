package com.charles445.simpledifficulty.compat.mod;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

import com.charles445.simpledifficulty.SimpleDifficulty;
import com.charles445.simpledifficulty.config.ModConfig;
import com.charles445.simpledifficulty.temperature.ModifierBase;

import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class HarvestFestivalModifier extends ModifierBase
{
	private boolean enabled;
	
	private Class HFApi;
	private Class CalendarManager;
	private Class Season;
	private Class CalendarDate;
	
	private Field f_HFApi_calendar;
	
	private Object o_CalendarManager;
	
	//Nullable
	//LOCATION Based Season, aka what season HarvestFestival determines a BlockPos should behave like
	private Method m_CalendarManager_getSeasonAtCoordinates;
	private Method m_CalendarManager_getDate;
	private Method m_CalendarDate_getSeason; 
	
	public HarvestFestivalModifier()
	{
		super("HarvestFestival");
		
		
		try
		{
			HFApi = Class.forName("joshie.harvest.api.HFApi");
			CalendarManager = Class.forName("joshie.harvest.api.calendar.CalendarManager");
			Season = Class.forName("joshie.harvest.api.calendar.Season");
			CalendarDate = Class.forName("joshie.harvest.api.calendar.CalendarDate");
			
			f_HFApi_calendar = HFApi.getDeclaredField("calendar");
			
			o_CalendarManager = CalendarManager.cast(f_HFApi_calendar.get(CalendarManager.getClass()));
			
			m_CalendarManager_getSeasonAtCoordinates = CalendarManager.getMethod("getSeasonAtCoordinates", World.class, BlockPos.class);
			m_CalendarManager_getDate = CalendarManager.getMethod("getDate", World.class);
			m_CalendarDate_getSeason = CalendarDate.getMethod("getSeason");
			
			enabled = true;
			
			//Safety Enum Check
			if(!Season.isEnum())
			{
				SimpleDifficulty.logger.error("HarvestFestivalModifier reflection failed! Season was not an enum! Harvest Festival compatibility is now disabled!");
				enabled = false;
			}
		}
		catch (Exception e)
		{
			SimpleDifficulty.logger.error("HarvestFestivalModifier reflection failed! Harvest Festival compatibility is now disabled!",e);
			enabled = false;
		}
		
	}
	
	@Override
	public float getWorldInfluence(World world, BlockPos pos)
	{
		if(enabled && world.provider.isSurfaceWorld() && ModConfig.server.compatibility.toggles.harvestFestival)
		{
			//Attempt Position Specific
			
			try
			{
				Object o_SeasonResult = m_CalendarManager_getSeasonAtCoordinates.invoke(o_CalendarManager, world, pos);
				if(o_SeasonResult == null)
				{
					//Get the global season instead
					Object o_DateResult = m_CalendarManager_getDate.invoke(o_CalendarManager, world);
					if(o_DateResult != null)
					{
						o_SeasonResult = m_CalendarDate_getSeason.invoke(CalendarDate.cast(o_DateResult));
					}
				}
				
				if(o_SeasonResult == null)
					return 0.0f;
				
				switch(((Enum)o_SeasonResult).name())
				{
					case "SPRING": return applyUndergroundEffect((float)ModConfig.server.compatibility.harvestfestival.seasonSpring, world, pos);
					case "SUMMER": return applyUndergroundEffect((float)ModConfig.server.compatibility.harvestfestival.seasonSummer, world, pos); 
					case "AUTUMN": return applyUndergroundEffect((float)ModConfig.server.compatibility.harvestfestival.seasonAutumn, world, pos);
					case "WINTER": return applyUndergroundEffect((float)ModConfig.server.compatibility.harvestfestival.seasonWinter, world, pos);
					default:
						return 0.0f;
				}
			}
			catch(Exception e)
			{
				SimpleDifficulty.logger.error("HarvestFestivalModifier reflection failed during getWorldInfluence! Harvest Festival compatibility is now disabled!",e);
				enabled = false;
			}
		}
		
		return 0.0f;
	}
}
