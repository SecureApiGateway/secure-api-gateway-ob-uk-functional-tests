package com.forgerock.sapi.gateway.framework.data

data class Application(val transportKeys: Map<String, JwkMsKey>, val keys: Map<String, JwkMsKey>)
