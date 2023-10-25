package com.forgerock.sapi.gateway.ob.uk.framework.consent;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Registry of factories for creating OB Consent objects
 */
public class ConsentFactoryRegistry {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    private final Set<Object> registry = new HashSet<>();

    public ConsentFactoryRegistry(List<String> factoryClassNames) {
        factoryClassNames.forEach(this::registerFactory);
    }

    private void registerFactory(String className) {
        logger.info("Registering factory: {}", className);
        try {
            registry.add(Class.forName(className).getDeclaredConstructor().newInstance());
        } catch (Exception e) {
            throw new IllegalStateException("Failed to register consent factory for className: " + className, e);
        }
    }

    public <T> T getConsentFactory(Class<T> factoryClass) {
        return  registry.stream()
                        .filter(factoryClass::isInstance)
                        .findFirst()
                        .map(factoryClass::cast)
                        .orElseThrow(() -> new IllegalStateException("Failed to find factory for class: " + factoryClass));
    }

}
