package com.charles445.simpledifficulty.config;

public enum JsonFileName
{
	armorTemperatures("armorTemperatures.json"),
	blockTemperatures("blockTemperatures.json"),
	consumableTemperature("consumableTemperature.json"),
	consumableThirst("consumableThirst.json"),
	fluidTemperatures("fluidTemperatures.json"),
	heldItemTemperatures("heldItemTemperatures.json"),
	materialTemperature("materialTemperature.json"),
	
	
	//TODO remove migration
	
	armorTemperatures_MIGRATE("armorTemperatures.json"),
	consumableTemperature_MIGRATE("consumableTemperature.json"),
	consumableThirst_MIGRATE("consumableThirst.json"),
	heldItemTemperatures_MIGRATE("heldItemTemperatures.json");
	
	
	
	private String fileName;
	
	private JsonFileName(String fileName)
	{
		this.fileName = fileName;
	}
	
	@Override
	public String toString()
	{
		return this.fileName;
	}
	
	public String get()
	{
		return this.toString();
	}
	
}
