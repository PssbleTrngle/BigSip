package com.possible_triangle.bigsip

import com.possible_triangle.bigsip.effect.DrunkEffect
import com.possible_triangle.bigsip.fluid.Juice
import com.possible_triangle.bigsip.item.Alcohol
import com.possible_triangle.bigsip.item.Drink
import net.minecraft.block.AbstractBlock
import net.minecraft.block.Blocks
import net.minecraft.block.FlowingFluidBlock
import net.minecraft.fluid.FlowingFluid
import net.minecraft.fluid.Fluid
import net.minecraft.item.BucketItem
import net.minecraft.item.Item
import net.minecraft.item.ItemGroup
import net.minecraft.item.Items
import net.minecraftforge.registries.ForgeRegistries
import thedarkcolour.kotlinforforge.forge.KDeferredRegister
import thedarkcolour.kotlinforforge.forge.MOD_BUS
import thedarkcolour.kotlinforforge.forge.ObjectHolderDelegate
import kotlin.properties.ReadOnlyProperty

object Content {

    private val TAB = ItemGroup.TAB_FOOD!!
    val Properties: Item.Properties
        get() = Item.Properties().tab(TAB)

    val ITEMS = KDeferredRegister(ForgeRegistries.ITEMS, BigSip.MOD_ID)
    private val BLOCKS = KDeferredRegister(ForgeRegistries.BLOCKS, BigSip.MOD_ID)
    private val EFFECTS = KDeferredRegister(ForgeRegistries.POTIONS, BigSip.MOD_ID)
    private val FLUIDS = KDeferredRegister(ForgeRegistries.FLUIDS, BigSip.MOD_ID)

    fun register() {
        listOf(ITEMS, BLOCKS, EFFECTS, FLUIDS).forEach {
            it.register(MOD_BUS)
        }
    }

    val DRUNK by EFFECTS.registerObject("drunk") { DrunkEffect() }

    val GRAPES by ITEMS.registerObject("grapes") { Item(Properties) }

    val APPLE_JUICE by withFluid("apple_juice") { Drink(4, 0.5F) }
    val CARROT_JUICE by withFluid("carrot_juice") { Drink(4, 0.5F) }
    val TOMATO_JUICE by withFluid("tomato_juice") { Drink(4, 0.5F) }

    val WINE_BOTTLE by withFluid("wine", "wine_bottle") { Alcohol(4, 0F, 5, uses = 3) }
    val BEER by withFluid("beer") { Alcohol(4, 0.2F, 6, uses = 2) }
    val DARK_BEER by withFluid("dark_beer") { Alcohol(4, 0.2F, 12, uses = 2) }

    private fun <I : Item> withFluid(
        id: String,
        itemId: String = id,
        itemSupplier: () -> I,
    ): ReadOnlyProperty<Any?, ItemAndFluid<I>> {
        val item = ITEMS.registerObject(itemId, itemSupplier)

        lateinit var bucket: ObjectHolderDelegate<Item>
        lateinit var source: ObjectHolderDelegate<FlowingFluid>
        lateinit var flowing: ObjectHolderDelegate<FlowingFluid>

        source = FLUIDS.registerObject(id) { Juice(id, true, bucket::get, flowing::get, source::get) }
        flowing = FLUIDS.registerObject(id) { Juice(id, false, bucket::get, flowing::get, source::get) }

        BLOCKS.registerObject(id) { FlowingFluidBlock(flowing::get, AbstractBlock.Properties.copy(Blocks.WATER)) }

        bucket = ITEMS.registerObject("${id}_bucket") {
            BucketItem(
                source,
                Item.Properties().tab(ItemGroup.TAB_MISC).craftRemainder(Items.BUCKET).stacksTo(1)
            )
        }

        return ReadOnlyProperty { _, _ -> ItemAndFluid(item.get(), source.get(), bucket.get()) }
    }

    data class ItemAndFluid<I : Item>(
        val item: I,
        val fluid: Fluid,
        val bucket: Item,
    )

}