/*
 * Copyright (C) 2002-2024 Sebastiano Vigna
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package it.unimi.dsi.fastutil;

/** Common code for all hash-based classes. */

public class HashCommon {

	protected HashCommon() {}

	/** 2<sup>32</sup> &middot; &phi;, &phi; = (&#x221A;5 &minus; 1)/2. */
	private static final int INT_PHI = 0x9E3779B9;
	/** The reciprocal of {@link #INT_PHI} modulo 2<sup>32</sup>. */
	private static final int INV_INT_PHI = 0x144cbc89;
	/** 2<sup>64</sup> &middot; &phi;, &phi; = (&#x221A;5 &minus; 1)/2. */
	private static final long LONG_PHI = 0x9E3779B97F4A7C15L;
	/** The reciprocal of {@link #LONG_PHI} modulo 2<sup>64</sup>. */
	private static final long INV_LONG_PHI = 0xf1de83e19937733dL;

	/** Avalanches the bits of an integer by applying the finalisation step of MurmurHash3.
	 *
	 * <p>This method implements the finalisation step of Austin Appleby's <a href="http://code.google.com/p/smhasher/">MurmurHash3</a>.
	 * Its purpose is to avalanche the bits of the argument to within 0.25% bias.
	 *
	 * @param x an integer.
	 * @return a hash value with good avalanching properties.
	 */
	public static int murmurHash3(int x) {
		x ^= x >>> 16;
		x *= 0x85ebca6b;
		x ^= x >>> 13;
		x *= 0xc2b2ae35;
		x ^= x >>> 16;
		return x;
	}


	/** Avalanches the bits of a long integer by applying the finalisation step of MurmurHash3.
	 *
	 * <p>This method implements the finalisation step of Austin Appleby's <a href="http://code.google.com/p/smhasher/">MurmurHash3</a>.
	 * Its purpose is to avalanche the bits of the argument to within 0.25% bias.
	 *
	 * @param x a long integer.
	 * @return a hash value with good avalanching properties.
	 */
	public static long murmurHash3(long x) {
		x ^= x >>> 33;
		x *= 0xff51afd7ed558ccdL;
		x ^= x >>> 33;
		x *= 0xc4ceb9fe1a85ec53L;
		x ^= x >>> 33;
		return x;
	}

	/** Quickly mixes the bits of an integer.
	 *
	 * <p>This method mixes the bits of the argument by multiplying by the golden ratio and
	 * xorshifting the result. It is borrowed from <a href="https://github.com/leventov/Koloboke">Koloboke</a>, and
	 * it has slightly worse behaviour than {@link #murmurHash3(int)} (in open-addressing hash tables the average number of probes
	 * is slightly larger), but it's much faster.
	 *
	 * @param x an integer.
	 * @return a hash value obtained by mixing the bits of {@code x}.
	 * @see #invMix(int)
	 */
	public static int mix(final int x) {
		final int h = x * INT_PHI;
		return h ^ (h >>> 16);
	}

	/** The inverse of {@link #mix(int)}. This method is mainly useful to create unit tests.
	 *
	 * @param x an integer.
	 * @return a value that passed through {@link #mix(int)} would give {@code x}.
	 */
	public static int invMix(final int x) {
		return (x ^ x >>> 16) * INV_INT_PHI;
	}

	/** Quickly mixes the bits of a long integer.
	 *
	 * <p>This method mixes the bits of the argument by multiplying by the golden ratio and
	 * xorshifting twice the result. It is borrowed from <a href="https://github.com/leventov/Koloboke">Koloboke</a>, and
	 * it has slightly worse behaviour than {@link #murmurHash3(long)} (in open-addressing hash tables the average number of probes
	 * is slightly larger), but it's much faster.
	 *
	 * @param x a long integer.
	 * @return a hash value obtained by mixing the bits of {@code x}.
	 */
	public static long mix(final long x) {
		long h = x * LONG_PHI;
		h ^= h >>> 32;
		return h ^ (h >>> 16);
	}

	/** The inverse of {@link #mix(long)}. This method is mainly useful to create unit tests.
	 *
	 * @param x a long integer.
	 * @return a value that passed through {@link #mix(long)} would give {@code x}.
	 */
	public static long invMix(long x) {
		x ^= x >>> 32;
		x ^= x >>> 16;
		return (x ^ x >>> 32) * INV_LONG_PHI;
	}


