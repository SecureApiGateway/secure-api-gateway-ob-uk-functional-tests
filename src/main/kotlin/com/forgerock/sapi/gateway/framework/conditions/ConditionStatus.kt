package com.forgerock.sapi.gateway.framework.conditions

import org.assertj.core.api.Condition
import org.assertj.core.util.Sets
import java.util.function.Predicate

// status for consents payment, consents file payment
private val consentStatus: LinkedHashSet<String> = Sets.newLinkedHashSet(
    "AwaitingAuthorisation",
    "AwaitingUpload",
    "Rejected",
    "Authorised",
    "Consumed"
)

// status for domestic and international payment and multiple authorisations
private val paymentStatus: LinkedHashSet<String> = Sets.newLinkedHashSet(
    "InitiationPending",
    "InitiationFailed",
    "InitiationCompleted",
    "Cancelled",
    "Pending",
    "Rejected",
    "AcceptedSettlementCompleted",
    "AcceptedSettlementInProcess",
    "AcceptedWithoutPosting",
    "AcceptedCreditSettlementCompleted",
    "AwaitingFurtherAuthorisation",
    "Authorised"
)

// payment details status
private val paymentDetailsStatus: LinkedHashSet<String> = Sets.newLinkedHashSet(
    "Accepted",
    "AcceptedCancellationRequest",
    "AcceptedTechnicalValidation",
    "AcceptedCustomerProfile",
    "AcceptedFundsChecked",
    "AcceptedWithChange",
    "Pending",
    "Rejected",
    "AcceptedSettlementInProcess",
    "AcceptedSettlementCompleted",
    "AcceptedWithoutPosting",
    "AcceptedCreditSettlementCompleted"
)

// V4 status for consents payment, consents file payment
private val consentStatusV4: LinkedHashSet<String> = Sets.newLinkedHashSet(
        "AWAU",
        "AWUP",
        "RJCT",
        "AUTH",
        "COND"
)

// V4 payment details status
private val paymentDetailsStatusV4: LinkedHashSet<String> = Sets.newLinkedHashSet(
        "PDNG",
        "ACTC",
        "PATC",
        "ACCP",
        "ACFC",
        "ACSP",
        "ACWC",
        "ACSC",
        "ACWP",
        "ACCC",
        "BLCK",
        "RJCT"
)

object Status {
    val consentCondition = Condition(Predicate<String> { consentStatus.contains(it) }, consentStatus.toString())

    val paymentCondition = Condition(Predicate<String> { paymentStatus.contains(it) }, paymentStatus.toString())

    val paymentDetailsCondition =
        Condition(Predicate<String> { paymentDetailsStatus.contains(it) }, paymentDetailsStatus.toString())
}

object StatusV4 {
    val consentCondition = Condition(Predicate<String> { consentStatusV4.contains(it) }, consentStatusV4.toString())

    val paymentCondition = Condition(Predicate<String> { paymentDetailsStatusV4.contains(it) }, paymentDetailsStatusV4.toString())

    val paymentDetailsCondition =
            Condition(Predicate<String> { paymentDetailsStatusV4.contains(it) }, paymentDetailsStatusV4.toString())
}