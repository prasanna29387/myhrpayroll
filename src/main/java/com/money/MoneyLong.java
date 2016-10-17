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
import java.math.BigInteger;
import java.math.MathContext;

/**
 * The most efficient implementation storing the number of currency units in
 * <code>long</code> field.
 */
class MoneyLong extends AbstractMoney implements Serializable {
	private static final long serialVersionUID = 8831728626297590627L;
	private static final long MASK32 = 0xFFFFFFFF00000000L;
	/** Number of currency units in your precision */
	private final long mUnits;
	/**
	 * Precision of your value. You should divide <code>m_units</code> by
	 * <code>10^m_precision</code> to get an actual value
	 */
	private final int mPrecision;

	public MoneyLong(long units, int precision) {
		mUnits = units;
		mPrecision = precision;
	}

	/**
	 * Convert to the original currency - divide <code>units</code> by
	 * <code>10^precision</code>.
	 * 
	 * @return <code>units / (10^precision)</code>
	 */
	@Override
	public double toDouble() {
		// we can not replace division here with multiplication by
		// MULTIPLIERS_NEG - it will sacrifice the exact result guarantee.
		return ((double) mUnits) / MoneyFactory.MULTIPLIERS[mPrecision];
	}

	/**
	 * Convert into a String in a plain notation with a decimal dot.
	 * 
	 * @return a String in a plain notation with a decimal dot.
	 */
	@Override
	public String toString() {
		if (mPrecision == 0)
			return Long.toString(mUnits);

		final char[] buf = new char[MoneyFactory.MAX_LONG_LENGTH + 3]; // do not
																		// replace
																		// with
																		// ThreadLocal
																		// - it
																		// is
																		// slower
		int p = buf.length;
		int remainingPrecision = mPrecision;
		long units = Math.abs(mUnits);
		long q;
		int rem;
		while (remainingPrecision > 0 && units > 0) {
			q = units / 10;
			rem = (int) (units - q * 10); // avoiding direct % call
			buf[--p] = (char) ('0' + rem);
			units = q;
			--remainingPrecision;
		}
		if (units == 0 && remainingPrecision == 0) // just add "0."
		{
			buf[--p] = '.';
			buf[--p] = '0';
		} else if (units == 0) // some precision left
		{
			while (remainingPrecision > 0) {
				buf[--p] = '0';
				--remainingPrecision;
			}
			buf[--p] = '.';
			buf[--p] = '0';
		} else if (remainingPrecision == 0) // some value left
		{
			buf[--p] = '.';
			while (units > 0) {
				q = units / 10;
				rem = (int) (units - q * 10);
				buf[--p] = (char) ('0' + rem);
				units = q;
			}
		}
		if (mUnits < 0)
			buf[--p] = '-';
		return new String(buf, p, buf.length - p);
	}

	/**
	 * Convert this value into a BigDecimal. This method is also used for
	 * arithmetic calculations when necessary.
	 *
	 * @return This object as BigDecimal
	 */
	@Override
	public BigDecimal toBigDecimal() {
		return BigDecimal.valueOf(mUnits, mPrecision);
	}

	/**
	 * Return this value with an opposite sign.
	 *
	 * @return A new object with the same value with a different sign
	 */
	@Override
	public Money negate() {
		return new MoneyLong(-mUnits, mPrecision);
	}

	@Override
	public boolean equals(Object o) {
		if (this == o)
			return true;
		if (o == null || getClass() != o.getClass())
			return false;

		MoneyLong moneyLong = (MoneyLong) o;
		return mPrecision == moneyLong.mPrecision && mUnits == moneyLong.mUnits;
	}

	@Override
	public int hashCode() {
		final int result = (int) (mUnits ^ (mUnits >>> 32));
		return 31 * result + mPrecision;
	}

	@Override
	public Money add(final MoneyLong other) {
		long normUnitsRes;
		int precision = mPrecision;
		if (mPrecision == other.mPrecision)
			normUnitsRes = mUnits + other.mUnits;
		else if (mPrecision > other.mPrecision) {
			long multiplier = MoneyFactory.MULTIPLIERS[mPrecision - other.mPrecision];
			long mult = other.mUnits * multiplier;
			if (mult / multiplier != other.mUnits) // overflow check,
													// alternative is double
													// multiplication and
													// compare with
													// Long.MAX_VALUE.
				return other.add(new MoneyBigDecimal(toBigDecimal()));
			normUnitsRes = mUnits + mult;
		} else {
			long multiplier = MoneyFactory.MULTIPLIERS[other.mPrecision - mPrecision];
			long mult = mUnits * multiplier;
			if (mult / multiplier != mUnits) // overflow check
				return other.add(new MoneyBigDecimal(toBigDecimal()));
			normUnitsRes = mult + other.mUnits;
			precision = other.mPrecision;
		}
		// cheap overflow check, it does not cover a case when normUnitsRes get
		// positive
		if (mUnits >= 0 && other.mUnits >= 0 && normUnitsRes < 0)
			return other.add(new MoneyBigDecimal(toBigDecimal()));
		return new MoneyLong(normUnitsRes, precision).normalize();
	}