	/**
	 * Returns {@link Float#floatToRawIntBits(float)}.
	 *
	 * <p>
	 * Note that {@link Float#hashCode()} returns {@link Float#floatToIntBits(float)}.
	 *
	 * @param f a float.
	 * @return {@link Float#floatToRawIntBits(float)}.
	 */
	public static int float2int(final float f) {
		return Float.floatToRawIntBits(f);
	}

	/**
	 * Returns the upper and lower halves of {@link Double#doubleToRawLongBits(double)} xor'd together.
	 *
	 * <p>
	 * Note that {@link Double#hashCode()} would do the same with
	 * {@link Double#doubleToLongBits(double)}.
	 *
	 * @param d a double.
	 * @return the upper and lower halves of {@link Double#doubleToRawLongBits(double)} xor'd together.
	 */
	public static int double2int(final double d) {
		final long l = Double.doubleToRawLongBits(d);
		return (int)(l ^ (l >>> 32));
	}

	/** Returns the hash code that would be returned by {@link Long#hashCode()}.
	 *
	 * @param l a long.
	 * @return the same code as {@link Long#hashCode() new Long(f).hashCode()}.
	 */
	public static int long2int(final long l) {
		return (int)(l ^ (l >>> 32));
	}

	/** Returns the least power of two greater than or equal to the specified value.
	 *
	 * <p>Note that this function will return 1 when the argument is 0.
	 *
	 * @param x an integer smaller than or equal to 2<sup>30</sup>.
	 * @return the least power of two greater than or equal to the specified value.
	 */
	public static int nextPowerOfTwo(final int x) {
		return 1 << (32 - Integer.numberOfLeadingZeros(x - 1));
	}

	/** Returns the least power of two greater than or equal to the specified value.
	 *
	 * <p>Note that this function will return 1 when the argument is 0.
	 *
	 * @param x a long integer smaller than or equal to 2<sup>62</sup>.
	 * @return the least power of two greater than or equal to the specified value.
	 */
	public static long nextPowerOfTwo(final long x) {
		return 1L << (64 - Long.numberOfLeadingZeros(x - 1));
	}


	/** Returns the maximum number of entries that can be filled before rehashing.
	 *
	 * @param n the size of the backing array.
	 * @param f the load factor.
	 * @return the maximum number of entries before rehashing.
	 */
	public static int maxFill(final int n, final float f) {
		/* We must guarantee that there is always at least
		 * one free entry (even with pathological load factors).
		 *
		 * The cast to double is essential to avoid a precision
		 * loss due to a cast to float before the call to Math.ceil.
		 */
		return Math.min((int)Math.ceil(n * (double)f), n - 1);
	}

	/** Returns the maximum number of entries that can be filled before rehashing.
	 *
	 * @param n the size of the backing array.
	 * @param f the load factor.
	 * @return the maximum number of entries before rehashing.
	 */
	public static long maxFill(final long n, final float f) {
		/* We must guarantee that there is always at least
		 * one free entry (even with pathological load factors).
		 *
		 * The cast to double is essential to avoid a precision
		 * loss due to a cast to float before the call to Math.ceil.
		 */
		return Math.min((long)Math.ceil(n * (double)f), n - 1);
	}

	/** Returns the least power of two smaller than or equal to 2<sup>30</sup> and larger than or equal to {@code Math.ceil(expected / f)}.
	 *
	 * @param expected the expected number of elements in a hash table.
	 * @param f the load factor.
	 * @return the minimum possible size for a backing array.
	 * @throws IllegalArgumentException if the necessary size is larger than 2<sup>30</sup>.
	 */
	public static int arraySize(final int expected, final float f) {
		/*
		 * The cast to double is essential to avoid a precision
		 * loss due to a cast to float before the call to Math.ceil.
		 */
		final long s = Math.max(2, nextPowerOfTwo((long)Math.ceil(expected / (double)f)));
		if (s > (1 << 30)) throw new IllegalArgumentException("Too large (" + expected + " expected elements with load factor " + f + ")");
		return (int)s;
	}

	/** Returns the least power of two larger than or equal to {@code Math.ceil(expected / f)}.
	 *
	 * @param expected the expected number of elements in a hash table.
	 * @param f the load factor.
	 * @return the minimum possible size for a backing big array.
	 */
	public static long bigArraySize(final long expected, final float f) {
		/*
		 * The cast to double is essential to avoid a precision
		 * loss due to a cast to float before the call to Math.ceil.
		 */
		return nextPowerOfTwo((long)Math.ceil(expected / (double)f));
	}
}
