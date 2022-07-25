package com.possible_triangle.bigsip.data.generation.recipes

import com.possible_triangle.bigsip.BigSip
import com.possible_triangle.bigsip.modules.ModModule
import com.simibubi.create.content.contraptions.processing.ProcessingRecipe
import com.simibubi.create.content.contraptions.processing.ProcessingRecipeBuilder
import com.simibubi.create.foundation.data.recipe.ProcessingRecipeGen
import com.simibubi.create.foundation.utility.recipe.IRecipeTypeInfo
import net.minecraft.advancements.critereon.InventoryChangeTrigger
import net.minecraft.data.DataGenerator
import net.minecraft.data.HashCache
import net.minecraft.data.recipes.FinishedRecipe
import net.minecraft.data.recipes.RecipeProvider
import net.minecraft.data.recipes.ShapedRecipeBuilder
import net.minecraft.data.recipes.ShapelessRecipeBuilder
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.crafting.Ingredient
import net.minecraft.world.level.ItemLike
import net.minecraftforge.common.crafting.PartialNBTIngredient
import java.util.function.Consumer

fun ingredient(stack: ItemStack): Ingredient {
    return if (stack.tag?.isEmpty == false) PartialNBTIngredient.of(stack.item, stack.orCreateTag)
    else Ingredient.of(stack)
}

class RecipeBuilder private constructor(generator: DataGenerator) : RecipeProvider(generator) {

    companion object {
        fun register(generator: DataGenerator) {
            val provider = RecipeBuilder(generator)
            ModModule.generateRecipes(provider)
            generator.addProvider(provider)
        }
    }

    private val vanillaRecipes = arrayListOf<Consumer<Consumer<FinishedRecipe>>>()
    private val processingProviders = hashMapOf<IRecipeTypeInfo, CustomProcessingGen>()
    private val thermalRecipes = ThermalRecipeProvider(generator).also(generator::addProvider)

    fun hasItem(item: ItemLike): InventoryChangeTrigger.TriggerInstance = has(item)

    fun shapeless(output: ItemLike, amount: Int = 1, builder: ShapelessRecipeBuilder.() -> Unit) {
        val recipe = ShapelessRecipeBuilder(output, amount).apply(builder)
        vanillaRecipes.add { recipe.save(it) }
    }

    fun shaped(output: ItemLike, amount: Int = 1, builder: ShapedRecipeBuilder.() -> Unit) {
        val recipe = ShapedRecipeBuilder(output, amount).apply(builder)
        vanillaRecipes.add { recipe.save(it) }
    }

    fun processing(
        type: IRecipeTypeInfo, name: String,
        builder: ProcessingRecipeBuilder<*>.() -> Unit,
    ) {
        val provider = processingProviders[type] ?: createProcessingProvider(type)
        provider.create(name, builder)
    }

    fun thermal(name: String, type: String, builder: ThermalRecipeBuilder.() -> Unit) {
        val recipe = ThermalRecipeBuilder(name, type).apply(builder)
        thermalRecipes.add(recipe::build)
    }

    private fun createProcessingProvider(type: IRecipeTypeInfo): CustomProcessingGen {
        return CustomProcessingGen(type).also {
            generator.addProvider(it)
            processingProviders[type] = it
        }
    }

    override fun buildCraftingRecipes(consumer: Consumer<FinishedRecipe>) {
        vanillaRecipes.forEach { it.accept(consumer) }
    }

    override fun run(cache: HashCache) {
        super.run(cache)
        thermalRecipes.run(cache)
    }

    private inner class CustomProcessingGen(private val type: IRecipeTypeInfo) :
        ProcessingRecipeGen(generator) {
        override fun getRecipeType() = type

        fun create(
            name: String,
            builder: ProcessingRecipeBuilder<*>.() -> Unit,
        ): GeneratedRecipe {
            return super.create<ProcessingRecipe<*>>(ResourceLocation(BigSip.MOD_ID, name)) {
                builder(it)
                it
            }
        }
    }
}