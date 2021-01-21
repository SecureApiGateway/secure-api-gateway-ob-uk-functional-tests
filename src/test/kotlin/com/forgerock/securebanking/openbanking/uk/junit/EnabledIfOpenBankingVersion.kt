package com.forgerock.openbanking.junit

import com.forgerock.openbanking.discovery.rsDiscoveryMap
import com.forgerock.openbanking.initFuel
import org.junit.jupiter.api.extension.ConditionEvaluationResult
import org.junit.jupiter.api.extension.ConditionEvaluationResult.disabled
import org.junit.jupiter.api.extension.ConditionEvaluationResult.enabled
import org.junit.jupiter.api.extension.ExecutionCondition
import org.junit.jupiter.api.extension.ExtensionContext
import org.junit.platform.commons.support.AnnotationSupport.findAnnotation


/**
 * This annotation is an extension of JUnit 5 to enable tests driven by the RS discovery configuration.
 *
 * - If the annotation isn't present the default behaviour is to run the test.
 * - If the annotation is present and the criteria is matched the test will run
 * - If the annotation is present and the criteria isn't matched the test will not run
 *
 * @property type the type of API that must be enabled in the RS discovery
 * @property version the version of the API that must be enabled in the RS discovery
 * @property apis *Optional* the APIs that should be enabled in the RS discovery.
 */
@Target(AnnotationTarget.CLASS, AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.RUNTIME)
annotation class EnabledIfOpenBankingVersion(val type: String, val version: String, vararg val apis: String = [])


class EnabledIfOpenBankingVersionCondition() : ExecutionCondition {
    init {
        initFuel()
    }

    override fun evaluateExecutionCondition(context: ExtensionContext): ConditionEvaluationResult {
        val element = context
                .element
                .orElseThrow { IllegalStateException() }
        return findAnnotation(element, EnabledIfOpenBankingVersion::class.java)
                .map { annotation -> disableIfNotUsingOpenBankingVersion(annotation) }
                .orElse(ENABLED_BY_DEFAULT)
    }

    private fun disableIfNotUsingOpenBankingVersion(
            annotation: EnabledIfOpenBankingVersion): ConditionEvaluationResult {
        val typeVersionMatches = rsDiscoveryMap[annotation.type]
                ?.filter { (version, _) -> version == annotation.version }
        val matchingApis = typeVersionMatches
                        ?.flatMap { it.second }
                        ?.mapNotNull { url -> annotation.apis.firstOrNull { url.endsWith(it) } }
        val matchesAllApis = matchingApis?.containsAll(annotation.apis.toList()) // if equals all apis were present

        return if (annotation.apis.isEmpty() && typeVersionMatches?.any() == true)
            enabled("Found ${annotation.type}, version ${annotation.version} in ${matchingApis}")
        else if (annotation.apis.isNotEmpty() && matchesAllApis == true)
            enabled("Found all matches ${matchingApis} with filter ${annotation.type}, version ${annotation.version}, ${annotation.apis.contentToString()} in ${matchingApis}")
        else
            disabled("Missing matches ${annotation.apis.toList().subtract(matchingApis?:emptyList())} with " +
                    "filter " +
                    "${annotation
                    .type}, version ${annotation.version}, ${annotation.apis.contentToString()} found in ${matchingApis}")
    }

    companion object {

        private val ENABLED_BY_DEFAULT = enabled(
                "@EnabledIfOpenBankingVersion is not present")
    }

}