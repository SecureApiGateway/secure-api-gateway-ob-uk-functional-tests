package com.forgerock.securebanking.framework.data

data class Application(val transportKeys: Map<String, JwkMsKey>, val keys: Map<String, JwkMsKey>)
