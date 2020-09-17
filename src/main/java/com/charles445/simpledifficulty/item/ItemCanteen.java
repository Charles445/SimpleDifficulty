package com.charles445.simpledifficulty.item;

import java.util.List;

import javax.annotation.Nullable;

import com.charles445.simpledifficulty.SimpleDifficulty;
import com.charles445.simpledifficulty.api.SDCapabilities;
import com.charles445.simpledifficulty.api.config.QuickConfig;
import com.charles445.simpledifficulty.api.config.ServerConfig;
import com.charles445.simpledifficulty.api.config.ServerOptions;
import com.charles445.simpledifficulty.api.item.IItemCanteen;
import com.charles445.simpledifficulty.api.thirst.IThirstCapability;
import com.charles445.simpledifficulty.api.thirst.ThirstEnum;
import com.charles445.simpledifficulty.api.thirst.ThirstEnumBlockPos;
import com.charles445.simpledifficulty.api.thirst.ThirstUtil;
import com.charles445.simpledifficulty.util.SoundUtil;

import net.minecraft.client.resources.I18n;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagInt;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.NonNullList;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class ItemCanteen extends ItemDrinkBase implements IItemCanteen
{
	//TODO This code is terrible and can't be interfaced with
	
	public static final String CANTEENTYPE = "CanteenType";
	public static final String DOSES = "Doses";
	
	public ItemCanteen()
	{
		//Doesn't super the constructor
		setMaxDamage(3);
		setNoRepair();
		setMaxStackSize(1);
	}
	
	@Override
	public void getSubItems(CreativeTabs tab, NonNullList<ItemStack> items)
	{
		if (this.isInCreativeTab(tab))
		{
			ItemStack emptyCanteen = new ItemStack(this, 1, 0);
			createTypeTag(emptyCanteen);
			setCanteenEmpty(emptyCanteen);
			
			ItemStack fullCanteen = emptyCanteen.copy();
			setCanteenFull(fullCanteen);
			
			ItemStack purifiedCanteen = fullCanteen.copy();
			setTypeTag(purifiedCanteen, ThirstEnum.PURIFIED.ordinal());
			
			items.add(emptyCanteen);
			items.add(fullCanteen);
			items.add(purifiedCanteen);
			
		}
	}
	
	@Override
	public String getUnlocalizedName(ItemStack stack)
	{
		if(isCanteenEmpty(stack))
			return "item."+SimpleDifficulty.MODID+":"+"canteen_empty";
		
		int type = getTypeTag(stack).getInt();
		if(type>=ThirstEnum.values().length)
			return "item."+SimpleDifficulty.MODID+":"+"canteen_broken";
		
		return "item."+SimpleDifficulty.MODID+":"+"canteen_"+ThirstEnum.values()[type].toString();
	}
	
	@Override
	public ActionResult<ItemStack> onItemRightClick(World world, EntityPlayer player, EnumHand hand)
	{
		ItemStack stack = player.getHeldItem(hand);
		
		//Initializes if it hasn't been initialized already
		int typetag = getTypeTag(stack).getInt();
		
		//Only attempt refill if item isn't full or if it isn't normal water
		//This prevents full purified canteens from getting overridden and removing purified water from the ground mistakenly
		//TODO not this weird implementation that doesn't make much sense
		if(!isCanteenFull(stack) || typetag==ThirstEnum.NORMAL.ordinal())
		{
			ThirstEnumBlockPos traceBlockPos = ThirstUtil.traceWater(player);
			if(traceBlockPos != null)
			{
				ThirstEnum trace = traceBlockPos.thirstEnum;
				//Clear out any purified block that got found
				if(trace==ThirstEnum.PURIFIED)
					player.world.setBlockToAir(traceBlockPos.pos);
				
				//Convert Rain to Normal
				if(trace==ThirstEnum.RAIN)
					trace = ThirstEnum.NORMAL;
				
				tryAddDose(stack,trace);
				SoundUtil.commonPlayPlayerSound(player, SoundEvents.ITEM_BUCKET_FILL);
				player.setActiveHand(hand);
				player.swingArm(hand);
				player.stopActiveHand();
				return new ActionResult(EnumActionResult.SUCCESS, stack);
			}
		}
		if(!isCanteenEmpty(stack))
		{
			IThirstCapability capability = SDCapabilities.getThirstData(player);
			if(capability.isThirsty() || !QuickConfig.isThirstEnabled())
			{
				player.setActiveHand(hand);
				//DebugUtil.messageAll("itemdamage is not maxdamage ActionResult SUCCESS");
				return new ActionResult(EnumActionResult.SUCCESS, stack);
			}
		}
		//DebugUtil.messageAll("ActionResult FAIL");
		return new ActionResult(EnumActionResult.FAIL, stack);
	}
	
	@Override
	public ItemStack onItemUseFinish(ItemStack stack, World world, EntityLivingBase entityLiving)
	{
		if(world.isRemote || !(entityLiving instanceof EntityPlayer))
			return stack;
		
		if(isCanteenEmpty(stack))
			return stack;
		
		EntityPlayer player = (EntityPlayer)entityLiving;
		ThirstUtil.takeDrink(player, this.getThirstLevel(stack), this.getSaturationLevel(stack), this.getDirtyChance(stack));
		removeDose(stack);
		return stack;
	}
	
	@Override
	@SideOnly(Side.CLIENT)
	public void addInformation(ItemStack stack, @Nullable World world, List<String> tooltip, ITooltipFlag flag)
	{
		//Add durability information
		int max = getMaxDoses(stack);
		
		tooltip.add(I18n.format("item.durability", getDoses(stack), max));
		
		/*
		boolean drawDurability = true;
		
		if(flag.isAdvanced())
		{
			
			if(!isCanteenFull(stack))
			{
				//Advanced tooltips, durability is already shown if the item has damage
				drawDurability = false;
			}
		}
		
		if(drawDurability)
		{
			tooltip.add(I18n.format("item.durability", getMaxDoses(stack) - getDoses(stack), getMaxDoses(stack)));
			//tooltip.add(I18n.format("item.durability", stack.getMaxDamage()-stack.getItemDamage(), stack.getMaxDamage()));
		}
		*/
	}
	
	@Override
	public double getDurabilityForDisplay(ItemStack stack)
    {
		double max = (double)this.getMaxDoses(stack);
		if(max == 0.0d)
			return 1.0d;
		
		
        return (max - (double)getDoses(stack)) / max;
    }
	
	@Override
	public boolean showDurabilityBar(ItemStack stack)
	{
		return !isCanteenEmpty(stack);
	}

	@Override
	public int getThirstLevel(ItemStack stack)
	{
		ThirstEnum thirstEnum = getThirstEnum(stack);
		return thirstEnum==null ? 0 : thirstEnum.getThirst();
	}

	@Override
	public float getSaturationLevel(ItemStack stack)
	{
		ThirstEnum thirstEnum = getThirstEnum(stack);
		return thirstEnum==null ? 0.0f : thirstEnum.getSaturation();
	}

	@Override
	public float getDirtyChance(ItemStack stack)
	{
		ThirstEnum thirstEnum = getThirstEnum(stack);
		return thirstEnum==null ? 0.0f : thirstEnum.getThirstyChance();
	}
	
	//TODO Nullable is not smart here
	@Nullable
	@Override
	public ThirstEnum getThirstEnum(ItemStack stack)
	{
		int type = getTypeTag(stack).getInt();
		if(type >= ThirstEnum.values().length)
			return null;
		
		return ThirstEnum.values()[type];
	}
	
	private NBTTagInt getTypeTag(ItemStack stack)
	{
		if(stack.getTagCompound()==null)
		{
			createTypeTag(stack);
			setCanteenEmpty(stack);
		}
		
		NBTBase tag = stack.getTagCompound().getTag(CANTEENTYPE);
		if(tag instanceof NBTTagInt)
		{
			return (NBTTagInt)tag;
		}
		else
		{
			stack.getTagCompound().removeTag(CANTEENTYPE);
			createTypeTag(stack);
			return new NBTTagInt(ThirstEnum.NORMAL.ordinal());
		}
	}
	
	private void setTypeTag(ItemStack stack, ThirstEnum thirstEnum)
	{
		setTypeTag(stack, thirstEnum.ordinal());
	}
	
	private void setTypeTag(ItemStack stack, int tag)
	{
		stack.setTagInfo(CANTEENTYPE, new NBTTagInt(tag));
	}
	
	private void createTypeTag(ItemStack stack)
	{
		setTypeTag(stack,ThirstEnum.NORMAL.ordinal());
	}
	
	private NBTTagInt getDosesTag(ItemStack stack)
	{
		if(stack.getTagCompound()==null)
		{
			createDosesTag(stack);
			setCanteenEmpty(stack);
		}
		
		NBTBase tag = stack.getTagCompound().getTag(DOSES);
		if(tag instanceof NBTTagInt)
		{
			return (NBTTagInt)tag;
		}
		else
		{
			stack.getTagCompound().removeTag(DOSES);
			createDosesTag(stack);
			return new NBTTagInt(0);
		}
	}
	
	private void setDosesTag(ItemStack stack, int doses)
	{
		stack.setTagInfo(DOSES, new NBTTagInt(doses));
	}
	
	private void createDosesTag(ItemStack stack)
	{
		setDosesTag(stack, 0);
	}
	
	@Override
	public int getDoses(ItemStack stack)
	{
		return getDosesTag(stack).getInt();
	}
	
	@Override
	public int getMaxDoses(ItemStack stack)
	{
		return ServerConfig.instance.getInteger(ServerOptions.CANTEEN_DOSES);
	}
	
	@Override
	public boolean isCanteenFull(ItemStack stack)
	{
		return getDoses(stack) >= getMaxDoses(stack);
	}
	
	@Override
	public boolean isCanteenEmpty(ItemStack stack)
	{
		return getDoses(stack) <= 0;
	}
	
	@Override
	public void setCanteenFull(ItemStack stack)
	{
		setDosesInternal(stack, getMaxDoses(stack));
	}
	
	@Override
	public void setCanteenEmpty(ItemStack stack)
	{
		setDosesInternal(stack, 0);
	}
	
	@Override
	public void removeDose(ItemStack stack)
	{
		if(!isCanteenEmpty(stack))
		{
			//setDosesInternal takes care of invalid results
			setDosesInternal(stack, getDoses(stack)-1);
		}
	}
	
	@Override
	public void setDoses(ItemStack stack, int amount)
	{
		//setDosesInternal takes care of invalid results
		setDosesInternal(stack, amount);
	}
	
	@Override
	public void setDoses(ItemStack stack, ThirstEnum thirstEnum, int amount)
	{
		formatCanteen(stack,thirstEnum);
		
		//setDosesInternal takes care of invalid results
		setDosesInternal(stack, amount);
	}
	
	@Override
	public boolean tryAddDose(ItemStack stack, ThirstEnum thirstEnum)
	{
		int oldDamage = getDoses(stack);
		if(oldDamage < 0)
			oldDamage = 0;
		
		boolean format = formatCanteen(stack,thirstEnum);
		//setDosesInternal takes care of invalid results
		
		//getDoses again, as it has possibly changed since formatCanteen
		setDosesInternal(stack, getDoses(stack) + 1);
		
		return format || getDoses(stack) != oldDamage;
	}
	
	private boolean formatCanteen(ItemStack stack, ThirstEnum thirstEnum)
	{
		if(thirstEnum != getThirstEnum(stack))
		{
			//Set canteen to empty and set new type
			setCanteenEmpty(stack);
			setTypeTag(stack,thirstEnum);
			return true;
		}
		
		//Preload doses
		getDoses(stack);
		
		return false;
	}
	
	private void setDosesInternal(ItemStack stack, int amount)
	{
		
		if(amount<=0)
		{
			this.setDosesTag(stack, 0);
			return;
		}
		
		int max = this.getMaxDoses(stack);
		
		if(amount > max)
		{
			this.setDosesTag(stack, max);
			return;
		}
		
		this.setDosesTag(stack, amount);
	}
}
