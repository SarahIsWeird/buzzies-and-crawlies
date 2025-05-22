package com.sarahisweird.buzzes_and_crawls

import net.fabricmc.api.ModInitializer
import org.slf4j.Logger
import org.slf4j.LoggerFactory

object BuzzesAndCrawls : ModInitializer {
	const val MOD_ID = "buzzes_and_crawls"
    val LOGGER: Logger = LoggerFactory.getLogger(BuzzesAndCrawls::class.java)

	override fun onInitialize() {
		LOGGER.info("*bzz bzz*")
	}
}
