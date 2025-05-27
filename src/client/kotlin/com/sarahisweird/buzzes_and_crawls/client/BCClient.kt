package com.sarahisweird.buzzes_and_crawls.client

import net.fabricmc.api.ClientModInitializer

object BCClient : ClientModInitializer {
	override fun onInitializeClient() {
		BaCClientJava.registerEntityRenderers()
    }
}
