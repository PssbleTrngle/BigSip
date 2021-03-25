package com.possible_triangle.bigsip.data.generation

import com.possible_triangle.bigsip.BigSip
import com.possible_triangle.bigsip.Content
import net.minecraft.data.DataGenerator
import net.minecraft.util.ResourceLocation
import net.minecraftforge.client.model.generators.ItemModelProvider
import net.minecraftforge.common.data.ExistingFileHelper

class ItemModels(generator: DataGenerator, fileHelper: ExistingFileHelper) :
    ItemModelProvider(generator, BigSip.MOD_ID, fileHelper) {

    fun itemLoc(name: ResourceLocation): ResourceLocation {
        return ResourceLocation(name.namespace, "item/" + name.path)
    }

    override fun registerModels() {
        listOf(
            Content.WINE_BOTTLE_FULL, Content.WINE_BOTTLE_STARTED, Content.WINE_BOTTLE_LOW,
            Content.APPLE_JUICE, Content.CARROT_JUICE, Content.TOMATO_JUICE,
            Content.BEER, Content.DARK_BEER,
            Content.GRAPES,
        ).mapNotNull { it.registryName }.forEach {
            singleTexture(it.path, mcLoc("item/generated"), itemLoc(it))
        }
    }

}