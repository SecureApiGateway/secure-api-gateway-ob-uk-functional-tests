package com.forgerock.openbanking.junit

import com.forgerock.openbanking.Tpp
import com.forgerock.openbanking.initFuel
import com.forgerock.openbanking.initFuelAsNewTpp
import org.junit.jupiter.api.extension.*


class CreateTppCallback : BeforeAllCallback, BeforeEachCallback, ParameterResolver {
    override fun supportsParameter(parameterContext: ParameterContext?, extensionContext: ExtensionContext?): Boolean {
        return TppResource::class.java == parameterContext?.getParameter()?.type
    }

    override fun resolveParameter(parameterContext: ParameterContext?, extensionContext: ExtensionContext?): Any {
        return extensionContext?.root?.getStore(ExtensionContext.Namespace.GLOBAL)?.getOrComputeIfAbsent<String, TppResource>("tppResource", { TppResource(initFuelAsNewTpp().apply { dynamicRegistration() }) }, TppResource::class.java)!!
    }

    override fun beforeEach(context: ExtensionContext?) {
        val tpp: TppResource? = context?.root?.getStore(ExtensionContext.Namespace.GLOBAL)?.getOrComputeIfAbsent<String, TppResource>("tppResource", { TppResource(initFuelAsNewTpp().apply { dynamicRegistration() }) }, TppResource::class.java)
        // Need to init fuel with transport keys as we may load cached result
        tpp?.let { initFuel(it.tpp.privateCert.byteInputStream(), it.tpp.publicCert.byteInputStream()) }
    }


    @Throws(Exception::class)
    override fun beforeAll(context: ExtensionContext) {
        val tpp: TppResource = context.root.getStore(ExtensionContext.Namespace.GLOBAL).getOrComputeIfAbsent<String, TppResource>("tppResource", { TppResource(initFuelAsNewTpp().apply { dynamicRegistration() }) }, TppResource::class.java)
        // Need to init fuel with transport keys as we may load cached result
        initFuel(tpp.tpp.privateCert.byteInputStream(), tpp.tpp.publicCert.byteInputStream())
    }

    class TppResource(val tpp: Tpp) : ExtensionContext.Store.CloseableResource {

        override fun close() {
            // Need to re-init fuel with transport keys as that may have changed
            initFuel(tpp.privateCert.byteInputStream(), tpp.publicCert.byteInputStream())
            tpp.unregister()
        }
    }
}