package com.sarahisweird.buzzes_and_crawls.util

import net.minecraft.entity.Entity
import net.minecraft.util.math.Box
import net.minecraft.world.World
import java.util.function.Predicate

inline fun <reified T : Entity> World.getEntitiesByClass(box: Box, predicate: Predicate<T>): List<T> =
    getEntitiesByClass(T::class.java, box, predicate)
