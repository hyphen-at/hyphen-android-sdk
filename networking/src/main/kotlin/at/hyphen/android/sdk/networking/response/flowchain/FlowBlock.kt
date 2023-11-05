package at.hyphen.android.sdk.networking.response.flowchain

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonArray

@Serializable
data class FlowBlock(
    @SerialName("_expandable")
    val expandable: Expandable,

    @SerialName("_links")
    val links: Links,

    @SerialName("block_status")
    val blockStatus: String,

    val header: Header,
    val payload: Payload
) {
    @Serializable
    data class Expandable(
        @SerialName("execution_result")
        val executionResult: String
    )

    @Serializable
    data class Header(
        val height: String,
        val id: String,

        @SerialName("parent_id")
        val parentID: String,

        @SerialName("parent_voter_signature")
        val parentVoterSignature: String,

        val timestamp: String
    )

    @Serializable
    data class Links(
        @SerialName("_self")
        val self: String
    )

    @Serializable
    data class Payload(
        @SerialName("block_seals")
        val blockSeals: JsonArray,

        @SerialName("collection_guarantees")
        val collectionGuarantees: List<CollectionGuarantee>
    )

    @Serializable
    data class CollectionGuarantee(
        @SerialName("collection_id")
        val collectionID: String,

        val signature: String,

        @SerialName("signer_indices")
        val signerIndices: String
    )
}
