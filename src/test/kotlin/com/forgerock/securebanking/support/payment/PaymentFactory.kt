package com.forgerock.securebanking.support.payment

import com.google.common.base.Preconditions
import java.nio.charset.StandardCharsets
import java.security.MessageDigest
import java.security.NoSuchAlgorithmException
import java.util.*

/**
 * Generate common OB payment data
 */
class PaymentFactory {
    companion object {

        fun computeSHA256FullHash(contentToEncode: String): String {
            Preconditions.checkNotNull(contentToEncode, "Cannot hash null")
            try {
                val digest = MessageDigest.getInstance("SHA-256")
                val hash = digest.digest(contentToEncode.toByteArray(StandardCharsets.UTF_8))
                return Base64.getEncoder().encodeToString(hash)
            } catch (ex: NoSuchAlgorithmException) {
                throw IllegalStateException("Unknown algorithm for file hash: SHA-256")
            }

        }
    }

}
