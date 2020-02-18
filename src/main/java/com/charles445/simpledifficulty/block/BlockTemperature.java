package com.charles445.simpledifficulty.block;

import java.util.Random;

import com.charles445.simpledifficulty.tileentity.TileEntityTemperature;

import net.minecraft.block.Block;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class BlockTemperature extends BlockContainer
{
	public static final PropertyBool ENABLED = PropertyBool.create("enabled");
	
	private final float temperature;
	
	public BlockTemperature(float temperature)
	{
		super(Material.ROCK);
		setHardness(0.5f);
		setDefaultState(blockState.getBaseState().withProperty(ENABLED, false));
		this.temperature = temperature;
	}
	
	public float getActiveTemperatureMult()
	{
		return temperature;
	}
	
	@Override
	public void onBlockAdded(World world, BlockPos pos, IBlockState state)
	{
		if (!world.isRemote)
		{
			boolean enabled = state.getValue(ENABLED);
			boolean powered = world.isBlockPowered(pos);
			
			if (enabled && !powered)
			{
				turnOff(world,pos,state);
			}
			else if(!enabled && powered)
			{
				turnOn(world,pos,state);
			}
		}
	}
	
	@Override
	public void neighborChanged(IBlockState state, World world, BlockPos pos, Block block, BlockPos fromPos)
	{
		if (!world.isRemote)
		{
			boolean enabled = state.getValue(ENABLED);
			boolean powered = world.isBlockPowered(pos);
		
			if (enabled && !powered)
			{
				world.scheduleUpdate(pos, this, 4);
			}
			else if (!enabled && powered)
			{
				turnOn(world,pos,state);
			}
		}
	}

	@Override
	public void updateTick(World world, BlockPos pos, IBlockState state, Random rand)
	{
		if (!world.isRemote)
		{
			if (state.getValue(ENABLED) && !world.isBlockPowered(pos))
			{
				turnOff(world,pos,state);
			}
		}
	}
	
	private void turnOff(final World world, final BlockPos pos, final IBlockState state)
	{
		world.setBlockState(pos, state.withProperty(ENABLED, false), 2);
	}
	
	private void turnOn(final World world, final BlockPos pos, final IBlockState state)
	{
		world.setBlockState(pos, state.withProperty(ENABLED, true), 2);
	}
	
	/* Alternate activation where you just right click the block
	 * Decided against it because you can't use redstone
	@Override
	public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer player, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ)
	{
		//TODO Feedback?
		
		if(world.isRemote)
		{
			//Client Side
			return true;
		}
		else
		{
			//Server Side
			if(state.getValue(ENABLED))
			{
				world.setBlockState(pos, state.withProperty(ENABLED, false));
			}
			else
			{
				world.setBlockState(pos, state.withProperty(ENABLED, true));
			}
			
			return true;
		}
	}
	*/
	
	//RENDER
	
	@Override
	public EnumBlockRenderType getRenderType(IBlockState state)
 	{
		return EnumBlockRenderType.MODEL;
	}
	
	//TILE
	
	@Override
	public TileEntity createNewTileEntity(World world, int meta)
	{
		return new TileEntityTemperature();
	}
	
	//STATE
	
	@Override
	public IBlockState getStateFromMeta(int meta)
	{
		return this.getDefaultState().withProperty(ENABLED, meta > 0);
	}

	@Override
	public int getMetaFromState(IBlockState state)
	{
		return state.getValue(ENABLED) ? 1 : 0;
	}
	
	@Override
	protected BlockStateContainer createBlockState()
	{
		return new BlockStateContainer(this, new IProperty[] {ENABLED});
	}
	
	//RENDER
	
	@Override
	public boolean isOpaqueCube(IBlockState state)
	{
		return false;
	}

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
	
	@SideOnly(Side.CLIENT)
	@Override
	public float getAmbientOcclusionLightValue(IBlockState state)
	{
		return 1.0F;
	}
	
	@Override
	public int getLightValue(IBlockState state, IBlockAccess world, BlockPos pos)
	{
		if(state.getValue(ENABLED))
		{
			return 1;
		}
		else
		{
			return 0;
		}
	}
	
}
