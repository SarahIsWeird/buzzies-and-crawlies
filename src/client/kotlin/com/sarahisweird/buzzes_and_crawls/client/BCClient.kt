package com.sarahisweird.buzzes_and_crawls.client

import com.sarahisweird.buzzes_and_crawls.BaCEntities
import com.sarahisweird.buzzes_and_crawls.BuzzesAndCrawls.MOD_ID
import com.sarahisweird.buzzes_and_crawls.client.render.OBMTintSource
import net.fabricmc.api.ClientModInitializer
import net.minecraft.client.render.item.tint.TintSourceTypes
import net.minecraft.util.Identifier

object BCClient : ClientModInitializer {
	val obmTints = mutableMapOf(
		BaCEntities.ANT to 2697251
	)

	override fun onInitializeClient() {
		BaCClientJava.registerEntityRenderers()

		TintSourceTypes.ID_MAPPER.put(
			Identifier.of(MOD_ID, "beaker_magnifier_tint"),
			OBMTintSource.CODEC,
		)
    }
}