	private static int compare(final long x, final long y) {
		return (x < y) ? -1 : ((x == y) ? 0 : 1);
	}

	@Override
	protected int compareTo(final MoneyLong other) {
		if (mPrecision == other.mPrecision)
			return compare(mUnits, other.mUnits);
		if (mPrecision < other.mPrecision) {
			final long multiplier = MoneyFactory.MULTIPLIERS[other.mPrecision - mPrecision];
			final long mult = mUnits * multiplier;
			if (mult / multiplier == mUnits) // overflow check
				return compare(mult, other.mUnits);
		}
		if (mPrecision > other.mPrecision) {
			final long multiplier = MoneyFactory.MULTIPLIERS[mPrecision - other.mPrecision];
			final long mult = other.mUnits * multiplier;
			if (mult / multiplier == other.mUnits) // overflow check
				return compare(mUnits, mult);
		}

		// fallback for generic case
		return toBigDecimal().compareTo(other.toBigDecimal());
	}

	/**
	 * If <code>m_units</code> ends with zeroes - reduce the
	 * <code>m_precision</code> accordingly
	 * 
	 * @return Normalized value
	 */
	MoneyLong normalize() {
		// shortcut - must be an even number (to be divisible by 10)
		if ((mUnits & 1) == 1)
			return this;
		int precision = mPrecision;
		long units = mUnits;
		long q, rem;
		while (precision > 0) {
			// todo move odd check here?
			q = units / 10;
			rem = units - ((q << 3) + (q << 1));
			if (rem != 0)
				break;
			--precision;
			units = q;
		}
		if (precision == mPrecision)
			return this;
		else
			return new MoneyLong(units, precision);
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
	public Money multiply(long multiplier) {
		final long resUnits = mUnits * multiplier;
		// fast overflow test - if both values fit in the 32 bits (and
		// positive), they can not overflow
		if (((mUnits | multiplier) & MASK32) == 0)
			return new MoneyLong(resUnits, mPrecision).normalize();

		// slower overflow test - check if we will get the original value back
		// after division. It is not possible
		// in case of overflow.
		final long origUnits = resUnits / multiplier;
		if (origUnits != mUnits) {
			final BigInteger res = BigInteger.valueOf(mUnits).multiply(BigInteger.valueOf(multiplier));
			return MoneyFactory.fromBigDecimal(new BigDecimal(res));
		}
		return new MoneyLong(resUnits, mPrecision).normalize();
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
	public Money multiply(final double multiplier) {
		final double unscaledRes = mUnits * multiplier; // need to apply
														// precision
		// try to check if we got an integer value first
		final long unscaledLng = (long) unscaledRes;
		if (unscaledLng == unscaledRes) // possible overflow is also checked
										// here
			return new MoneyLong(unscaledLng, mPrecision).normalize();

		// 4 is a "safe" precision of this calculation. The higher it is - the
		// less results will end up
		// on the BD branch, but at the same time the more expensive the
		// normalization will be.
		final MoneyLong unscaledLong = MoneyFactory.fromDoubleNoFallback(unscaledRes, 4);
		if (unscaledLong != null && unscaledLong.mPrecision + mPrecision <= MoneyFactory.MAX_ALLOWED_PRECISION) {
			return new MoneyLong(unscaledLong.mUnits, unscaledLong.mPrecision + mPrecision).normalize();
		}
		// slow path via BD. We may still get MoneyLong on this branch if the
		// unscaledRes precision is too high.
		return MoneyFactory.fromBigDecimal(
				toBigDecimal().multiply(new BigDecimal(multiplier, MathContext.DECIMAL64), MathContext.DECIMAL64));
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
		return divide((double) divider, precision);
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
		if (precision > MoneyFactory.MAX_ALLOWED_PRECISION)
			return new MoneyBigDecimal(toBigDecimal()).divide(divider, precision);
		final double unscaledRes = mUnits / divider;
		// We already have m_precision digits of precision. We need to take
		// (precision-m_precision) digits
		// more from the unscaled result. Plus one more digit for rounding.
		final long destRes;
		if (mPrecision < precision) {
			// take precision-m_precision digits after decimal point
			destRes = Math.round(unscaledRes * MoneyFactory.MULTIPLIERS[precision - mPrecision]);
		} else if (mPrecision == precision) {
			// round to long
			destRes = Math.round(unscaledRes);
		} else // if ( m_precision > precision )
		{
			// m_units = 135, m_precision=3, precision = 2 => round 13.5 to 14
			// we can multiply by a floating point value here because we will
			// round the result
			destRes = Math.round(unscaledRes * MoneyFactory.MULTIPLIERS_NEG[mPrecision - precision]);
		}
		return new MoneyLong(destRes, precision).normalize();
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
		if (mPrecision <= maximalPrecision)
			return this;
		MoneyFactory.checkPrecision(maximalPrecision);

		// remove not needed digits
		// we can multiply by floating point values here because we will round
		// the result afterwards
		return new MoneyLong(Math.round(mUnits * MoneyFactory.MULTIPLIERS_NEG[mPrecision - maximalPrecision]),
				maximalPrecision).normalize();
	}
}
