package com.forgerock.securebanking.framework.extensions.junit

import com.forgerock.securebanking.framework.data.Tpp
import com.forgerock.securebanking.framework.http.fuel.initFuel
import com.forgerock.securebanking.framework.http.fuel.initFuelAsNewTpp
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
                object {}.javaClass.getResourceAsStream(it.tpp.privateCert),
                object {}.javaClass.getResourceAsStream(it.tpp.publicCert)
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
            object {}.javaClass.getResourceAsStream(tpp.tpp.privateCert),
            object {}.javaClass.getResourceAsStream(tpp.tpp.publicCert)
        )
    }

    class TppResource(val tpp: Tpp) : ExtensionContext.Store.CloseableResource {

        override fun close() {
            // Need to re-init fuel with transport keys as that may have changed
            initFuel(
                object {}.javaClass.getResourceAsStream(tpp.privateCert),
                object {}.javaClass.getResourceAsStream(tpp.publicCert)
            )
            tpp.unregister()
        }
    }
}
