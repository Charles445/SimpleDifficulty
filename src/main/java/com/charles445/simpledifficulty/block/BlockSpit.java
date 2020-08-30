package com.charles445.simpledifficulty.block;

import java.util.Random;

import javax.annotation.Nullable;

import com.charles445.simpledifficulty.api.SDBlocks;
import com.charles445.simpledifficulty.tileentity.TileEntitySpit;

import net.minecraft.block.Block;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.MapColor;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class BlockSpit extends BlockContainer
{
	//TODO tune Y (although this seems fine)
	private static final AxisAlignedBB HITBOX = new AxisAlignedBB(0.0D, 0.0D, 0.0D, 1.0D, 0.25D, 1.0D);
	
	public BlockSpit()
	{
		super(Material.WOOD, MapColor.WOOD);
		setHardness(0.5f);
		setSoundType(SoundType.WOOD);
		setTickRandomly(true);
	}

	@Override
	public TileEntity createNewTileEntity(World worldIn, int meta)
	{
		return new TileEntitySpit();
	}
	
	//Block handling
	@Override
	public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer player, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ)
	{
		if(world.isRemote)
			return true;
		
		//Server Side
		
		TileEntity te = world.getTileEntity(pos);
		if(te instanceof TileEntitySpit)
		{
			((TileEntitySpit)te).handleRightClick(world, pos, state, player, hand, facing, hitX, hitY, hitZ);
		}
		
		//Always return true
		return true;
	}
	
	//Block placing and removing
	
	@Override
	public void updateTick(World world, BlockPos pos, IBlockState state, Random rand)
	{
		checkCampfireOrDestroy(world, pos, state);
	}
	
	@Override
	public boolean canPlaceBlockAt(World world, BlockPos pos)
	{
		return hasCampfire(world, pos) && super.canPlaceBlockAt(world, pos);
	}
	
	@Override
	public void observedNeighborChange(IBlockState observerState, World world, BlockPos observerPos, Block changedBlock, BlockPos changedBlockPos)
	{
		super.observedNeighborChange(observerState, world, observerPos, changedBlock, changedBlockPos);
		checkCampfireOrDestroy(world, observerPos, observerState);
		//DebugUtil.messageAll("OB: "+observerState.getBlock().toString());
		//DebugUtil.messageAll("CB: "+changedBlock.toString());
	}
	
	public boolean hasCampfire(World world, BlockPos pos)
	{
		return world.getBlockState(pos.down()).getBlock() == SDBlocks.campfire;
	}
	
	public void checkCampfireOrDestroy(World world, BlockPos pos, IBlockState state)
	{
		if(!hasCampfire(world, pos))
		{
			this.dropBlockAsItem(world, pos, state, 0);
			world.setBlockState(pos, Blocks.AIR.getDefaultState());
		}
	}
	
	@Override
	public void breakBlock(World world, BlockPos pos, IBlockState state)
	{
		//Need to check the tile entity and drop its items first
		TileEntity te = world.getTileEntity(pos);
		if(te instanceof TileEntitySpit)
		{
			((TileEntitySpit) te).dumpItems(world, pos);
			((TileEntitySpit) te).dumpExperience(world, pos);
		}
		
		super.breakBlock(world, pos, state);
	}
	
	
	//COLLISION
	
	@Override
	public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos)
	{
		return HITBOX;
	}
	
	@Nullable
	@Override
	public AxisAlignedBB getCollisionBoundingBox(IBlockState blockState, IBlockAccess world, BlockPos pos)
	{
		return Block.NULL_AABB;
	}
	
	//RENDER
	
	@Override
	public boolean isFullCube(IBlockState state) { return false; }
	@Override
	public boolean isOpaqueCube(IBlockState state){ return false; }
	
	@Override
	public EnumBlockRenderType getRenderType(IBlockState state)
 	{
		return EnumBlockRenderType.MODEL;
	}
	
	@SideOnly(Side.CLIENT)
	@Override
  	public BlockRenderLayer getBlockLayer()
	{
		return BlockRenderLayer.CUTOUT;
	}
}
