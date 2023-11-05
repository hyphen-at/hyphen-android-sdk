package at.hyphen.android.sdk.flow

import at.hyphen.android.sdk.core.Hyphen
import at.hyphen.android.sdk.core.crypto.HyphenCryptography
import at.hyphen.android.sdk.networking.HyphenNetworking
import com.nftco.flow.sdk.Flow
import com.nftco.flow.sdk.FlowAddress
import com.nftco.flow.sdk.FlowArgument
import com.nftco.flow.sdk.FlowSignature
import com.nftco.flow.sdk.bytesToHex
import com.nftco.flow.sdk.crypto.Crypto
import com.nftco.flow.sdk.flowTransaction
import com.skydoves.sandwich.getOrThrow
import okio.ByteString.Companion.decodeHex
import okio.ByteString.Companion.toByteString
import timber.log.Timber

object HyphenFlow {

    internal val accessAPI = Flow.newAccessApi(
        if (Hyphen.network == Hyphen.NetworkType.TESTNET)
            "access.devnet.nodes.onflow.org" else
            "access.mainnet.nodes.onflow.org",
        9000
    )

    /// @returns: TransactionId
    @OptIn(ExperimentalStdlibApi::class)
    suspend fun signAndSendTransaction(
        cadenceScript: String,
        arguments: List<FlowArgument>,
        withAuthorizer: Boolean = true,
    ): String {

        val hyphenAccount = HyphenNetworking.Account.getAccount().getOrThrow()
        val flowAddress = FlowAddress(hyphenAccount.addresses.first().address)
        val keys = HyphenNetworking.Key.getKeys().getOrThrow()
        val deviceKeyIndex =
            keys.first { it.publicKey == HyphenCryptography.getPublicKeyHex() }.keyIndex

        val payMasterAddress =
            if (Hyphen.network == Hyphen.NetworkType.TESTNET) "0xe22cea2c515f26e6" else "0xd998bea00bb8d39c"

        val latestBlockId = accessAPI.getLatestBlockHeader().id

        var payload = flowTransaction {
            referenceBlockId(latestBlockId)
            gasLimit(9999)

            script {
                cadenceScript
            }

            arguments(arguments.toMutableList())

            proposalKey {
                address = flowAddress
                keyIndex = getAccountKey(flowAddress, deviceKeyIndex).id
                sequenceNumber = getAccountKey(flowAddress, deviceKeyIndex).sequenceNumber
            }

            payerAddress {
                FlowAddress(payMasterAddress)
            }

            if (withAuthorizer) {
                authorizers {
                    address {
                        flowAddress
                    }
                }
            }
        }

        val deviceKeyRawSignData =
            HyphenCryptography.signData(
                normalize("FLOW-V0.0-transaction") + payload.canonicalPayload
            )!!

        payload = payload.addPayloadSignature(
            address = flowAddress,
            keyIndex = deviceKeyIndex,
            signature = FlowSignature(
                Crypto.normalizeSignature(
                    signature = deviceKeyRawSignData,
                    ecCoupleComponentSize = 32
                ).toByteString().hex()
            )
        )

        val serverKeySignData =
            HyphenNetworking.Sign.signTransactionWithServerKey(
                (normalize("FLOW-V0.0-transaction") + payload.canonicalPayload).toHexString()
            ).getOrThrow().signature

        payload = payload.addPayloadSignature(
            address = FlowAddress(serverKeySignData.addr),
            keyIndex = serverKeySignData.keyId.toInt(),
            signature = FlowSignature(hex = serverKeySignData.signature)
        )

        val payMasterKeySignData =
            HyphenNetworking.Sign.signTransactionWithPayMasterKey(
                (normalize("FLOW-V0.0-transaction") + payload.canonicalAuthorizationEnvelope).toByteString()
                    .hex()
            ).getOrThrow().signature


        payload = payload.addEnvelopeSignature(
            address = FlowAddress(payMasterAddress),
            keyIndex = payMasterKeySignData.keyId.toInt(),
            signature = FlowSignature(payMasterKeySignData.signature.decodeHex().toByteArray())
        )

        val txId = accessAPI.sendTransaction(transaction = payload)
        Timber.tag("HyphenFlow").e(txId.bytes.bytesToHex())
        waitForSeal(txId)

        return txId.bytes.bytesToHex()
    }
}
