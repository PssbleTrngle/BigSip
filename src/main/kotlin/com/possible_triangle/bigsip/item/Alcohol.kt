package com.possible_triangle.bigsip.item

import com.possible_triangle.bigsip.BigSip
import com.possible_triangle.bigsip.alcohol.AlcoholHelper
import net.minecraft.network.chat.Component
import net.minecraft.network.chat.TranslatableComponent
import net.minecraft.world.entity.LivingEntity
import net.minecraft.world.item.Item
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.Items
import net.minecraft.world.item.TooltipFlag
import net.minecraft.world.level.Level
import net.minecraft.world.level.material.Fluid

class Alcohol(
    getFluid: () -> Fluid,
    thirst: Int,
    hydration: Float,
    private val percentage: Int,
    canAlwaysDrink: Boolean = false,
    container: Item? = Items.GLASS_BOTTLE,
    uses: Int = 1,
) : Drink(getFluid, thirst, hydration, percentage / 10F, canAlwaysDrink, container, uses) {

    override fun finishUsingItem(stack: ItemStack, world: Level, entity: LivingEntity): ItemStack {
        AlcoholHelper.applyAlcohol(entity, percentage)
        return super.finishUsingItem(stack, world, entity)
    }

    override fun appendHoverText(
        stack: ItemStack,
        world: Level?,
        tooltip: MutableList<Component>,
        flag: TooltipFlag,
    ) {
        tooltip.add(TranslatableComponent("${BigSip.MOD_ID}.tooltip.alcohol.percentage", percentage))
        super.appendHoverText(stack, world, tooltip, flag)
    }
}