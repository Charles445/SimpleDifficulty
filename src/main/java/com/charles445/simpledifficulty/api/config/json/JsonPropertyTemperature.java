package com.charles445.simpledifficulty.api.config.json;

import java.util.HashMap;
import java.util.Map;

import com.google.gson.annotations.SerializedName;

import net.minecraft.block.properties.IProperty;
import net.minecraft.block.state.IBlockState;

public class JsonPropertyTemperature
{
	@SerializedName("properties")
	public Map<String,String> properties;
	
	@SerializedName("temperature")
	public float temperature;
	
	public JsonPropertyTemperature(float temperature, JsonPropertyValue ... props)
	{
		this.temperature = temperature;
		this.properties = new HashMap<String,String>();
		for(JsonPropertyValue prop : props)
		{
			properties.put(prop.property, prop.value);
		}
	}

	public boolean matchesState(IBlockState blockstate)
	{
		//Takes a blockstate and checks its properties for any mismatches
		
		for(IProperty<?> property : blockstate.getPropertyKeys())
		{
			String propname = property.getName();
			if(properties.containsKey(propname))
			{
				//Matching property keys
				//Get the value as a string
				String stateValue = blockstate.getValue(property).toString();
				if(!properties.get(propname).equals(stateValue))
				{
					return false;
				}
			}
		}
		
		return true;
	}
	
	public boolean matchesDescribedProperties(JsonPropertyValue... props)
	{
		if(props.length != properties.keySet().size())
			return false;
		
		for(JsonPropertyValue prop : props)
		{
			if(!properties.containsKey(prop.property))
			{
				return false;
			}
			else
			{
				//Has key
				if(!prop.value.equals(properties.get(prop.property)))
				{
					return false;
				}
			}
		}
		
		return true;
	}
}
