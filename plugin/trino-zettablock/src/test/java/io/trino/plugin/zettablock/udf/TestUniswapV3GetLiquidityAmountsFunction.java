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

import org.testng.annotations.Test;

import static io.trino.testing.assertions.Assert.assertEquals;

public class TestUniswapV3GetLiquidityAmountsFunction {

    @Test
    public void testInRangeLiquidityAmounts() {
        Long tickLower = -54000L;
        Long tickUpper = -40140L;
        String liquidity = "1450188690342344788";
        String sqrtPriceX96 = "5347821107469791720822805920";
        String[] amounts = UniswapV3GetLiquidityAmountsFunction.uniswapV3GetLiquidityAmountsImpl(
                sqrtPriceX96,
                tickLower,
                tickUpper,
                liquidity
        );
        assertEquals("10694883051552888351", amounts[0]);
        assertEquals("412443097383405", amounts[1]);
    }

    @Test
    public void testOutOfLowerRangeLiquidityAmounts() {
        Long tickLower = -210000L;
        Long tickUpper = -199000L;
        String liquidity = "650076632089172";
        String sqrtPriceX96 = "1450188690342344788";
        String[] amounts = UniswapV3GetLiquidityAmountsFunction.uniswapV3GetLiquidityAmountsImpl(
                sqrtPriceX96,
                tickLower,
                tickUpper,
                liquidity
        );
        assertEquals("9981693549619681972", amounts[0]);
        assertEquals("0", amounts[1]);
    }

    @Test
    public void testOutOfUpperRangeLiquidityAmounts() {
        Long tickLower = -27120L;
        Long tickUpper = -25200L;
        String liquidity = "3081461833172";
        String sqrtPriceX96 = "43133977623203340760261788069";
        String[] amounts = UniswapV3GetLiquidityAmountsFunction.uniswapV3GetLiquidityAmountsImpl(
                sqrtPriceX96,
                tickLower,
                tickUpper,
                liquidity
        );
        assertEquals("0", amounts[0]);
        assertEquals("80010000000", amounts[1]);
    }
}
