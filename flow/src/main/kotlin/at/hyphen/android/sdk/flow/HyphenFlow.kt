package at.hyphen.android.sdk.flow

import at.hyphen.android.sdk.core.Hyphen
import at.hyphen.android.sdk.core.crypto.HyphenCryptography
import at.hyphen.android.sdk.networking.HyphenNetworking
import com.nftco.flow.sdk.Flow
import com.nftco.flow.sdk.FlowAddress
import com.nftco.flow.sdk.FlowArgument
import com.nftco.flow.sdk.FlowScript
import com.nftco.flow.sdk.FlowSignature
import com.nftco.flow.sdk.FlowTransaction
import com.nftco.flow.sdk.FlowTransactionProposalKey
import com.nftco.flow.sdk.bytesToHex
import com.nftco.flow.sdk.crypto.Crypto
import com.skydoves.sandwich.getOrThrow
import okio.ByteString.Companion.decodeHex
import okio.ByteString.Companion.toByteString
import timber.log.Timber

object HyphenFlow {

    /// @returns: TransactionId
    @OptIn(ExperimentalStdlibApi::class)
    suspend fun signAndSendTransaction(
        cadenceScript: String,
        arguments: List<FlowArgument>,
    ): String {
        val accessAPI = Flow.newAccessApi(
            if (Hyphen.network == Hyphen.NetworkType.TESTNET)
                "access.devnet.nodes.onflow.org" else
                "access.mainnet.nodes.onflow.org",
            9000
        )

        val hyphenAccount = HyphenNetworking.Account.getAccount().getOrThrow()
        val flowAddress = FlowAddress(hyphenAccount.addresses.first().address)
        val keys = HyphenNetworking.Key.getKeys().getOrThrow()
        val deviceKeyIndex =
            keys.first { it.publicKey == HyphenCryptography.getPublicKeyHex() }.keyIndex
//        val deviceKeyIndex = 7

        val payMasterAddress =
            if (Hyphen.network == Hyphen.NetworkType.TESTNET) "0xe22cea2c515f26e6" else "0xd998bea00bb8d39c"

        val latestBlockId = accessAPI.getLatestBlockHeader().id
        // val latestBlockId = getLatestBlockId().getOrThrow()

        var payload = FlowTransaction(
            script = FlowScript(cadenceScript.encodeToByteArray()),
            arguments = arguments,
            referenceBlockId = latestBlockId,
            gasLimit = 9999,
            proposalKey = FlowTransactionProposalKey(
                address = flowAddress,
                keyIndex = deviceKeyIndex,
                sequenceNumber = 0,
            ),
            payerAddress = FlowAddress(payMasterAddress),
            authorizers = listOf(flowAddress),
        )

        val serverKeySignData =
            HyphenNetworking.Sign.signTransactionWithServerKey(
                payload.canonicalPayload.toHexString()
            ).getOrThrow().signature

        val deviceKeyRawSignData =
            HyphenCryptography.signData(payload.canonicalPayload)!!

        payload = payload.addPayloadSignature(
            address = FlowAddress(serverKeySignData.addr),
            keyIndex = serverKeySignData.keyId.toInt(),
            signature = FlowSignature(serverKeySignData.signature.decodeHex().toByteArray())
        )

        payload = payload.addPayloadSignature(
            address = flowAddress,
            keyIndex = deviceKeyIndex,
            signature = FlowSignature(
                Crypto.normalizeSignature(
                    signature = deviceKeyRawSignData,
                    ecCoupleComponentSize = 32
                )
            )
        )

        val payMasterKeySignData =
            HyphenNetworking.Sign.signTransactionWithPayMasterKey(
                payload.canonicalPaymentEnvelope.toByteString().hex()
            )
                .getOrThrow().signature


        payload = payload.addEnvelopeSignature(
            address = FlowAddress(payMasterAddress),
            keyIndex = payMasterKeySignData.keyId.toInt(),
            signature = FlowSignature(payMasterKeySignData.signature.decodeHex().toByteArray())
        )

        val txId = accessAPI.sendTransaction(transaction = payload)

        Timber.tag("HyphenFlow").e(txId.bytes.bytesToHex())

        return ""
    }
}
