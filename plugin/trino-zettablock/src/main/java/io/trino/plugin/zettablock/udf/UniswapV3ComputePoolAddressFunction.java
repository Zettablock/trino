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
import io.airlift.slice.Slices;
import io.trino.spi.function.Description;
import io.trino.spi.function.ScalarFunction;
import io.trino.spi.function.SqlNullable;
import io.trino.spi.function.SqlType;
import io.trino.spi.type.StandardTypes;
import org.web3j.abi.TypeEncoder;
import org.web3j.abi.datatypes.Address;
import org.web3j.abi.datatypes.Bytes;
import org.web3j.abi.datatypes.StaticStruct;
import org.web3j.abi.datatypes.generated.Uint24;
import org.web3j.abi.datatypes.generated.Bytes1;
import org.web3j.abi.datatypes.generated.Bytes32;
import org.web3j.crypto.Hash;
import org.web3j.utils.Numeric;

import java.math.BigInteger;

public class UniswapV3ComputePoolAddressFunction {
    private static final String POOL_INIT_CODE_HASH = "0xe34f199b19b2b4f47f68442619d555527d244f78a3297ea89325f843f87b8b54";

    private UniswapV3ComputePoolAddressFunction() {
    }

    @ScalarFunction("UniV3ComputePoolAddr")
    @Description("Compute Uniswap V3 pool address from pool key."
            + "Refer to https://github.com/Uniswap/v3-periphery/blob/75f3b72b4412b41e31c2a2370bb52d55f99ec717/contracts/libraries/PoolAddress.sol#L33")
    @SqlType(StandardTypes.VARCHAR)
    @SqlNullable
    public static Slice uniswapV3ComputePoolAddress(
            @SqlType(StandardTypes.VARCHAR) Slice factory,
            @SqlType(StandardTypes.VARCHAR) Slice token0,
            @SqlType(StandardTypes.VARCHAR) Slice token1,
            @SqlNullable @SqlType(StandardTypes.INTEGER) Integer fee) {
        try {
            String factoryStr = factory.toStringUtf8();
            String token0Str = token0.toStringUtf8();
            String token1Str = token1.toStringUtf8();
            String pool;
            BigInteger token0BigInt = Numeric.toBigInt(token0Str);
            BigInteger token1BigInt = Numeric.toBigInt(token1Str);
            if (token0BigInt.compareTo(token1BigInt) == -1) {
                pool = uniswapV3ComputePoolAddressImpl(factoryStr, token0Str, token1Str, fee);
            } else {
                pool = uniswapV3ComputePoolAddressImpl(factoryStr, token1Str, token0Str, fee);
            }
            return Slices.utf8Slice(pool);
        } catch (Throwable e) {
            return null;
        }
    }

    public static String uniswapV3ComputePoolAddressImpl(
            String factory,
            String token0,
            String token1,
            Integer fee) {
        Bytes prefix = new Bytes1(Numeric.hexStringToByteArray("0xff")); // hex ff
        String poolKey = Hash.sha3(TypeEncoder.encode(
                new StaticStruct(
                        new Address(token0),
                        new Address(token1),
                        new Uint24(fee))));
        Address factoryAddr = new Address(factory);
        // encodePacked has trouble encoding a StaticStruct, so we have to encode them individually
        String secondStructPacked = TypeEncoder.encodePacked(prefix)
                + TypeEncoder.encodePacked(factoryAddr)
                + TypeEncoder.encodePacked(new Bytes32(Numeric.hexStringToByteArray(poolKey)))
                + TypeEncoder.encodePacked(new Bytes32(Numeric.hexStringToByteArray(POOL_INIT_CODE_HASH)));
        String hexStr = Hash.sha3(secondStructPacked);
        return "0x" + hexStr.substring(26); // take the last 40 hexes
    }
}
