package com.possible_triangle.bigsip

import com.possible_triangle.bigsip.BigSip.MOD_ID
import com.possible_triangle.bigsip.block.GrapeCrop
import com.possible_triangle.bigsip.block.MaturingBarrel
import com.possible_triangle.bigsip.block.tile.MaturingBarrelTile
import com.possible_triangle.bigsip.effect.DizzinessEffect
import com.possible_triangle.bigsip.fluid.Juice
import com.possible_triangle.bigsip.item.Alcohol
import com.possible_triangle.bigsip.item.Drink
import com.possible_triangle.bigsip.recipe.MaturingRecipe
import com.simibubi.create.content.contraptions.processing.ProcessingRecipeSerializer
import com.simibubi.create.foundation.block.connected.AllCTTypes
import com.simibubi.create.foundation.block.connected.CTSpriteShiftEntry
import com.simibubi.create.foundation.block.connected.CTSpriteShifter
import net.minecraft.core.Registry
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.item.*
import net.minecraft.world.item.crafting.Recipe
import net.minecraft.world.item.crafting.RecipeType
import net.minecraft.world.level.block.entity.BlockEntityType
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

    val ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, MOD_ID)
    private val BLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS, MOD_ID)
    private val TILES = DeferredRegister.create(ForgeRegistries.BLOCK_ENTITIES, MOD_ID)
    private val EFFECTS = DeferredRegister.create(ForgeRegistries.MOB_EFFECTS, MOD_ID)
    private val FLUIDS = DeferredRegister.create(ForgeRegistries.FLUIDS, MOD_ID)
    private val RECIPES = DeferredRegister.create(Registry.RECIPE_TYPE_REGISTRY, MOD_ID)
    private val RECIPE_SERIALIZERS = DeferredRegister.create(ForgeRegistries.RECIPE_SERIALIZERS, MOD_ID)

    fun register() {
        listOf(FLUIDS, ITEMS, BLOCKS, TILES, EFFECTS, RECIPES, RECIPE_SERIALIZERS).forEach {
            it.register(MOD_BUS)
        }
    }

    val DIZZYNESS by EFFECTS.registerObject("dizziness") { DizzinessEffect() }

    val BARREL by BLOCKS.registerObject("maturing_barrel") { MaturingBarrel() }
    val BARREL_ITEM by ITEMS.registerObject("maturing_barrel") { BlockItem(BARREL, Properties) }
    val BARREL_TILE by TILES.registerObject("maturing_barrel") {
        BlockEntityType.Builder.of(::MaturingBarrelTile, BARREL).build(null)
    }

    val BARREL_CT_FRONT = createBarrelCT("front")
    val BARREL_CT_SIDE = createBarrelCT("side")
    val BARREL_CT_TOP = createBarrelCT("top")
    val BARREL_CT_BOTTOM = createBarrelCT("bottom")

    private fun createBarrelCT(side: String): CTSpriteShiftEntry {
        return CTSpriteShifter.getCT(AllCTTypes.CROSS, ResourceLocation(MOD_ID, "block/maturing_barrel_$side"), ResourceLocation(MOD_ID,"block/maturing_barrel_${side}_connected"));
    }

    val MATURING_RECIPE = RECIPES.register(MaturingRecipe.ID) { createRecipeType<MaturingRecipe>(MaturingRecipe.ID) }
    val MATURING_RECIPE_SERIALIZER by RECIPE_SERIALIZERS.registerObject(MaturingRecipe.ID) { ProcessingRecipeSerializer(::MaturingRecipe) }

    private fun <T : Recipe<*>> createRecipeType(id: String): RecipeType<T> {
        return object : RecipeType<T> {
            override fun toString() = "${MOD_ID}:$id"
        }
    }

    val GRAPES by ITEMS.registerObject("grapes") { Item(Properties) }
    val GRAPE_SAPLING by ITEMS.registerObject("grape_sapling") { Item(Properties) }
    val GRAPE_CROP by BLOCKS.registerObject("grapes") { GrapeCrop() }

    val APPLE_JUICE by withFluid("apple_juice") { Drink(it, 4, 0.5F) }
    val CARROT_JUICE by withFluid("carrot_juice") { Drink(it, 4, 0.5F) }
    val GRAPE_JUICE by withFluid("grape_juice") { Drink(it, 4, 0.5F) }

    val WINE_BOTTLE by withFluid("wine", "wine_bottle") { Alcohol(it, 4, 0F, 5, uses = 3) }
    val BEER by withFluid("beer") { Alcohol(it, 4, 0.2F, 6, uses = 2) }
    val DARK_BEER by withFluid("dark_beer") { Alcohol(it, 4, 0.2F, 12, uses = 2) }

    val DRINKS
        get() = ITEMS.entries.mapNotNull { it.get() }.filterIsInstance<Drink>()

    private fun <I : Drink> withFluid(
        id: String,
        itemId: String = id,
        itemSupplier: (() -> Fluid) -> I,
    ): ReadOnlyProperty<Any?, I> {

        lateinit var bucket: RegistryObject<Item>
        lateinit var source: RegistryObject<Fluid>
        lateinit var flowing: RegistryObject<Fluid>

        source = FLUIDS.register(id) { Juice(id, true, bucket::get, flowing::get, source::get) }
        flowing = FLUIDS.register("${id}_flow") { Juice(id, false, bucket::get, flowing::get, source::get) }

        //BLOCKS.registerObject(id) { FlowingFluidBlock(source::get, AbstractBlock.Properties.copy(Blocks.WATER)) }

        bucket = ITEMS.register("${id}_bucket") {
            BucketItem(
                { source.get() },
                Item.Properties().tab(CreativeModeTab.TAB_MISC).craftRemainder(Items.BUCKET).stacksTo(1)
            )
        }

        return ITEMS.registerObject(itemId) {
            itemSupplier { source.get() }
        }
    }

}