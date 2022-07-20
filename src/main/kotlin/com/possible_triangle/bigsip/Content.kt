package com.possible_triangle.bigsip

import com.possible_triangle.bigsip.block.GrapeCrop
import com.possible_triangle.bigsip.effect.DizzinessEffect
import com.possible_triangle.bigsip.fluid.Juice
import com.possible_triangle.bigsip.item.Alcohol
import com.possible_triangle.bigsip.item.Drink
import net.minecraft.world.item.BucketItem
import net.minecraft.world.item.CreativeModeTab
import net.minecraft.world.item.Item
import net.minecraft.world.item.Items
import net.minecraft.world.level.material.FlowingFluid
import net.minecraft.world.level.material.Fluid
import net.minecraftforge.registries.DeferredRegister
import net.minecraftforge.registries.ForgeRegistries
import net.minecraftforge.registries.RegistryObject
import thedarkcolour.kotlinforforge.forge.MOD_BUS
import thedarkcolour.kotlinforforge.forge.registerObject
import kotlin.properties.ReadOnlyProperty

object Content {

    private val TAB = CreativeModeTab.TAB_FOOD
    val Properties: Item.Properties
        get() = Item.Properties().tab(TAB)

    val ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, BigSip.MOD_ID)
    private val BLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS, BigSip.MOD_ID)
    private val EFFECTS = DeferredRegister.create(ForgeRegistries.MOB_EFFECTS, BigSip.MOD_ID)
    private val FLUIDS = DeferredRegister.create(ForgeRegistries.FLUIDS, BigSip.MOD_ID)

    fun register() {
        listOf(ITEMS, BLOCKS, EFFECTS, FLUIDS).forEach {
            it.register(MOD_BUS)
        }
    }

    val DIZZYNESS by EFFECTS.registerObject("dizziness") { DizzinessEffect() }

    val GRAPES by ITEMS.registerObject("grapes") { Item(Properties) }
    val GRAPE_SAPLING by ITEMS.registerObject("grape_sapling") { Item(Properties) }
    val GRAPE_CROP by BLOCKS.registerObject("grapes") { GrapeCrop() }

    val APPLE_JUICE by withFluid("apple_juice") { Drink(4, 0.5F) }
    val CARROT_JUICE by withFluid("carrot_juice") { Drink(4, 0.5F) }
    val GRAPE_JUICE by withFluid("grape_juice") { Drink(4, 0.5F) }

    val WINE_BOTTLE by withFluid("wine", "wine_bottle") { Alcohol(4, 0F, 5, uses = 3) }
    val BEER by withFluid("beer") { Alcohol(4, 0.2F, 6, uses = 2) }
    val DARK_BEER by withFluid("dark_beer") { Alcohol(4, 0.2F, 12, uses = 2) }

    private fun <I : Item> withFluid(
        id: String,
        itemId: String = id,
        itemSupplier: () -> I,
    ): ReadOnlyProperty<Any?, ItemAndFluid<I>> {
        val item = ITEMS.registerObject(itemId, itemSupplier)

        lateinit var bucket: RegistryObject<Item>
        lateinit var source: RegistryObject<FlowingFluid>
        lateinit var flowing: RegistryObject<FlowingFluid>

        source = FLUIDS.register(id) { Juice(id, true, bucket::get, flowing::get, source::get) }
        flowing = FLUIDS.register("${id}_flow") { Juice(id, false, bucket::get, flowing::get, source::get) }

        //BLOCKS.registerObject(id) { FlowingFluidBlock(source::get, AbstractBlock.Properties.copy(Blocks.WATER)) }

        bucket = ITEMS.register("${id}_bucket") {
            BucketItem(
                { source.get() },
                Item.Properties().tab(CreativeModeTab.TAB_MISC).craftRemainder(Items.BUCKET).stacksTo(1)
            )
        }

        return ReadOnlyProperty { a, p -> ItemAndFluid(item.getValue(a, p), source.get(), bucket.get()) }
    }

    data class ItemAndFluid<I : Item>(
        val item: I,
        val fluid: Fluid,
        val bucket: Item,
    )

}