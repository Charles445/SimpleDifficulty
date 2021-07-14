package com.charles445.simpledifficulty.compat.mod;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import com.charles445.simpledifficulty.SimpleDifficulty;
import com.charles445.simpledifficulty.api.SDDamageSources;
import com.charles445.simpledifficulty.util.ReflectUtil;
import com.charles445.simpledifficulty.util.VersionDelimiter;

import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.util.DamageSource;

public class FirstAidCompat
{
	private Class c_FirstAid;
	private Field f_FirstAid_VERSION;
	private VersionDelimiter version;
	
	private Class c_IStandardDamageDistributionBuilder;
	private Method m_IStandardDamageDistributionBuilder_addDistributionLayer;
	private Method m_IStandardDamageDistributionBuilder_disableNeighbourRestDistribution;
	private Method m_IStandardDamageDistributionBuilder_ignoreOrder;
	
	//private Class c_IDamageDistributionBuilder;
	//private Method m_IDamageDistributionBuilder_registerStatic;
	
	private Class c_DamageDistributionBuilderFactory;
	private Method m_DamageDistributionBuilderFactory_getInstance;
	private Method m_DamageDistributionBuilderFactory_newStandardBuilder;
	
	private Class c_EnumPlayerPart;
	private Object o_EnumPlayerPart_BODY;
	private Object o_EnumPlayerPart_HEAD;
	
	private Class c_BaseDamageDistributionBuilder;
	//private Method m_BaseDamageDistributionBuilder_registerStatic;
	private Method m_BaseDamageDistributionBuilder_build;
	
	private Class c_IDamageDistribution;
	
	private Class c_FirstAidRegistryImpl;
	private Field f_FirstAidRegistryImpl_INSTANCE;
	private Method m_FirstAidRegistryImpl_registerDistribution_Array;
	
	
	public FirstAidCompat()
	{
		try
		{
			c_FirstAid = Class.forName("ichttt.mods.firstaid.FirstAid");
			f_FirstAid_VERSION = ReflectUtil.findField(c_FirstAid, "VERSION");
			
			String versionStr = (String) f_FirstAid_VERSION.get(null);
			version = new VersionDelimiter(versionStr);
			
			if(version.isSameOrNewerVersion(1, 6, 16))
			{
				c_IStandardDamageDistributionBuilder = Class.forName("ichttt.mods.firstaid.api.distribution.IStandardDamageDistributionBuilder");
				m_IStandardDamageDistributionBuilder_addDistributionLayer = ReflectUtil.findMethod(c_IStandardDamageDistributionBuilder, "addDistributionLayer");
				m_IStandardDamageDistributionBuilder_disableNeighbourRestDistribution = ReflectUtil.findMethod(c_IStandardDamageDistributionBuilder, "disableNeighbourRestDistribution");
				m_IStandardDamageDistributionBuilder_ignoreOrder = ReflectUtil.findMethod(c_IStandardDamageDistributionBuilder, "ignoreOrder");
				
				//c_IDamageDistributionBuilder = Class.forName("ichttt.mods.firstaid.api.distribution.IDamageDistributionBuilder");
				//m_IDamageDistributionBuilder_registerStatic = ReflectUtil.findMethod(c_IDamageDistributionBuilder, "registerStatic");
				
				c_DamageDistributionBuilderFactory = Class.forName("ichttt.mods.firstaid.api.distribution.DamageDistributionBuilderFactory");
				m_DamageDistributionBuilderFactory_getInstance = ReflectUtil.findMethod(c_DamageDistributionBuilderFactory, "getInstance");
				m_DamageDistributionBuilderFactory_newStandardBuilder = ReflectUtil.findMethod(c_DamageDistributionBuilderFactory, "newStandardBuilder");
				
				c_EnumPlayerPart = Class.forName("ichttt.mods.firstaid.api.enums.EnumPlayerPart");
				o_EnumPlayerPart_BODY = ReflectUtil.findField(c_EnumPlayerPart, "BODY").get(null);
				o_EnumPlayerPart_HEAD = ReflectUtil.findField(c_EnumPlayerPart, "HEAD").get(null);
				
				c_BaseDamageDistributionBuilder = Class.forName("ichttt.mods.firstaid.common.apiimpl.distribution.BaseDamageDistributionBuilder");
				//m_BaseDamageDistributionBuilder_registerStatic = ReflectUtil.findMethod(c_BaseDamageDistributionBuilder, "registerStatic");
				m_BaseDamageDistributionBuilder_build = ReflectUtil.findMethod(c_BaseDamageDistributionBuilder, "build");
				
				c_IDamageDistribution = Class.forName("ichttt.mods.firstaid.api.IDamageDistribution");
				
				c_FirstAidRegistryImpl = Class.forName("ichttt.mods.firstaid.common.apiimpl.FirstAidRegistryImpl");
				f_FirstAidRegistryImpl_INSTANCE = ReflectUtil.findField(c_FirstAidRegistryImpl, "INSTANCE");
				m_FirstAidRegistryImpl_registerDistribution_Array = c_FirstAidRegistryImpl.getDeclaredMethod("registerDistribution", DamageSource[].class, c_IDamageDistribution);
				m_FirstAidRegistryImpl_registerDistribution_Array.setAccessible(true);
				
				//Run the setup
				registerDamageSources();
			}
			else
			{
				SimpleDifficulty.logger.info("FirstAid version is lower than 1.6.16, compatibility was skipped");
			}
			
		}
		catch(Exception e)
		{
			SimpleDifficulty.logger.error("FirstAidCompat reflection failed! First Aid compatibility is now disabled!",e);
		}
	}
	
