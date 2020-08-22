package com.charles445.simpledifficulty.api;

import net.minecraft.util.DamageSource;

public class SDDamageSources
{
	public static final DamageSource DEHYDRATION = (new DamageSource("dehydration")).setDamageBypassesArmor().setDamageIsAbsolute();
	public static final DamageSource HYPERTHERMIA = (new DamageSource("hyperthermia")).setDamageBypassesArmor();
	public static final DamageSource HYPOTHERMIA = (new DamageSource("hypothermia")).setDamageBypassesArmor();
	public static final DamageSource PARASITES = (new DamageSource("parasites")).setDamageBypassesArmor().setMagicDamage();
}
