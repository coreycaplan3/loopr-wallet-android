package org.loopring.looprwallet.core.networking.ethplorer

import io.realm.RealmList
import kotlinx.coroutines.experimental.Deferred
import org.loopring.looprwallet.core.models.loopr.tokens.LooprToken
import org.loopring.looprwallet.core.models.loopr.transfers.LooprTransfer
import org.loopring.looprwallet.core.utilities.BuildUtility
import org.loopring.looprwallet.core.utilities.BuildUtility.FLAVOR_MAINNET
import org.loopring.looprwallet.core.utilities.BuildUtility.FLAVOR_MOCKNET
import org.loopring.looprwallet.core.utilities.BuildUtility.FLAVOR_TESTNET

/**
 * Created by Corey Caplan on 3/17/18.
 *
 * Project: loopr-wallet-android
 *
 * Purpose of Class:
 *
 */
interface LooprEthplorerService {

    companion object {

        fun getInstance(): LooprEthplorerService {
            val buildType = BuildUtility.BUILD_FLAVOR
            return when (buildType) {
                FLAVOR_MOCKNET -> LooprEthplorerServiceMockImpl()
                FLAVOR_TESTNET, FLAVOR_MAINNET -> LooprEthplorerServiceProdImpl()
                else -> throw IllegalArgumentException("Invalid build type, found: $buildType")
            }
        }

    }

    /**
     * Gets a token's information like price and total supply from the network.
     *
     * @param contractAddress The token's [LooprToken.contractAddress] used to get the proper token's
     * information.
     */
    fun getTokenInfo(contractAddress: String): Deferred<LooprToken>

    /**
     * Gets an address's information. This includes ETH and token balance information.
     */
    fun getAddressBalances(address: String): Deferred<RealmList<LooprToken>>

    /**
     * Gets an address's transfer history
     */
    fun getAddressTransferHistory(address: String): Deferred<RealmList<LooprTransfer>>

}