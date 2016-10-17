/*
* Copyright 2014 Mikhail Vorontsov
*
* Licensed under the Apache License, Version 2.0 (the "License");
* you may not use this file except in compliance with the License.
* You may obtain a copy of the License at
*
* http://www.apache.org/licenses/LICENSE-2.0
*
* Unless required by applicable law or agreed to in writing, software
* distributed under the License is distributed on an "AS IS" BASIS,
* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
* See the License for the specific language governing permissions and
* limitations under the License.
*/

package com.money;

import java.io.Serializable;
import java.math.BigDecimal;
import java.math.MathContext;

/**
 * Safe but slow Money implementation. Uses BigDecimal as a storage.
 */
class MoneyBigDecimal extends AbstractMoney implements Serializable {
	private static final long serialVersionUID = 5702082884787248398L;

	private final BigDecimal mValue;

	public MoneyBigDecimal(final BigDecimal value) {
		mValue = value;
	}

	public MoneyBigDecimal(final double value) {
		mValue = new BigDecimal(value, MathContext.DECIMAL64).stripTrailingZeros(); // decimal64
																					// to
																					// match
																					// double
	}

	public MoneyBigDecimal(final String value) {
		// important - do not use DECIMAL64 context here - you will lose
		// precision for huge values.
		// at the same time using it is required for BigDecimal(double)
		// constructor - it matches "double" range.
		mValue = new BigDecimal(value);
	}

	@Override
	public double toDouble() {
		return mValue.doubleValue();
	}

	/**
	 * Convert this value into a BigDecimal. This method is also used for
	 * arithmetic calculations when necessary.
	 *
	 * @return This object as BigDecimal
	 */
	@Override
	public BigDecimal toBigDecimal() {
		return mValue;
	}

	/**
	 * Return this value with an opposite sign.
	 *
	 * @return A new object with the same value with a different sign
	 */
	@Override
	public Money negate() {
		return new MoneyBigDecimal(mValue.negate());
	}

	/**
	 * Convert into a String in a plain notation with a decimal dot.
	 * 
	 * @return a String in a plain notation with a decimal dot.
	 */
	@Override
	public String toString() {
		return mValue.toPlainString();
	}

	@Override
	public boolean equals(Object o) {
		if (this == o)
			return true;
		if (o == null || getClass() != o.getClass())
			return false;

		MoneyBigDecimal that = (MoneyBigDecimal) o;

		return mValue.equals(that.mValue);
	}

	@Override
	public int hashCode() {
		return mValue.hashCode();
	}

	@Override
	protected Money add(final MoneyLong other) {
		return other.add(this); // implemented in MoneyLong
	}

	@Override
	protected int compareTo(MoneyLong other) {
		return -(other.compareTo(this)); // flips the response with unary
	}

	/**
	 * Multiply the current object by the <code>long</code> value.
	 *
	 * @param multiplier
	 *            Multiplier
	 * @return A new Money object normalized to the efficient representation if
	 *         possible
	 */
	@Override
	public Money multiply(final long multiplier) {
		final BigDecimal res = mValue.multiply(BigDecimal.valueOf(multiplier));
		return MoneyFactory.fromBigDecimal(res);
	}

	/**
	 * Multiply the current object by the <code>double</code> value.
	 *
	 * @param multiplier
	 *            Multiplier
	 * @return A new Money object normalized to the efficient representation if
	 *         possible
	 */
	@Override
	public Money multiply(double multiplier) {
		return MoneyFactory.fromBigDecimal(
				mValue.multiply(new BigDecimal(multiplier, MathContext.DECIMAL64), MathContext.DECIMAL64));
	}

	/**
	 * Divide the current object by the given <code>long</code> divider.
	 *
	 * @param divider
	 *            Divider
	 * @param precision
	 *            Maximal precision to keep. We will round the next digit.
	 * @return A new Money object normalized to the efficient representation if
	 *         possible
	 */
	@Override
	public Money divide(final long divider, final int precision) {
		final BigDecimal res = mValue.divide(BigDecimal.valueOf(divider), MathContext.DECIMAL64).stripTrailingZeros();
		return truncate(res, precision);
	}

	/**
	 * Divide the current object by the given <code>long</code> divider.
	 *
	 * @param divider
	 *            Divider
	 * @param precision
	 *            Maximal precision to keep. We will round the next digit.
	 * @return A new Money object normalized to the efficient representation if
	 *         possible
	 */
	@Override
	public Money divide(final double divider, final int precision) {
		final BigDecimal res = mValue.divide(BigDecimal.valueOf(divider), MathContext.DECIMAL64).stripTrailingZeros();
		return truncate(res, precision);
	}

	/**
	 * Truncate the current value leaving no more than {@code maximalPrecision}
	 * signs after decimal point. The number will be rounded towards closest
	 * digit (0-4 -> 0; 5-9 -> 1)
	 *
	 * @param maximalPrecision
	 *            Required precision
	 * @return A new Money object normalized to the efficient representation if
	 *         possible
	 */
	private static Money truncate(final BigDecimal val, final int maximalPrecision) {
		MoneyFactory.checkPrecision(maximalPrecision);

		final BigDecimal res = val.setScale(maximalPrecision, BigDecimal.ROUND_HALF_UP);
		return MoneyFactory.fromBigDecimal(res);
	}

	/**
	 * Truncate the current value leaving no more than {@code maximalPrecision}
	 * signs after decimal point. The number will be rounded towards closest
	 * digit (0-4 -> 0; 5-9 -> 1)
	 *
	 * @param maximalPrecision
	 *            Required precision
	 * @return A new Money object normalized to the efficient representation if
	 *         possible
	 */
	@Override
	public Money truncate(final int maximalPrecision) {
		if (mValue.scale() <= maximalPrecision)
			return this;
		return truncate(mValue, maximalPrecision);
	}
}
