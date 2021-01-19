package com.forgerock.openbanking

import org.assertj.core.api.Condition
import org.assertj.core.util.Sets
import java.util.function.Predicate

// status for consents payment, consents file payment
private val consentStatus: LinkedHashSet<String> = Sets.newLinkedHashSet(
        "AwaitingAuthorisation",
        "AwaitingUpload",
        "Rejected",
        "Authorised",
        "Consumed")
// status for domestic and international payment and multiple authorisations
private val paymentStatus: LinkedHashSet<String> = Sets.newLinkedHashSet<String>(
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
private val paymentDetailsStatus: LinkedHashSet<String> = Sets.newLinkedHashSet<String>(
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
        "AcceptedCreditSettlementCompleted",
        "Cancelled",
        "NoCancellationProcess",
        "PartiallyAcceptedCancellationRequest",
        "PartiallyAcceptedTechnicalCorrect",
        "PaymentCancelled",
        "PendingCancellationRequest",
        "Received",
        "RejectedCancellationRequest")

object Status {
    val consentCondition = Condition<String>(Predicate<String> { consentStatus.contains(it) }, consentStatus.toString())

    val paymentCondition = Condition<String>(Predicate<String> { paymentStatus.contains(it) }, paymentStatus.toString())

    val paymentDetailsCondition = Condition<String>(Predicate<String> { paymentDetailsStatus.contains(it) }, paymentDetailsStatus.toString())
}
