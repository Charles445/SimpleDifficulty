package com.charles445.simpledifficulty.compat;

import com.charles445.simpledifficulty.SimpleDifficulty;
import com.charles445.simpledifficulty.api.SDDamageSources;
import ichttt.mods.firstaid.api.distribution.DamageDistributionBuilderFactory;
import ichttt.mods.firstaid.api.enums.EnumPlayerPart;
import net.minecraft.inventory.EntityEquipmentSlot;

public class FirstAidCompat
{

    public static void init()
    {
        DamageDistributionBuilderFactory factory = DamageDistributionBuilderFactory.getInstance();
        if (factory == null)
        {
            SimpleDifficulty.logger.error("FirstAid DamageDistributionBuilderFactory not found!");
            return;
        }

        factory.newStandardBuilder()
                .addDistributionLayer(EntityEquipmentSlot.CHEST, EnumPlayerPart.BODY)
                .addDistributionLayer(EntityEquipmentSlot.HEAD, EnumPlayerPart.HEAD)
                .ignoreOrder()
                .disableNeighbourRestDistribution()
                .registerStatic(SDDamageSources.DEHYDRATION);
    }
}
