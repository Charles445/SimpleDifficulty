package com.charles445.simpledifficulty.asm;

import java.util.Map;

import com.charles445.simpledifficulty.asm.helper.ObfHelper;

import net.minecraftforge.fml.relauncher.IFMLLoadingPlugin;

@IFMLLoadingPlugin.Name("SimpleDifficulty ASM")
@IFMLLoadingPlugin.SortingIndex(1001)
@IFMLLoadingPlugin.TransformerExclusions({ "com.charles445.simpledifficulty.asm", "com.charles445.simpledifficulty.asm." })
public class CoreLoader implements IFMLLoadingPlugin
{

	@Override
	public String[] getASMTransformerClass()
	{
		return new String[] { "com.charles445.simpledifficulty.asm.SimpleDifficultyASM" };
	}

	@Override
	public String getModContainerClass()
	{
		return null;
	}

	@Override
	public String getSetupClass()
	{
		return null;
	}

	@Override
	public void injectData(Map<String, Object> data)
	{
		ObfHelper.setObfuscated((Boolean) data.get("runtimeDeobfuscationEnabled"));
		ObfHelper.setRunsAfterDeobfRemapper(true);
	}

	@Override
	public String getAccessTransformerClass()
	{
		return null;
	}
	
}
