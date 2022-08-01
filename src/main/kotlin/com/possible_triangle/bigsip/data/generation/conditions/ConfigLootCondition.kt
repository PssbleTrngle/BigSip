package com.possible_triangle.bigsip.data.generation.conditions

import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonObject
import com.google.gson.JsonSerializationContext
import com.possible_triangle.bigsip.Registration
import com.possible_triangle.bigsip.config.Configs
import net.minecraft.world.level.storage.loot.LootContext
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition
import net.minecraftforge.common.ForgeConfigSpec.BooleanValue
import net.minecraft.world.level.storage.loot.Serializer as LootSerializer

class ConfigLootCondition private constructor(private val key: String) : LootItemCondition {

    override fun test(t: LootContext?): Boolean {
        val value: BooleanValue = Configs.SERVER_SPEC.values.get(key)
        return value.get()
    }

    override fun getType() = Registration.CONFIG_LOOT_CONDITION.get()

    companion object : LootSerializer<ConfigLootCondition> {

        fun forOption(key: BooleanValue): LootItemCondition.Builder {
            return LootItemCondition.Builder { ConfigLootCondition(key.path.joinToString(".")) }
        }

        override fun serialize(
            json: JsonObject,
            value: ConfigLootCondition,
            context: JsonSerializationContext,
        ) {
            json.addProperty("key", value.key)
        }

        override fun deserialize(json: JsonObject, context: JsonDeserializationContext): ConfigLootCondition {
            val key = json.get("key").asString
            return ConfigLootCondition(key)
        }
    }

}