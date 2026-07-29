package com.example.data.qubo

/**
 * Mulberry32 — a fast, deterministic 32-bit PRNG in Kotlin.
 * Same seed always produces same sequence.
 */
class DeterministicRNG(seed: Long) {
    private var state: Int = seed.toInt()

    fun next(): Double {
        state = state + 0x6D2B79F5.toInt()
        var t = (state xor (state ushr 15)) * (state or 1)
        t = t xor (t + ((t xor (t ushr 7)) * (t or 61)))
        val raw = (t xor (t ushr 14)).toLong() and 0xFFFFFFFFL
        return raw.toDouble() / 4294967296.0
    }

    fun nextInt(max: Int): Int {
        if (max <= 0) return 0
        return Math.floor(next() * max).toInt().coerceIn(0, max - 1)
    }
}
