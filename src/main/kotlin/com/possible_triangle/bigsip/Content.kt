package com.possible_triangle.bigsip

import com.possible_triangle.bigsip.effect.DrunkEffect
import com.possible_triangle.bigsip.item.Alcohol
import com.possible_triangle.bigsip.item.Drink
import net.minecraft.item.Item
import net.minecraft.item.ItemGroup
import net.minecraftforge.registries.ForgeRegistries
import thedarkcolour.kotlinforforge.forge.KDeferredRegister
import thedarkcolour.kotlinforforge.forge.MOD_BUS

object Content {

    private val BLOCKS = KDeferredRegister(ForgeRegistries.BLOCKS, BigSip.MOD_ID)
    private val ITEMS = KDeferredRegister(ForgeRegistries.ITEMS, BigSip.MOD_ID)
    private val EFFECTS = KDeferredRegister(ForgeRegistries.POTIONS, BigSip.MOD_ID)

    val TAB = ItemGroup.TAB_FOOD!!

    val DRUNK by EFFECTS.registerObject("drunk") { DrunkEffect() }

    val GRAPES by ITEMS.registerObject("grapes") { Item(Item.Properties().tab(TAB)) }

    val APPLE_JUICE by ITEMS.registerObject("apple_juice") { Drink(4, 0.5F) }
    val CARROT_JUICE by ITEMS.registerObject("carrot_juice") { Drink(4, 0.5F) }
    val TOMATO_JUICE by ITEMS.registerObject("tomato_juice") { Drink(4, 0.5F) }

    val WINE_BOTTLE_FULL by ITEMS.registerObject("wine_bottle_full") { Alcohol(4, 0F, 5, uses = 3) }

    val BEER by ITEMS.registerObject("beer") { Alcohol(4, 0.2F,6, uses = 2) }
    val DARK_BEER by ITEMS.registerObject("dark_beer") { Alcohol(4, 0.2F,12, uses = 2) }

    fun register() {
        BLOCKS.register(MOD_BUS)
        ITEMS.register(MOD_BUS)
        EFFECTS.register(MOD_BUS)
    }

}