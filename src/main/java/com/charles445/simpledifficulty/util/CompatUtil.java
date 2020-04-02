package com.charles445.simpledifficulty.util;

import java.lang.reflect.Method;

import com.charles445.simpledifficulty.SimpleDifficulty;
import com.charles445.simpledifficulty.api.SDCompatibility;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.ModContainer;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.ReflectionHelper;

public class CompatUtil
{
	private static CompatUtil instance;
	
	//EventBus
	private Class EventBus;
	private Method m_register;
	
	public static boolean canUseMod(String modid)
	{
		return Loader.isModLoaded(modid) && !SDCompatibility.disabledCompletely.contains(modid);
	}
	
	
	/** Manual EVENT_BUS registering 
	 * Don't forget to add the SubscribeEvent annotation
	 * But do not register the whole class to the event bus!
	 * 
	 * @param clazz (Event) Event Class
	 * @param thiz  (this) Event Handler Object
	 * @param thiz_toCall (this.onEvent(Object event)) Method to be invoked
	 * @return
	 */
	public static boolean subscribeEventManually(Class<?> clazz, Object thiz, Method thiz_toCall)
	{
		try
		{
			if(instance==null)
			{
				instance = new CompatUtil();
				instance.EventBus = Class.forName("net.minecraftforge.fml.common.eventhandler.EventBus");
				instance.m_register = instance.EventBus.getDeclaredMethod("register", Class.class, Object.class, Method.class, ModContainer.class);
				instance.m_register.setAccessible(true);
			}
			
			if(!thiz_toCall.isAnnotationPresent(SubscribeEvent.class))
			{
				SimpleDifficulty.logger.error("Method needs a SubscribeEvent annotation, go add one.");
				return false;
			}
			
			instance.m_register.invoke(MinecraftForge.EVENT_BUS, clazz, thiz, thiz_toCall, Loader.instance().getMinecraftModContainer());
			
			return true;
		}
		catch(Exception e)
		{
			SimpleDifficulty.logger.error("subscribeEventManually error.",e);
			return false;
		}
	}
}
