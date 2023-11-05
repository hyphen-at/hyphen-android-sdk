package at.hyphen.android.sdk.networking.request.flowchain

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class FlowTransactionRequest(
    val arguments: List<String>,
    val authorizers: List<String>,

    @SerialName("envelope_signatures")
    val envelopeSignatures: List<Signature>,

    @SerialName("gas_limit")
    val gasLimit: String,

    val payer: String,

    @SerialName("payload_signatures")
    val payloadSignatures: List<Signature>,

    @SerialName("proposal_key")
    val proposalKey: ProposalKey,

    @SerialName("reference_block_id")
    val referenceBlockID: String,

    val script: String
) {
    @Serializable
    data class Signature(
        val address: String,

        @SerialName("key_index")
        val keyIndex: String,

        val signature: String
    )

    @Serializable
    data class ProposalKey(
        val address: String,

        @SerialName("key_index")
        val keyIndex: String,

        @SerialName("sequence_number")
        val sequenceNumber: String
    )
}
