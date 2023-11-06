package at.hyphen.android.sdk.flow

import com.nftco.flow.sdk.FlowAccount
import com.nftco.flow.sdk.FlowAccountKey
import com.nftco.flow.sdk.FlowAddress
import com.nftco.flow.sdk.FlowId
import com.nftco.flow.sdk.FlowTransactionResult
import com.nftco.flow.sdk.FlowTransactionStatus

internal fun getAccount(address: FlowAddress): FlowAccount =
    HyphenFlow.accessAPI.getAccountAtLatestBlock(address)!!

internal fun getAccountKey(address: FlowAddress, keyIndex: Int): FlowAccountKey {
    val account = getAccount(address)
    return account.keys[keyIndex]
}

internal fun normalize(tag: String): ByteArray {
    val bytes = tag.toByteArray(Charsets.UTF_8)
    return when {
        bytes.size > 32 -> throw IllegalArgumentException("Domain tags cannot be longer than 32 characters")
        bytes.size < 32 -> bytes + ByteArray(32 - bytes.size)
        else -> bytes
    }
}

fun getTransactionResult(txID: FlowId): FlowTransactionResult {
    FlowId("")
    val txResult = HyphenFlow.accessAPI.getTransactionResultById(txID)!!
    if (txResult.errorMessage.isNotEmpty()) {
        throw Exception(txResult.errorMessage)
    }
    return txResult
}

fun waitForSeal(txID: FlowId): FlowTransactionResult {
    var txResult: FlowTransactionResult
    while (true) {
        txResult = getTransactionResult(txID)
        if (txResult.status == FlowTransactionStatus.SEALED) {
            return txResult
        }
        Thread.sleep(1000)
    }
}
