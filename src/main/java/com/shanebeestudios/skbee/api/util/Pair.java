package com.shanebeestudios.skbee.api.util;

/**
 * A simple pair class for holding two values
 *
 * <p>Not to be confused with a pear.
 *
 * @param first  First value
 * @param second Second value
 * @param <A>    Type of first value
 * @param <B>    Type of second value
 */
public record Pair<A, B>(A first, B second) {
}
