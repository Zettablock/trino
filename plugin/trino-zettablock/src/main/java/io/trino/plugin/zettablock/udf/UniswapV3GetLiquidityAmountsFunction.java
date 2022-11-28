/*
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
package io.trino.plugin.zettablock.udf;

import io.airlift.slice.Slice;
import io.trino.spi.block.Block;
import io.trino.spi.block.BlockBuilder;
import io.trino.spi.function.Description;
import io.trino.spi.function.ScalarFunction;
import io.trino.spi.function.SqlNullable;
import io.trino.spi.function.SqlType;
import io.trino.spi.type.StandardTypes;
import org.web3j.abi.datatypes.generated.Uint256;
import org.web3j.utils.Numeric;

import java.math.BigInteger;

import static io.trino.spi.type.VarcharType.VARCHAR;

public class UniswapV3GetLiquidityAmountsFunction {
    private static final int FIXED_POINT96_RESOLUTION = 96;
    private static final BigInteger UINT256_MAX = BigInteger.TWO.pow(256).subtract(BigInteger.ONE);
    private static final BigInteger FIXED_POINT96_Q96 = Numeric.toBigInt("0x1000000000000000000000000");

    private UniswapV3GetLiquidityAmountsFunction() {
    }

    @ScalarFunction("UniV3GetLiquidityAmounts")
    @Description("Get Uniswap V3 token0 and token1 amounts from liquidity."
            + "Refer to https://github.com/Uniswap/v3-periphery/blob/75f3b72b4412b41e31c2a2370bb52d55f99ec717/contracts/libraries/LiquidityAmounts.sol#L120")
    @SqlType("array(varchar)")
    @SqlNullable
    public static Block uniswapV3GetLiquidityAmounts(
            @SqlType(StandardTypes.VARCHAR) Slice sqrtRatioX96,
            @SqlNullable @SqlType(StandardTypes.BIGINT) Long tickLower,
            @SqlNullable @SqlType(StandardTypes.BIGINT) Long tickUpper,
            @SqlType(StandardTypes.VARCHAR) Slice liquidity
    ) {
        try {
            String[] amounts;
            amounts = uniswapV3GetLiquidityAmountsImpl(
                    sqrtRatioX96.toStringUtf8(),
                    tickLower,
                    tickUpper,
                    liquidity.toStringUtf8());
            BlockBuilder results = VARCHAR.createBlockBuilder(null, 2, Uint256.MAX_BYTE_LENGTH);
            if (amounts.length == 2) {
                VARCHAR.writeString(results, amounts[0]);
                VARCHAR.writeString(results, amounts[1]);
            }
            return results.build();
        } catch (Throwable e) {
            return null;
        }
    }

    public static String[] uniswapV3GetLiquidityAmountsImpl(
            String sqrtRatioX96,
            Long tickLower,
            Long tickUpper,
            String liquidity
    ) {
        BigInteger sqrtRatioX96BigInt = new BigInteger(sqrtRatioX96);
        BigInteger sqrtRatioAX96BigInt = getSqrtRatioAtTick(tickLower);
        BigInteger sqrtRatioBX96BigInt = getSqrtRatioAtTick(tickUpper);
        BigInteger liquidityBigInt = new BigInteger(liquidity);
        String amount0 = "0", amount1 = "0";
        if (sqrtRatioX96BigInt.compareTo(sqrtRatioAX96BigInt) < 0) {
            amount0 = getAmount0ForLiquidity(sqrtRatioAX96BigInt, sqrtRatioBX96BigInt, liquidityBigInt);
        } else if (sqrtRatioX96BigInt.compareTo(sqrtRatioBX96BigInt) < 0) {
            amount0 = getAmount0ForLiquidity(sqrtRatioX96BigInt, sqrtRatioBX96BigInt, liquidityBigInt);
            amount1 = getAmount1ForLiquidity(sqrtRatioAX96BigInt, sqrtRatioX96BigInt, liquidityBigInt);
        } else {
            amount1 = getAmount1ForLiquidity(sqrtRatioAX96BigInt, sqrtRatioBX96BigInt, liquidityBigInt);
        }
        return new String[]{amount0, amount1};
    }

    private static String getAmount0ForLiquidity(
            BigInteger sqrtRatioAX96,
            BigInteger sqrtRatioBX96,
            BigInteger liquidity
    ) {
        return liquidity.shiftLeft(FIXED_POINT96_RESOLUTION)
                .multiply(sqrtRatioBX96.subtract(sqrtRatioAX96))
                .divide(sqrtRatioBX96)
                .divide(sqrtRatioAX96).toString();
    }

    private static String getAmount1ForLiquidity(
            BigInteger sqrtRatioAX96,
            BigInteger sqrtRatioBX96,
            BigInteger liquidity
    ) {
        return liquidity
                .multiply(sqrtRatioBX96.subtract(sqrtRatioAX96))
                .divide(FIXED_POINT96_Q96).toString();
    }

    private static BigInteger getSqrtRatioAtTick(Long tick) {
        int shifts = 128;
        BigInteger tickBigInt = BigInteger.valueOf(tick);
        BigInteger absTick = tickBigInt.abs();
        BigInteger ratio = absTick.testBit(0) ? Numeric.toBigInt("0xfffcb933bd6fad37aa2d162d1a594001")
                : Numeric.toBigInt("0x100000000000000000000000000000000");
        if (absTick.testBit(1))
            ratio = ratio.multiply(Numeric.toBigInt("0xfff97272373d413259a46990580e213a")).shiftRight(shifts);
        if (absTick.testBit(2))
            ratio = ratio.multiply(Numeric.toBigInt("0xfff2e50f5f656932ef12357cf3c7fdcc")).shiftRight(shifts);
        if (absTick.testBit(3))
            ratio = ratio.multiply(Numeric.toBigInt("0xffe5caca7e10e4e61c3624eaa0941cd0")).shiftRight(shifts);
        if (absTick.testBit(4))
            ratio = ratio.multiply(Numeric.toBigInt("0xffcb9843d60f6159c9db58835c926644")).shiftRight(shifts);
        if (absTick.testBit(5))
            ratio = ratio.multiply(Numeric.toBigInt("0xff973b41fa98c081472e6896dfb254c0")).shiftRight(shifts);
        if (absTick.testBit(6))
            ratio = ratio.multiply(Numeric.toBigInt("0xff2ea16466c96a3843ec78b326b52861")).shiftRight(shifts);
        if (absTick.testBit(7))
            ratio = ratio.multiply(Numeric.toBigInt("0xfe5dee046a99a2a811c461f1969c3053")).shiftRight(shifts);
        if (absTick.testBit(8))
            ratio = ratio.multiply(Numeric.toBigInt("0xfcbe86c7900a88aedcffc83b479aa3a4")).shiftRight(shifts);
        if (absTick.testBit(9))
            ratio = ratio.multiply(Numeric.toBigInt("0xf987a7253ac413176f2b074cf7815e54")).shiftRight(shifts);
        if (absTick.testBit(10))
            ratio = ratio.multiply(Numeric.toBigInt("0xf3392b0822b70005940c7a398e4b70f3")).shiftRight(shifts);
        if (absTick.testBit(11))
            ratio = ratio.multiply(Numeric.toBigInt("0xe7159475a2c29b7443b29c7fa6e889d9")).shiftRight(shifts);
        if (absTick.testBit(12))
            ratio = ratio.multiply(Numeric.toBigInt("0xd097f3bdfd2022b8845ad8f792aa5825")).shiftRight(shifts);
        if (absTick.testBit(13))
            ratio = ratio.multiply(Numeric.toBigInt("0xa9f746462d870fdf8a65dc1f90e061e5")).shiftRight(shifts);
        if (absTick.testBit(14))
            ratio = ratio.multiply(Numeric.toBigInt("0x70d869a156d2a1b890bb3df62baf32f7")).shiftRight(shifts);
        if (absTick.testBit(15))
            ratio = ratio.multiply(Numeric.toBigInt("0x31be135f97d08fd981231505542fcfa6")).shiftRight(shifts);
        if (absTick.testBit(16))
            ratio = ratio.multiply(Numeric.toBigInt("0x9aa508b5b7a84e1c677de54f3e99bc9")).shiftRight(shifts);
        if (absTick.testBit(17))
            ratio = ratio.multiply(Numeric.toBigInt("0x5d6af8dedb81196699c329225ee604")).shiftRight(shifts);
        if (absTick.testBit(18))
            ratio = ratio.multiply(Numeric.toBigInt("0x2216e584f5fa1ea926041bedfe98")).shiftRight(shifts);
        if (absTick.testBit(19))
            ratio = ratio.multiply(Numeric.toBigInt("0x48a170391f7dc42444e8fa2")).shiftRight(shifts);

        // Solidity: if (tick > 0) ratio = type(uint256).max / ratio;
        if (tickBigInt.signum() == 1) ratio = UINT256_MAX.divide(ratio);

        // Solidity: uint160((ratio >> 32) + (ratio % (1 << 32) == 0 ? 0 : 1));
        return ratio.shiftRight(32).add(
                ratio.mod(BigInteger.ONE.shiftLeft(32)).compareTo(BigInteger.ZERO) == 0 ? BigInteger.ZERO : BigInteger.ONE);
    }
}
