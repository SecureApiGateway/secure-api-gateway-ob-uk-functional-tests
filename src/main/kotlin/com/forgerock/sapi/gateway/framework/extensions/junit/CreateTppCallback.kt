package com.forgerock.sapi.gateway.framework.extensions.junit

import com.forgerock.sapi.gateway.framework.data.Tpp
import com.forgerock.sapi.gateway.framework.http.fuel.initFuel
import com.forgerock.sapi.gateway.framework.http.fuel.initFuelAsNewTpp
import com.forgerock.sapi.gateway.framework.utils.FileUtils
import org.junit.jupiter.api.extension.*


class CreateTppCallback : BeforeAllCallback, BeforeEachCallback, ParameterResolver {
    override fun supportsParameter(parameterContext: ParameterContext?, extensionContext: ExtensionContext?): Boolean {
        return TppResource::class.java == parameterContext?.parameter?.type
    }

    override fun resolveParameter(parameterContext: ParameterContext?, extensionContext: ExtensionContext?): Any {
        return extensionContext?.root?.getStore(ExtensionContext.Namespace.GLOBAL)
            ?.getOrComputeIfAbsent<String, TppResource>(
                "tppResource",
                { TppResource(initFuelAsNewTpp().apply { dynamicRegistration() }) },
                TppResource::class.java
            )!!
    }

    override fun beforeEach(context: ExtensionContext?) {
        val tpp: TppResource? = context?.root?.getStore(ExtensionContext.Namespace.GLOBAL)
            ?.getOrComputeIfAbsent<String, TppResource>(
                "tppResource",
                { TppResource(initFuelAsNewTpp().apply { dynamicRegistration() }) },
                TppResource::class.java
            )
        // Need to init fuel with transport keys as we may load cached result
        tpp?.let {
            initFuel(
                FileUtils().getInputStream(it.tpp.privateCert),
                FileUtils().getInputStream(it.tpp.publicCert)
            )
        }
    }


    @Throws(Exception::class)
    override fun beforeAll(context: ExtensionContext) {
        val tpp: TppResource = context.root.getStore(ExtensionContext.Namespace.GLOBAL)
            .getOrComputeIfAbsent<String, TppResource>(
                "tppResource",
                { TppResource(initFuelAsNewTpp().apply { dynamicRegistration() }) },
                TppResource::class.java
            )
        // Need to init fuel with transport keys as we may load cached result

        initFuel(
            FileUtils().getInputStream(tpp.tpp.privateCert),
            FileUtils().getInputStream(tpp.tpp.publicCert)
        )
    }

    class TppResource(val tpp: Tpp) : ExtensionContext.Store.CloseableResource {

        override fun close() {
            // Need to re-init fuel with transport keys as that may have changed
            initFuel(
                FileUtils().getInputStream(tpp.privateCert),
                FileUtils().getInputStream(tpp.publicCert)
            )
            tpp.unregister()
        }
    }
}
