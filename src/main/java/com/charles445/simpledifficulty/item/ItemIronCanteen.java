package com.charles445.simpledifficulty.item;

import com.charles445.simpledifficulty.SimpleDifficulty;
import com.charles445.simpledifficulty.api.config.ServerConfig;
import com.charles445.simpledifficulty.api.config.ServerOptions;
import com.charles445.simpledifficulty.api.item.IItemCanteen;
import com.charles445.simpledifficulty.api.thirst.ThirstEnum;
import com.charles445.simpledifficulty.config.json.ExtraItem;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.IItemPropertyGetter;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class ItemIronCanteen extends ItemCanteen
{
	public ItemIronCanteen()
	{
		super();
		
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
		return ServerConfig.instance.getInteger(ServerOptions.IRON_CANTEEN_DOSES);
	}
	
	@Override
	public String getUnlocalizedName(ItemStack stack)
	{
		if(isCanteenEmpty(stack))
			return "item."+SimpleDifficulty.MODID+":"+"iron_canteen_empty";
		
		int type = getTypeTag(stack).getInt();
		if(type>=ThirstEnum.values().length)
			return "item."+SimpleDifficulty.MODID+":"+"iron_canteen_broken";
		
		return "item."+SimpleDifficulty.MODID+":"+"iron_canteen_"+ThirstEnum.values()[type].toString();
	}
}
