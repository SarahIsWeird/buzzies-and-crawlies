package com.sarahisweird.buzzes_and_crawls

import net.fabricmc.api.ModInitializer
import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroup
import net.fabricmc.loader.api.FabricLoader
import net.minecraft.item.ItemGroup
import net.minecraft.item.ItemStack
import net.minecraft.registry.Registries
import net.minecraft.registry.Registry
import net.minecraft.registry.RegistryKey
import net.minecraft.registry.RegistryKeys
import net.minecraft.text.Text
import net.minecraft.util.Identifier
import org.apache.logging.log4j.kotlin.KotlinLogger
import org.apache.logging.log4j.kotlin.logger
import org.spongepowered.asm.mixin.MixinEnvironment

object BuzzesAndCrawls : ModInitializer {
	const val MOD_ID = "buzzes_and_crawls"
    val LOGGER: KotlinLogger = logger()

	val ITEM_GROUP_KEY: RegistryKey<ItemGroup> = RegistryKey.of(RegistryKeys.ITEM_GROUP, Identifier.of(MOD_ID, "item_group"))
	val ITEM_GROUP: ItemGroup = FabricItemGroup.builder()
		.icon { ItemStack(BaCItems.BEAKER_MAGNIFIER) }
		.displayName(Text.translatable("itemGroup.buzzes_and_crawls"))
		.build()

	override fun onInitialize() {
		if (FabricLoader.getInstance().isDevelopmentEnvironment) {
			MixinEnvironment.getCurrentEnvironment().audit()
		}

		LOGGER.info("*bzz bzz*")

		Registry.register(Registries.ITEM_GROUP, ITEM_GROUP_KEY, ITEM_GROUP)

		BaCComponents.init()
		BaCItems.init()
		BaCBlocks.init()
		BaCEntities.init()
	}
}