	private void registerDamageSources() throws IllegalAccessException, IllegalArgumentException, InvocationTargetException
	{
		Object factory = m_DamageDistributionBuilderFactory_getInstance.invoke(null);
		
		if (factory == null)
		{
			SimpleDifficulty.logger.error("FirstAid DamageDistributionBuilderFactory not found!");
			return;
		}
		
		//Create builders
		Object builder = newStandardBuilder(factory);
		addDistributionLayer(builder, EntityEquipmentSlot.CHEST, o_EnumPlayerPart_BODY);
		addDistributionLayer(builder, EntityEquipmentSlot.HEAD, o_EnumPlayerPart_HEAD);
		ignoreOrder(builder);
		disableNeighbourRestDistribution(builder);
		registerStatic(builder, SDDamageSources.DEHYDRATION);
		
		SimpleDifficulty.logger.info("FirstAid Dehydration Registered");
		
	}
	
	private <O> O[] array(O... objs)
	{
		return objs;
	}
	
	//Builder
	private Object addDistributionLayer(Object builder, EntityEquipmentSlot ees, Object... enumPlayerParts) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException
	{
		return m_IStandardDamageDistributionBuilder_addDistributionLayer.invoke(builder, ees, ReflectUtil.createCastedArray(c_EnumPlayerPart, enumPlayerParts));
	}
	
	private Object disableNeighbourRestDistribution(Object builder) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException
	{
		return m_IStandardDamageDistributionBuilder_disableNeighbourRestDistribution.invoke(builder);
	}
	
	private Object ignoreOrder(Object builder) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException
	{
		return m_IStandardDamageDistributionBuilder_ignoreOrder.invoke(builder);
	}
	
	private void registerStatic(Object builder, DamageSource... sources) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException
	{
		//Due to diamond inheritance of IDamageDistributionBuilder, or some bizarre java issue
		//I can't actually use the API here via reflection
		//So I'm going to replicate the desired effect instead
		if(c_BaseDamageDistributionBuilder.isInstance(builder))
		{
			Object instance = f_FirstAidRegistryImpl_INSTANCE.get(null);
			Object built = m_BaseDamageDistributionBuilder_build.invoke(builder);
			m_FirstAidRegistryImpl_registerDistribution_Array.invoke(instance, sources, built);
		}
		else
		{
			throw new RuntimeException("Builder is not supported");
		}
	}
	
	//Factory
	private Object newStandardBuilder(Object factory) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException
	{
		return m_DamageDistributionBuilderFactory_newStandardBuilder.invoke(factory);
	}
	
	
}
