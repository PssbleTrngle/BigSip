package com.possible_triangle.bigsip.data.generation.recipes

import com.google.gson.JsonArray
import com.google.gson.JsonElement
import com.google.gson.JsonObject
import com.simibubi.create.foundation.fluid.FluidIngredient
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.crafting.Ingredient
import net.minecraft.world.level.ItemLike
import net.minecraft.world.level.material.Fluid
import net.minecraftforge.fluids.FluidStack

private data class ItemStackWithChance(val stack: ItemStack, val chance: Float?)

class ThermalRecipeBuilder(private val name: String, private val type: String) {

    private val ingredients = arrayListOf<Ingredient>()
    private val fluidIngredients = arrayListOf<FluidIngredient>()
    private val outputs = arrayListOf<ItemStackWithChance>()

    fun require(ingredient: Ingredient) {
        ingredients.add(ingredient)
    }

    fun require(item: ItemLike) = require(Ingredient.of(item))
    fun require(stack: ItemStack) = require(ingredient(stack))

    fun require(ingredient: FluidIngredient) {
        fluidIngredients.add(ingredient)
    }

    fun require(stack: FluidStack) = require(FluidIngredient.fromFluidStack(stack))
    fun require(fluid: Fluid, amount: Int) = require(FluidStack(fluid, amount))

    fun output(item: ItemLike, amount: Int = 1, chance: Float? = null) {
        outputs.add(ItemStackWithChance(ItemStack(item, amount), chance))
    }

    fun build(consumer: (String, JsonElement) -> Unit) {
        consumer("$type/$name", JsonObject().apply {
            addProperty("type", "thermal:$type")
            add("ingredients", JsonArray().apply {
                ingredients.forEach { add(it.toJson()) }
                fluidIngredients.forEach { add(it.serialize()) }
            })
            add("result", JsonArray().apply {
                outputs.forEach { (stack, chance) ->
                    add(JsonObject().apply {
                        addProperty("item", stack.item.registryName.toString())
                        if (stack.count > 0) addProperty("count", stack.count)
                        if (chance != null) addProperty("chance", chance)
                    })
                }
            })
        })
    }

}