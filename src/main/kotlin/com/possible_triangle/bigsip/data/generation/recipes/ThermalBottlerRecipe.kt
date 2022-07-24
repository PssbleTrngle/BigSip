package com.possible_triangle.bigsip.data.generation.recipes

import cofh.lib.fluid.FluidIngredient
import com.google.gson.JsonArray
import com.google.gson.JsonElement
import com.google.gson.JsonObject
import com.mojang.serialization.JsonOps
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.crafting.Ingredient
import net.minecraft.world.level.ItemLike
import net.minecraft.world.level.material.Fluid
import net.minecraftforge.fluids.FluidStack

class ThermalBottlerRecipeBuilder(private val name: String) {

    private val ingredients = arrayListOf<Ingredient>()
    private val fluidIngredients = arrayListOf<FluidIngredient>()
    private val outputs = arrayListOf<ItemStack>()

    fun require(ingredient: Ingredient) {
        ingredients.add(ingredient)
    }

    fun require(item: ItemLike) = require(Ingredient.of(item))
    fun require(stack: ItemStack) = require(ingredient(stack))

    fun require(ingredient: FluidIngredient) {
        fluidIngredients.add(ingredient)
    }

    fun require(stack: FluidStack) = require(FluidIngredient.of(stack))
    fun require(fluid: Fluid, amount: Int) = require(FluidStack(fluid, amount))

    fun output(item: ItemLike, amount: Int = 1) {
        outputs.add(ItemStack(item, amount))
    }

    fun build(consumer: (String, JsonElement) -> Unit) {
        consumer(name, JsonObject().apply {
            addProperty("type", "thermal:bottler")
            add("ingredients", JsonArray().apply {
                ingredients.forEach { add(it.toJson()) }
                fluidIngredients.forEach { add(it.toJson()) }
            })
            add("result", JsonArray().apply {
                outputs.forEach {
                    val encoded = ItemStack.CODEC.encodeStart(JsonOps.INSTANCE, it)
                    encoded.result().ifPresent(::add)
                }
            })
        })
    }

}