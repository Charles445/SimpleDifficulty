package com.charles445.simpledifficulty.api.item;

import javax.annotation.Nullable;

import com.charles445.simpledifficulty.api.thirst.ThirstEnum;

import net.minecraft.item.ItemStack;

/** Interface for ItemCanteen
 * <br>
 */
public interface IItemCanteen
{
	/** Gets the water type of the canteen (can be null!)
	 * 
	 * @param stack (canteen's ItemStack)
	 * @return ThirstEnum (water type)
	 */
	@Nullable
	public ThirstEnum getThirstEnum(ItemStack stack);
	
	/** Sets the canteen's water type and number of doses
	 * 
	 * @param stack (canteen's ItemStack)
	 * @param thirstEnum (water type)
	 * @param amount (number of doses)
	 */
	public void setDoses(ItemStack stack, ThirstEnum thirstEnum, int amount);
	
	/** Sets the canteen's doses without changing water type
	 * 
	 * @param stack (canteen's ItemStack)
	 * @param amount (number of doses)
	 */
	public void setDoses(ItemStack stack, int amount);
	
	/** Tries to add a dose to the canteen with the specified water type, returns true if successful
	 * 
	 * @param stack (canteen's ItemStack)
	 * @param thirstEnum (water type)
	 * @return boolean (true if a dose was added)
	 * 
	 */
	public boolean tryAddDose(ItemStack stack, ThirstEnum thirstEnum);
	
	/** Removes a dose from the canteen
	 * 
	 * @param stack (canteen's ItemStack)
	 */
	public void removeDose(ItemStack stack);
	
	/** Sets the canteen to full
	 * 
	 * @param stack (canteen's ItemStack)
	 */
	public void setCanteenFull(ItemStack stack);
	
	/** Sets the canteen to empty
	 * 
	 * @param stack (canteen's ItemStack)
	 */
	public void setCanteenEmpty(ItemStack stack);
	
	/** Returns boolean of whether the canteen is full
	 * 
	 * @param stack (canteen's ItemStack)
	 * @return boolean (is the canteen full?)
	 */
	public boolean isCanteenFull(ItemStack stack);
	
	/** Returns boolean of whether the canteen is empty
	 * 
	 * @param stack (canteen's ItemStack)
	 * @return boolean (is the canteen empty?)
	 */
	public boolean isCanteenEmpty(ItemStack stack);
	
	/** Returns maximum doses of the canteen
	 * 
	 * @param stack (canteen's ItemStack)
	 * @return int (number of doses)
	 */
	public int getMaxDoses(ItemStack stack);
	
	/** Returns the number of doses in the canteen
	 * 
	 * @param stack (canteen's ItemStack)
	 * @return int (number of doses)
	 */
	public int getDoses(ItemStack stack);
}
