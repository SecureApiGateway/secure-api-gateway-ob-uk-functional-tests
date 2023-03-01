package com.forgerock.sapi.gateway.ob.uk.support.general

class GeneralFactory {
    companion object {
        fun urlSubstituted(url: String, replaceable: Map<String, String>): String {
            var replaced = url
            for (replace in replaceable) replaced = replaced.replace("{${replace.key}}", replace.value)
            return replaced
        }
    }
}
