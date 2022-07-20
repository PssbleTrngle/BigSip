package com.possible_triangle.bigsip.block.tile

 fun interface IProgress {

    fun get(): Int

    fun set(value: Int) {}

    fun tick() {}

}

class ControllerProgress : IProgress {

    private var value = 0

    override fun tick() {
        value += 10;
    }

    override fun set(value: Int) {
        this.value = value
    }

    override fun get(): Int = value

}