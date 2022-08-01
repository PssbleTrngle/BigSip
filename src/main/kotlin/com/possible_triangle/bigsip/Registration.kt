package com.possible_triangle.bigsip

import com.possible_triangle.bigsip.BigSip.MOD_ID
import com.possible_triangle.bigsip.data.generation.conditions.ConfigLootCondition
import com.possible_triangle.bigsip.fluid.ModFluid
import com.possible_triangle.bigsip.item.Drink
import com.possible_triangle.bigsip.modules.ModModule
import com.simibubi.create.foundation.ponder.PonderRegistrationHelper
import net.minecraft.core.Registry
import net.minecraft.resources.ResourceKey
import net.minecraft.resources.ResourceLocation
import net.minecraft.tags.TagKey
import net.minecraft.world.item.BucketItem
import net.minecraft.world.item.CreativeModeTab
import net.minecraft.world.item.Item
import net.minecraft.world.item.Items
import net.minecraft.world.item.crafting.Recipe
import net.minecraft.world.item.crafting.RecipeType
import net.minecraft.world.level.material.Fluid
import net.minecraft.world.level.storage.loot.predicates.LootItemConditionType
import net.minecraftforge.registries.DeferredRegister
import net.minecraftforge.registries.ForgeRegistries
import net.minecraftforge.registries.RegistryObject
import thedarkcolour.kotlinforforge.forge.MOD_BUS
import thedarkcolour.kotlinforforge.forge.registerObject
import kotlin.properties.ReadOnlyProperty
import kotlin.reflect.KFunction0

object Registration {

    val PONDER_HELPER = PonderRegistrationHelper(MOD_ID)

    private val TAB = CreativeModeTab.TAB_FOOD
    val Properties: Item.Properties
        get() = Item.Properties().tab(TAB)

    internal val ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, MOD_ID)
    internal val BLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS, MOD_ID)
    internal val TILES = DeferredRegister.create(ForgeRegistries.BLOCK_ENTITIES, MOD_ID)
    internal val EFFECTS = DeferredRegister.create(ForgeRegistries.MOB_EFFECTS, MOD_ID)
    internal val FLUIDS = DeferredRegister.create(ForgeRegistries.FLUIDS, MOD_ID)
    internal val RECIPES = DeferredRegister.create(Registry.RECIPE_TYPE_REGISTRY, MOD_ID)
    internal val RECIPE_SERIALIZERS = DeferredRegister.create(ForgeRegistries.RECIPE_SERIALIZERS, MOD_ID)
    private val LOOT_CONDITION_TYPES = DeferredRegister.create(Registry.LOOT_CONDITION_TYPE.key(), MOD_ID)

    val CONFIG_LOOT_CONDITION = LOOT_CONDITION_TYPES.register("config_option") { LootItemConditionType(ConfigLootCondition) }

    fun <T> modTag(name: String, registryKey: ResourceKey<Registry<T>>): TagKey<T> {
        return TagKey.create(registryKey, ResourceLocation(MOD_ID, name))
    }

    fun <T> forgeTag(type: String, value: String, registryKey: ResourceKey<Registry<T>>): TagKey<T> {
        return TagKey.create(registryKey, ResourceLocation("forge", "${type}s/$value"))
    }

    fun cropTag(value: String) = forgeTag("crop", value, Registry.ITEM_REGISTRY)
    fun fruitTag(value: String) = forgeTag("fruit", value, Registry.ITEM_REGISTRY)
    fun juiceTag(value: String) = forgeTag("juice", value, Registry.ITEM_REGISTRY)
    fun juiceFluidTag(value: String) = forgeTag("juice", value, Registry.FLUID_REGISTRY)

    fun register(vararg modules: ModModule) {
        modules.forEach { ModModule.register(it) }
        listOf(FLUIDS, ITEMS, BLOCKS, TILES, EFFECTS, RECIPES, RECIPE_SERIALIZERS, LOOT_CONDITION_TYPES).forEach {
            it.register(MOD_BUS)
        }
    }

    internal fun <T : Recipe<*>> createRecipeType(id: String): RecipeType<T> {
        return object : RecipeType<T> {
            override fun toString() = "${MOD_ID}:$id"
        }
    }

    val DRINKS
        get() = ITEMS.entries.mapNotNull { it.get() }.filterIsInstance<Drink>()

    fun createFluid(id: String, withBucket: Boolean = false): RegistryObject<Fluid> {
        lateinit var source: RegistryObject<Fluid>
        lateinit var flowing: RegistryObject<Fluid>

        var bucketGetter: KFunction0<BucketItem>? = null

        source = FLUIDS.register(id) { ModFluid(id, true, bucketGetter, flowing::get, source::get) }
        flowing = FLUIDS.register("${id}_flow") { ModFluid(id, false, bucketGetter, flowing::get, source::get) }

        //BLOCKS.registerObject(id) { FlowingFluidBlock(source::get, AbstractBlock.Properties.copy(Blocks.WATER)) }

        if (withBucket) {
            val bucket = ITEMS.register("${id}_bucket") {
                BucketItem({ source.get() },
                    Item.Properties().tab(CreativeModeTab.TAB_MISC).craftRemainder(Items.BUCKET).stacksTo(1))
            }
            bucketGetter = bucket::get
        }

        return source
    }

    internal fun <I : Drink> withFluid(
        id: String,
        itemId: String = id,
        itemSupplier: (() -> Fluid) -> I,
    ): ReadOnlyProperty<Any?, I> {
        val source = createFluid(id)
        return ITEMS.registerObject(itemId) {
            itemSupplier { source.get() }
        }
    }

}