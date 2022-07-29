package com.possible_triangle.bigsip.modules

import com.possible_triangle.bigsip.compat.ModCompat
import com.possible_triangle.bigsip.data.generation.TagBuilder
import com.possible_triangle.bigsip.data.generation.recipes.RecipeBuilder
import net.minecraft.core.HolderSet
import net.minecraft.core.Registry
import net.minecraft.resources.ResourceKey
import net.minecraft.server.MinecraftServer
import net.minecraft.tags.TagKey
import net.minecraft.world.item.Item
import net.minecraft.world.item.ItemStack
import net.minecraft.world.level.ItemLike
import net.minecraft.world.level.material.Fluid
import net.minecraftforge.fml.ModList

interface ILoadingContext {
    fun <T> getTag(tag: TagKey<T>, registryKey: ResourceKey<Registry<T>>): HolderSet.Named<T>?
    fun isLoaded(mod: ModCompat.Mod) = isLoaded(mod.id)
    fun isLoaded(mod: String): Boolean
    fun <T> tagEmpty(tag: TagKey<T>, registryKey: ResourceKey<Registry<T>>): Boolean {
        val tag = getTag(tag, registryKey) ?: return true
        return tag.size() == 0
    }
}

class ServerLoadingContext(private val server: MinecraftServer) : ILoadingContext {
    override fun <T> getTag(tag: TagKey<T>, registryKey: ResourceKey<Registry<T>>): HolderSet.Named<T>? {
        val registry = server.registryAccess().registryOrThrow(registryKey)
        return registry.getTag(tag).orElse(null)
    }

    override fun isLoaded(mod: String): Boolean {
        return ModList.get().isLoaded(mod)
    }
}

interface IConditionBuilder {
    fun register(item: ItemLike, predicate: (ILoadingContext) -> Boolean)
    fun register(fluid: Fluid, predicate: (ILoadingContext) -> Boolean)
}

class FilteredList<T> {
    private val values = arrayListOf<Pair<T, (ILoadingContext) -> Boolean>>()
    fun add(value: T, predicate: (ILoadingContext) -> Boolean) = values.add(value to predicate)
    fun clear() = values.clear()
    fun getValues(context: ILoadingContext): List<T> {
        return values.filterNot { it.second(context) }.map { it.first }
    }
}

fun Item.withDamage(damage: Int): ItemStack {
    return ItemStack(this).apply {
        damageValue = damage
    }
}

interface ModModule {

    companion object {
        private val MODULES = arrayListOf<ModModule>()
        fun register(module: ModModule) = MODULES.add(module)

        private val HIDDEN_ITEMS = FilteredList<ItemLike>()
        fun hiddenItems(context: ILoadingContext) = HIDDEN_ITEMS.getValues(context)

        private val HIDDEN_FLUIDS = FilteredList<Fluid>()
        fun hiddenFluids(context: ILoadingContext) = HIDDEN_FLUIDS.getValues(context)

        fun addConditions() {
            HIDDEN_ITEMS.clear()
            HIDDEN_FLUIDS.clear()

            val builder = object : IConditionBuilder {
                override fun register(item: ItemLike, predicate: (ILoadingContext) -> Boolean) {
                    HIDDEN_ITEMS.add(item, predicate)
                }

                override fun register(fluid: Fluid, predicate: (ILoadingContext) -> Boolean) {
                    HIDDEN_FLUIDS.add(fluid, predicate)
                }
            }

            MODULES.forEach { it.addConditions(builder) }
        }

        fun forEach(consumer: (ModModule) -> Unit) {
            MODULES.forEach(consumer)
        }

    }

    fun addConditions(builder: IConditionBuilder) {}

    fun generateRecipes(builder: RecipeBuilder) {}

    fun generateTags(builder: TagBuilder) {}

    fun registerPonders() {}

    fun registerCTM() {}

}