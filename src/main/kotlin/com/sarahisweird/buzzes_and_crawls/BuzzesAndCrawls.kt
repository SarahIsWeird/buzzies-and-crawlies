package com.sarahisweird.buzzes_and_crawls

import net.fabricmc.api.ModInitializer
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.spongepowered.asm.mixin.MixinEnvironment

object BuzzesAndCrawls : ModInitializer {
	const val MOD_ID = "buzzes_and_crawls"
    val LOGGER: Logger = LoggerFactory.getLogger(BuzzesAndCrawls::class.java)

	override fun onInitialize() {
		MixinEnvironment.getCurrentEnvironment().audit()

		LOGGER.info("*bzz bzz*")
		BaCBlocks.init()
		BaCEntities.init()
	}
}
