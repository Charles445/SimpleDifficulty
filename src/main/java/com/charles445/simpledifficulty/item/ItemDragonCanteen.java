package com.charles445.simpledifficulty.item;

import javax.annotation.Nullable;

import com.charles445.simpledifficulty.SimpleDifficulty;
import com.charles445.simpledifficulty.api.SDCapabilities;
import com.charles445.simpledifficulty.api.config.QuickConfig;
import com.charles445.simpledifficulty.api.item.IItemCanteen;
import com.charles445.simpledifficulty.api.thirst.IThirstCapability;
import com.charles445.simpledifficulty.api.thirst.ThirstEnum;
import com.charles445.simpledifficulty.api.thirst.ThirstEnumBlockPos;
import com.charles445.simpledifficulty.api.thirst.ThirstUtil;
import com.charles445.simpledifficulty.config.json.ExtraItem;
import com.charles445.simpledifficulty.util.SoundUtil;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.IItemPropertyGetter;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagInt;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class ItemDragonCanteen extends ItemCanteen
{
	//TODO redo this, it's garbage
	
	public static final String EI_CAPACITY = "capacity";
	
	public int capacity = 5; //Default to 5 for 'canary' purposes
	
	public ItemDragonCanteen(ExtraItem extraItem)
	{
		super();
		
		Integer oCapacity = extraItem.getInteger(EI_CAPACITY);
		if(oCapacity != null)
			this.capacity = oCapacity.intValue();
		
		addPropertyOverride(new ResourceLocation("contain"), new IItemPropertyGetter()
		{
			@Override
			@SideOnly(Side.CLIENT)
			public float apply(ItemStack stack, World worldIn, EntityLivingBase entityIn)
			{
				if(stack.getItem() instanceof IItemCanteen)
				{
					IItemCanteen canteen = (IItemCanteen)stack.getItem();
					if(!canteen.isCanteenEmpty(stack))
						return 1.0f;
				}
				
				return 0.0f;
			}
		});
	}
	
	@Override
	public int getMaxDoses(ItemStack stack)
	{
		return this.capacity;
	}
	
	@Override
	public String getUnlocalizedName(ItemStack stack)
	{
		if(isCanteenEmpty(stack))
			return "item."+SimpleDifficulty.MODID+":"+"dragon_canteen_empty";
		
		int type = getTypeTag(stack).getInt();
		if(type>=ThirstEnum.values().length)
			return "item."+SimpleDifficulty.MODID+":"+"dragon_canteen_broken";
		
		return "item."+SimpleDifficulty.MODID+":"+"dragon_canteen_"+ThirstEnum.values()[type].toString();
	}
	
	
	//TODO redo this due to visibility changes
	
	@Override
	public void getSubItems(CreativeTabs tab, NonNullList<ItemStack> items)
	{
		if (this.isInCreativeTab(tab))
		{
			ItemStack emptyCanteen = new ItemStack(this, 1, 0);
			tryAddDose(emptyCanteen, ThirstEnum.PURIFIED);
			setCanteenEmpty(emptyCanteen);
			
			ItemStack fullCanteen = emptyCanteen.copy();
			tryAddDose(fullCanteen, ThirstEnum.PURIFIED);
			setCanteenFull(fullCanteen);
			
			customSetTypeTag(emptyCanteen);
			customSetTypeTag(fullCanteen);
			
			items.add(emptyCanteen);
			items.add(fullCanteen);
			
		}
	}
	
	//TODO Nullable is not smart here
	@Nullable
	@Override
	public ThirstEnum getThirstEnum(ItemStack stack)
	{
		//Always Purified
		return ThirstEnum.PURIFIED;
	}
	
	@Override
	public ActionResult<ItemStack> onItemRightClick(World world, EntityPlayer player, EnumHand hand)
	{
		ItemStack stack = player.getHeldItem(hand);
		
		if(!isCanteenFull(stack))
		{
			ThirstEnumBlockPos traceBlockPos = ThirstUtil.traceWater(player);
			if(traceBlockPos != null)
			{
				ThirstEnum trace = traceBlockPos.thirstEnum;
				//Clear out any purified block that got found
				if(trace==ThirstEnum.PURIFIED)
					player.world.setBlockToAir(traceBlockPos.pos);
				
				//Always purified
				tryAddDose(stack,ThirstEnum.PURIFIED);
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
	
	protected void customSetTypeTag(ItemStack stack)
	{
		stack.setTagInfo(CANTEENTYPE, new NBTTagInt(ThirstEnum.PURIFIED.ordinal()));
	}
}
