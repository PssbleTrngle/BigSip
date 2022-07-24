package com.possible_triangle.bigsip.data.generation.recipes

import com.google.gson.GsonBuilder
import com.google.gson.JsonElement
import com.google.gson.JsonObject
import com.mojang.logging.LogUtils
import com.possible_triangle.bigsip.BigSip
import net.minecraft.data.DataGenerator
import net.minecraft.data.HashCache
import net.minecraft.data.recipes.FinishedRecipe
import net.minecraft.data.recipes.RecipeProvider
import java.io.IOException
import java.nio.file.Path
import java.util.function.Consumer
import java.util.function.Supplier
import kotlin.io.path.Path
import kotlin.io.path.createDirectories
import kotlin.io.path.exists
import kotlin.io.path.writeText

class ThermalRecipeProvider(generator: DataGenerator) : RecipeProvider(generator) {

    companion object {
        private val LOGGER = LogUtils.getLogger()
        private val GSON = GsonBuilder().setPrettyPrinting().create()

    }

    private val recipes = arrayListOf<Consumer<(String, JsonElement) -> Unit>>()

    override fun buildCraftingRecipes(consumer: Consumer<FinishedRecipe>) {}

    fun add(recipe: Consumer<(String, JsonElement) -> Unit>) {
        recipes.add(recipe)
    }

    override fun run(cache: HashCache) {
        recipes.forEach {
            it.accept { name, json ->
                val path = generator.outputFolder.resolve("data/${BigSip.MOD_ID}/recipes/thermal/bottler/${name}.json")
                saveRecipe(cache, json, path)
            }
        }
    }

    private fun saveRecipe(cache: HashCache, json: JsonElement, file: Path) {
        try {
            val encoded = GSON.toJson(json)
            val hash = SHA1.hashUnencodedChars(encoded).toString()

            if (cache.getHash(file) != hash || !file.exists()) {
                file.parent.createDirectories()
                file.writeText(encoded)
            }
            cache.putNew(file, hash)
        } catch (ex: IOException) {
            LOGGER.error("Couldn't save recipe {}", file, ex)
        }
    }

}