package com.charles445.simpledifficulty.block;

import java.util.Random;

import com.charles445.simpledifficulty.api.SDCapabilities;
import com.charles445.simpledifficulty.api.SDItems;
import com.charles445.simpledifficulty.api.thirst.ThirstEnum;
import com.charles445.simpledifficulty.api.thirst.ThirstUtil;
import com.charles445.simpledifficulty.item.ItemCanteen;
import com.charles445.simpledifficulty.util.SoundUtil;

import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.MapColor;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyInteger;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Items;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class BlockRainCollector extends Block
{
	/*
	 * This is essentially a faster acting cauldron
	 * As such, most of it can be found in BlockCauldron
	 * 
	 * It would be nice to just extend BlockCauldron but that's harder to manage
	 */
	
	//BlockCauldron.LEVEL
	public static final PropertyInteger LEVEL = PropertyInteger.create("level", 0, 3);
	
	//BlockCauldron()
	public BlockRainCollector()
	{
		super(Material.ROCK, MapColor.STONE);
		setDefaultState(blockState.getBaseState().withProperty(LEVEL, 0));
		setHardness(2.0f);
		setSoundType(SoundType.STONE);

		//Serene Seasons Compatibility
		setTickRandomly(true);
	}
	
	//Serene Seasons Compatibility
	@Override
	public void randomTick(World world, BlockPos pos, IBlockState state, Random random)
	{
		if (world.rand.nextInt(6) == 1 && world.isRainingAt(pos.up()))
		{
			float f = world.getBiome(pos).getTemperature(pos);

			if (world.getBiomeProvider().getTemperatureAtHeight(f, pos.getY()) >= 0.15F)
			{
				IBlockState iblockstate = world.getBlockState(pos);

				if (iblockstate.getValue(LEVEL) < 3)
				{
					world.setBlockState(pos, iblockstate.cycleProperty(LEVEL), 2);
				}
			}
		}
	}
	
	//BlockCauldron.fillWithRain
	//Incompatible with Serene Seasons...
	//I should make a PR
	//Although this'd probably need to be core modded in so maybe abandoning fillWithRain entirely is best...
	
	/*
	@Override
	public void fillWithRain(World world, BlockPos pos)
	{
		//Tweaked to be 10 times as powerful
		if (world.rand.nextInt(2) == 1)
		{
			float f = world.getBiome(pos).getTemperature(pos);

			if (world.getBiomeProvider().getTemperatureAtHeight(f, pos.getY()) >= 0.15F)
			{
				IBlockState iblockstate = world.getBlockState(pos);

				if (iblockstate.getValue(LEVEL) < 3)
				{
					world.setBlockState(pos, iblockstate.cycleProperty(LEVEL), 2);
				}
			}
		}
	}
	*/
	
	//BlockCauldron.onBlockActivated
	public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer player, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ)
	{
		//Both Sides
		
		//Heavily tweaked to only cause purified water withdrawal, and to change the items that work with it
		ItemStack itemstack = player.getHeldItem(hand);

		if (itemstack.isEmpty())
		{
			if(player.isSneaking())
			{
				int amount = state.getValue(LEVEL);
				if(amount > 0)
				{
					if(SDCapabilities.getThirstData(player).isThirsty())
					{
						SoundUtil.commonPlayPlayerSound(player, SoundEvents.ENTITY_GENERIC_DRINK);
						
						if(!world.isRemote)
						{
							//Server Side
							this.setWaterLevel(world, pos, state, amount - 1);
							ThirstUtil.takeDrink(player, ThirstEnum.PURIFIED);
						}
					}
				}
			}
			
			return true;
		}
		else
		{
			int amount = state.getValue(LEVEL);
			Item item = itemstack.getItem();
			
			//Item is what's being used
			//Amount is how much water is present in the rain collector
			
			if (item == Items.BUCKET)
			{
				//Check if contains any and server side
				if (amount > 0 && !world.isRemote)
				{
					if (!player.capabilities.isCreativeMode)
					{
						itemstack.shrink(1);

						if (itemstack.isEmpty())
						{
							player.setHeldItem(hand, ThirstUtil.createPurifiedWaterBucket());
						}
						else if (!player.inventory.addItemStackToInventory(ThirstUtil.createPurifiedWaterBucket()))
						{
							player.dropItem(ThirstUtil.createPurifiedWaterBucket(), false);
						}
						//Should this also update the player's inventory like glass bottles do?
					}
					this.setWaterLevel(world, pos, state, amount - 1);
					SoundUtil.serverPlayBlockSound(world, pos, SoundEvents.ITEM_BUCKET_FILL);
				}
				
				return true;
			}
			else if (item == Items.GLASS_BOTTLE)
			{
				if(amount > 0 && !world.isRemote)
				{
					if(!player.capabilities.isCreativeMode)
					{
						itemstack.shrink(1);
						
						if (itemstack.isEmpty())
						{
							player.setHeldItem(hand, new ItemStack(SDItems.purifiedWaterBottle));
						}
						else if (!player.inventory.addItemStackToInventory(new ItemStack(SDItems.purifiedWaterBottle)))
						{
							player.dropItem(new ItemStack(SDItems.purifiedWaterBottle), false);
						}
						else if (player instanceof EntityPlayerMP)
						{
							((EntityPlayerMP)player).sendContainerToPlayer(player.inventoryContainer);
						}
						this.setWaterLevel(world, pos, state, amount - 1);
						SoundUtil.serverPlayBlockSound(world, pos, SoundEvents.ITEM_BOTTLE_FILL);
					}
				}
				
				return true;
			}
			else if (item == SDItems.canteen)
			{
				if(amount > 0 && !world.isRemote)
				{
					if(!player.capabilities.isCreativeMode)
					{
						//TODO some sort of canteen utility? Canteen code is awful for usability
						if(itemstack.getTagCompound()!=null)
						{
							//If it's missing, getting 0 is fine
							int type = itemstack.getTagCompound().getInteger(ItemCanteen.CANTEENTYPE);
							
							if(type!=ThirstEnum.PURIFIED.ordinal())
							{
								//Replace it
								itemstack.getTagCompound().setInteger(ItemCanteen.CANTEENTYPE, ThirstEnum.PURIFIED.ordinal());
								
								//Set damage
								itemstack.setItemDamage(itemstack.getMaxDamage()-1);
								
								//Drain collector
								this.setWaterLevel(world, pos, state, amount - 1);
								
								//Play sound
								SoundUtil.serverPlayBlockSound(world, pos, SoundEvents.ITEM_BUCKET_FILL);
							}
							else if(itemstack.getItemDamage() > 0)
							{
								//It has purified water in it, and it needs another dose
								itemstack.setItemDamage(itemstack.getItemDamage()-1);
								
								//Drain collector
								this.setWaterLevel(world, pos, state, amount - 1);
								
								//Play sound
								SoundUtil.serverPlayBlockSound(world, pos, SoundEvents.ITEM_BUCKET_FILL);
							}
							
						}
					}
				}
				
				return true;
			}
			else
			{
				return false;
			}
		}
	}
	
	//BlockCauldron.setWaterLevel
	public void setWaterLevel(World world, BlockPos pos, IBlockState state, int level)
	{
		world.setBlockState(pos, state.withProperty(LEVEL, MathHelper.clamp(level, 0, 3)), 2);
		world.updateComparatorOutputLevel(pos, this);
	}
	
	// COMPARATOR
	
	//BlockCauldron.hasComparatorInputOverride
	@Override
	public boolean hasComparatorInputOverride(IBlockState state)
	{
		return true;
	}

	//BlockCauldron.getComparatorInputOverride
	@Override
	public int getComparatorInputOverride(IBlockState blockState, World worldIn, BlockPos pos)
	{
		return blockState.getValue(LEVEL);
	}

	// STATE
	
	//BlockCauldron.getStateFromMeta
	@Override
	public IBlockState getStateFromMeta(int meta)
	{
		return getDefaultState().withProperty(LEVEL, meta);
	}

	//BlockCauldron.getMetaFromState
	@Override
	public int getMetaFromState(IBlockState state)
	{
		return state.getValue(LEVEL);
	}

	//BlockCauldron.createBlockState
	@Override
	protected BlockStateContainer createBlockState()
	{
		return new BlockStateContainer(this, new IProperty[] {LEVEL});
	}
	
	// RENDER
	
	//BlockCauldron.isOpaqueCube
	@Override
	public boolean isOpaqueCube(IBlockState state)
	{
		return false;
	}

  	//BlockCauldron.isFullCube
  	@Override
  	public boolean isFullCube(IBlockState state)
  	{
  		return false;
  	}
  	
  	@SideOnly(Side.CLIENT)
	@Override
  	public BlockRenderLayer getBlockLayer()
	{
		return BlockRenderLayer.CUTOUT;
	}
}
