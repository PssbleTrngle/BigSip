package com.possible_triangle.bigsip.recipe

import com.google.gson.JsonObject
import com.possible_triangle.bigsip.config.Configs
import net.minecraft.resources.ResourceLocation
import net.minecraftforge.common.ForgeConfigSpec.BooleanValue
import net.minecraftforge.common.crafting.conditions.ICondition
import net.minecraftforge.common.crafting.conditions.IConditionSerializer

class ConfigCondition private constructor(private val key: String) : ICondition {

    constructor(key: BooleanValue) : this(key.path.joinToString("."))

    companion object {
        val ID = ResourceLocation("bigsip", "config_option")
    }

    override fun getID(): ResourceLocation = ID

    override fun test(): Boolean {
        val value: BooleanValue = Configs.SERVER_SPEC.values.get(key)
        return value.get()
    }

    object Serializer : IConditionSerializer<ConfigCondition> {
        override fun getID(): ResourceLocation = ID

        override fun write(json: JsonObject, value: ConfigCondition) {
            json.addProperty("key", value.key)
        }

        override fun read(json: JsonObject): ConfigCondition {
            val key = json.get("key").asString
            return ConfigCondition(key)
        }
    }

}