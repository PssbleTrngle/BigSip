package com.possible_triangle.bigsip.item

import com.possible_triangle.bigsip.BigSip
import com.possible_triangle.bigsip.Content
import com.possible_triangle.bigsip.config.Configs
import net.minecraft.advancements.CriteriaTriggers
import net.minecraft.entity.LivingEntity
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.entity.player.ServerPlayerEntity
import net.minecraft.item.Item
import net.minecraft.item.ItemStack
import net.minecraft.item.Items
import net.minecraft.item.UseAction
import net.minecraft.potion.EffectInstance
import net.minecraft.stats.Stats
import net.minecraft.util.ActionResult
import net.minecraft.util.DrinkHelper
import net.minecraft.util.Hand
import net.minecraft.world.World
import net.minecraftforge.fml.common.Mod
import toughasnails.api.potion.TANEffects
import toughasnails.api.thirst.ThirstHelper

@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.FORGE)
open class Drink(
    private val thirst: Int,
    private val hydration: Float,
    private val poisonChance: Float = 0.0F,
    private val canAlwaysDrink: Boolean = false,
    container: Item? = Items.GLASS_BOTTLE,
    uses: Int = 1,
) : Item(createProperties(uses, container)) {

    private companion object {
        fun createProperties(uses: Int, container: Item?): Properties {
            return with(Content.Properties) {
                if (container != null) craftRemainder(container)
                if (uses > 1) durability(uses).setNoRepair()
                else stacksTo(16)
                this
            }
        }
    }

    override fun getUseAnimation(p_77661_1_: ItemStack): UseAction {
        return UseAction.DRINK
    }

    override fun use(world: World, player: PlayerEntity, hand: Hand): ActionResult<ItemStack> {
        return if (ThirstHelper.canDrink(player, canAlwaysDrink || Configs.SERVER.CAN_ALWAYS_DRINK_ALCOHOL.get()))
            DrinkHelper.useDrink(world, player, hand)
        else
            ActionResult.pass(player.getItemInHand(hand))
    }

    override fun getUseDuration(stack: ItemStack): Int {
        return 32
    }

    override fun isEnchantable(stack: ItemStack): Boolean {
        return Configs.SERVER.ENCHANTABLE_DRINKS.get()
    }

    override fun finishUsingItem(stack: ItemStack, world: World, entity: LivingEntity): ItemStack {
        if (entity !is PlayerEntity) return stack

        if (entity is ServerPlayerEntity) {
            CriteriaTriggers.CONSUME_ITEM.trigger(entity, stack)
        }

        val thirst = ThirstHelper.getThirst(entity)
        thirst.addThirst(this.thirst)
        thirst.addHydration(this.hydration)
        if (entity.level.random.nextFloat() < this.poisonChance) {
            entity.addEffect(EffectInstance(TANEffects.THIRST, 600))
        }

        entity.awardStat(Stats.ITEM_USED[this])

        return if (!entity.isCreative) {

            val empty = if (stack.isDamageableItem) {
                BigSip.LOGGER.info("Damageable ${stack.maxDamage}")
                stack.damageValue = stack.damageValue + 1
                stack.damageValue >= stack.maxDamage
            } else {
                BigSip.LOGGER.info("Stackable")
                stack.shrink(1)
                stack.isEmpty
            }

            val newStack = if (empty) {
                if (stack.hasContainerItem()) stack.containerItem else ItemStack.EMPTY
            } else stack

            BigSip.LOGGER.info("Drunk ${stack.item.registryName}")
            BigSip.LOGGER.info("Damage: ${stack.damageValue}")
            BigSip.LOGGER.info("Count: ${stack.count}")
            BigSip.LOGGER.info("Empty: ${stack.isEmpty}")

            newStack

        } else stack
    }
}