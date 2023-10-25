package com.forgerock.sapi.gateway.ob.uk.framework.consent;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Registry of factories for creating OB Consent objects.
 *
 * Factories are registered by passing a list of factory class names to the constructor, instances of these classes
 * are then created via reflection by invoking a default (no-args) constructor.
 *
 * If any factory fails to be created then the registry fails to be created and throws the exception onwards.
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

    /**
     * Finds the first factory in the registry that is an instance of the requested factory class. This allows the
     * registry to be queried with the interface class and return an implementation class.
     *
     * @param factoryClass the class of the factory to retrieve
     * @return the first registered factory that is an instance of the factoryClass
     * @param <T>
     * @throws IllegalStateException if no factory is found
     */
    public <T> T getConsentFactory(Class<T> factoryClass) {
        return  registry.stream()
                        .filter(factoryClass::isInstance)
                        .findFirst()
                        .map(factoryClass::cast)
                        .orElseThrow(() -> new IllegalStateException("Failed to find factory for class: " + factoryClass));
    }

}
