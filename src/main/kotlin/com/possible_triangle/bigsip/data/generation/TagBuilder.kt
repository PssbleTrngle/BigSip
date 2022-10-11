package com.possible_triangle.bigsip.data.generation

import com.possible_triangle.bigsip.BigSip
import com.possible_triangle.bigsip.modules.ModModule
import net.minecraft.core.Registry
import net.minecraft.data.DataGenerator
import net.minecraft.data.tags.TagsProvider
import net.minecraft.tags.TagKey
import net.minecraftforge.common.data.ExistingFileHelper

fun <T> TagsProvider.TagAppender<T>.addOptional(value: T) {
    addOptional(registry.getKey(value) ?: throw NullPointerException())
}

class TagBuilder private constructor(private val generator: DataGenerator, private val fileHelper: ExistingFileHelper) {

    companion object {
        fun register(generator: DataGenerator, fileHelper: ExistingFileHelper) {
            TagBuilder(generator, fileHelper)
        }
    }

    val blocks = ModTagsProvider(Registry.BLOCK)
    val items = ModTagsProvider(Registry.ITEM)
    val fluids = ModTagsProvider(Registry.FLUID)

    inner class ModTagsProvider<T>(registry: Registry<T>) :
        TagsProvider<T>(generator, registry, BigSip.MOD_ID, fileHelper) {

        init {
            generator.addProvider(this)
        }

        fun create(key: TagKey<T>, builder: TagAppender<T> .() -> Unit) {
            this.tag(key).apply(builder)
        }

        override fun getName() = "BigSip Tags (${registry.key().location().path})"

        override fun addTags() = ModModule.forEach {
            it.generateTags(this@TagBuilder)
        }
    }

}