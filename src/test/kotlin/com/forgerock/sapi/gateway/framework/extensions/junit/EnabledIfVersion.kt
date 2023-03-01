package com.forgerock.sapi.gateway.framework.extensions.junit

import com.forgerock.sapi.gateway.framework.http.fuel.initFuel
import com.forgerock.sapi.gateway.ob.uk.support.discovery.rsDiscoveryEnabledOperationsMap
import com.forgerock.sapi.gateway.ob.uk.support.discovery.rsDiscoveryMap
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
 * Special tests Cases:
 * - A resource created in newer version can't be accessed via older version
 * - A resource created in newer version can't be updated via older version
 * - A resource created in newer version can't be deleted via older version
 * - A resource created in older version can be accessed via newer version
 * - A resource created in older version can be updated via newer version
 * - A resource created in older version can be deleted via newer version
 * For that special tests cases we need check if the 'postCreateVersion' is enabled, if not, the test will not run
 * - The 'postCreateVersion' value must not be empty to enable the operations criteria
 *
 * @property type the type of API that must be enabled in the RS discovery
 * @property apiVersion the main version of the API that must be enabled in the RS discovery
 * @Property postCreateVersion *Optional* the newer/older version of the API used in tests to access/update/delete resources created via 'apiVersion'
 * @property operations *Optional* the operations that should match with the enabled operations in the RS discovery
 * @property apis *Optional* the APIs that should be enabled in the RS discovery.
 */
@Target(AnnotationTarget.CLASS, AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.RUNTIME)
annotation class EnabledIfVersion(
    val type: String,
    val apiVersion: String,
    val postCreateVersion: String = "",
    val operations: Array<String> = [],
    vararg val apis: String = [],
    val compatibleVersions: Array<String> = []
)


class EnabledIfVersionCondition : ExecutionCondition {
    init {
        initFuel()
    }

    override fun evaluateExecutionCondition(context: ExtensionContext): ConditionEvaluationResult {
        val element = context
            .element
            .orElseThrow { IllegalStateException() }
        return findAnnotation(element, EnabledIfVersion::class.java)
            .map { annotation -> disableIfNotUsingVersion(annotation) }
            .orElse(ENABLED_BY_DEFAULT)
    }

    /**
     * Main method to evaluate the annotation properties to enable/disable tests
     */
    private fun disableIfNotUsingVersion(
        annotation: EnabledIfVersion
    ): ConditionEvaluationResult {

        // if the evaluation result of operations is disable the test will not run
        val condition = evaluateOperations(annotation)

        return if (condition.isDisabled) {
            condition
        } else {
            val typeApiVersionMatches = rsDiscoveryMap[annotation.type]
                ?.filter { (version, _) -> version == annotation.apiVersion }

            val matchingApis = typeApiVersionMatches
                ?.flatMap { it.second }
                ?.mapNotNull { url -> annotation.apis.firstOrNull { url.endsWith(it) } }

            val matchesAllApis = matchingApis?.containsAll(annotation.apis.toList()) // if equals all apis were present

            return if (annotation.postCreateVersion.isEmpty()) {
                evaluateApiVersion(annotation, typeApiVersionMatches, matchingApis, matchesAllApis)
            } else {
                evaluatePostCreateVersion(annotation, typeApiVersionMatches, matchingApis, matchesAllApis)
            }
        }
    }

    /**
     * Evaluate the operations set in the annotation against the operations enabled map to enable/disable a tests
     */
    private fun evaluateOperations(annotation: EnabledIfVersion): ConditionEvaluationResult {

        return if (annotation.operations.isEmpty()) {
            enabled("@EnabledIfVersion#operations is not present")
        } else {
            // create a map with simple member names filtered by type and apiVersion
            val operations = rsDiscoveryEnabledOperationsMap[annotation.type]
                ?.filter { (version, _) -> version == annotation.apiVersion }
                ?.flatMap { it.second }
                ?.map { it.name }
                ?.toList()

            return when (operations?.isEmpty()) {
                true -> disabled(
                    "Operation/s ${annotation.operations.toList().subtract(operations)} not found " +
                            "in version ${annotation.apiVersion} endpoints"
                )
                else -> {
                    // filter to get all operations that match with annotation.operations
                    val operationsFiltered = operations?.filter { annotation.operations.contains(it) }.orEmpty()
                    // the size of operationsFiltered and annotation.operations must be the same size
                    if (operationsFiltered.size == annotation.operations.size) {
                        enabled(
                            "Found operation/s ${annotation.operations.toList()} in " +
                                    "version ${annotation.apiVersion} endpoints"
                        )
                    } else {
                        disabled(
                            "Operation/s ${
                                annotation.operations.toList().subtract(operations ?: emptyList())
                            } not found " +
                                    "in version ${annotation.apiVersion} endpoints"
                        )
                    }
                }
            }
        }
    }

    /**
     * Evaluate the apiVersion enabled in the RS discovery
     */
    private fun evaluateApiVersion(
            annotation: EnabledIfVersion,
            typeApiVersionMatches: List<Pair<String, List<String>>>?,
            matchingApis: List<String>?,
            matchesAllApis: Boolean?
    ): ConditionEvaluationResult {

        return if (annotation.apis.isEmpty() && typeApiVersionMatches?.any() == true) {
            enabled("Found apis version ${annotation.apiVersion} in $matchingApis")
        } else if (annotation.apis.isNotEmpty() && matchesAllApis == true) {
            enabled(
                "Found all ${annotation.apis.contentToString()} apis $matchingApis in " +
                        "version ${annotation.apiVersion}"
            )
        } else {
            disabled(
                "Apis ${annotation.apis.toList().subtract(matchingApis ?: emptyList())} " +
                        "not found in version ${annotation.apiVersion}"
            )
        }
    }

    /**
     * Evaluate the postCreateVersion enabled in the RS discovery
     */
    private fun evaluatePostCreateVersion(
            annotation: EnabledIfVersion,
            typeApiVersionMatches: List<Pair<String, List<String>>>?,
            matchingApis: List<String>?,
            matchesAllApis: Boolean?
    ): ConditionEvaluationResult {

        val typePostCreateVersionMatches = rsDiscoveryMap[annotation.type]
            ?.filter { (version, _) -> version == annotation.postCreateVersion }

        val matchingPostCreateApis = typePostCreateVersionMatches
            ?.flatMap { it.second }
            ?.mapNotNull { url -> annotation.apis.firstOrNull { url.endsWith(it) } }

        val matchesAllPostCreateApis =
            matchingPostCreateApis?.containsAll(annotation.apis.toList()) // if equals all post create apis were present

        return if (annotation.apis.isEmpty() && typeApiVersionMatches?.any() == true && typePostCreateVersionMatches?.any() == true) {
            enabled(
                "Found apis postCreateVersion ${annotation.postCreateVersion} " +
                        "in $matchesAllPostCreateApis"
            )
        } else if (annotation.apis.isNotEmpty() && matchesAllApis == true && matchesAllPostCreateApis == true && typePostCreateVersionMatches.any()) {
            enabled(
                "Found all $matchesAllPostCreateApis apis " +
                        "in postCreateVersion ${annotation.postCreateVersion}"
            )
        } else {
            disabled(
                "Apis ${annotation.apis.toList().subtract(matchingApis ?: emptyList())} not found " +
                        "in postCreateVersion ${annotation.postCreateVersion} endpoints"
            )
        }

    }

    companion object {
        private val ENABLED_BY_DEFAULT = enabled("@EnabledIfVersion is not present")
    }

}
