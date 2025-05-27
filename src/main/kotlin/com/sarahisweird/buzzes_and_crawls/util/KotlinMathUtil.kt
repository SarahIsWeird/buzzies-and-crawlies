package com.sarahisweird.buzzes_and_crawls.util

import net.minecraft.util.math.BlockPos
import net.minecraft.util.math.Vec2f
import net.minecraft.util.math.Vec3d
import net.minecraft.util.math.Vec3i

operator fun Vec3i.plus(other: Vec3i): Vec3i {
    return this.add(other)
}

operator fun Vec3i.minus(other: Vec3i): Vec3i {
    return this.subtract(other)
}

operator fun Vec3i.times(scale: Int): Vec3i {
    return this.multiply(scale)
}

operator fun Vec3d.plus(other: Vec3d): Vec3d {
    return this.add(other)
}

operator fun Vec3d.minus(other: Vec3d): Vec3d {
    return this.subtract(other)
}

operator fun Vec3d.times(scale: Double): Vec3d {
    return this.multiply(scale)
}

operator fun Vec2f.plus(other: Vec2f): Vec2f {
    return this.add(other)
}

operator fun Vec2f.minus(other: Vec2f): Vec2f {
    return this.add(other.negate())
}

operator fun Vec2f.times(value: Float): Vec2f {
    return this.multiply(value)
}

operator fun BlockPos.plus(vector: Vec3i): BlockPos {
    return this.add(vector)
}

operator fun BlockPos.minus(vector: Vec3i): BlockPos {
    return this.subtract(vector)
}
