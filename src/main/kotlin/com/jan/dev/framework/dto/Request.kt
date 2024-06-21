package com.jan.dev.framework.dto

import com.jan.dev.framework.annotation.HttpMethod

data class Request(
    val protocol: String? = null,
    val protocolVersion: String? = null,
    val headers: Map<String, String>? = null,
    val body: String? = null,
    val method: HttpMethod? = null,
    val path: String? = null
)

data class WebSocketFrame(
    val fin: Boolean,
    val rsv1: Boolean,
    val rsv2: Boolean,
    val rsv3: Boolean,
    val opcode: Int,
    val masked: Boolean,
    val payloadLength: Long,
    val maskingKey: ByteArray?,
    val payloadData: ByteArray
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as WebSocketFrame

        if (fin != other.fin) return false
        if (rsv1 != other.rsv1) return false
        if (rsv2 != other.rsv2) return false
        if (rsv3 != other.rsv3) return false
        if (opcode != other.opcode) return false
        if (masked != other.masked) return false
        if (payloadLength != other.payloadLength) return false
        if (maskingKey != null) {
            if (other.maskingKey == null) return false
            if (!maskingKey.contentEquals(other.maskingKey)) return false
        } else if (other.maskingKey != null) return false
        return payloadData.contentEquals(other.payloadData)
    }

    override fun hashCode(): Int {
        var result = fin.hashCode()
        result = 31 * result + rsv1.hashCode()
        result = 31 * result + rsv2.hashCode()
        result = 31 * result + rsv3.hashCode()
        result = 31 * result + opcode
        result = 31 * result + masked.hashCode()
        result = 31 * result + payloadLength.hashCode()
        result = 31 * result + (maskingKey?.contentHashCode() ?: 0)
        result = 31 * result + payloadData.contentHashCode()
        return result
    }
}
