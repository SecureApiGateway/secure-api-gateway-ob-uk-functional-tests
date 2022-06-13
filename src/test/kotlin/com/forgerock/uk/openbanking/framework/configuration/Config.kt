package com.forgerock.uk.openbanking.framework.configuration

// OB directory access token to create an SSA
val OB_SOFTWARE_ID = System.getenv("obSoftwareId") ?: "ebSqTNqmQXFYz6VtWGXZAa"
val OB_ORGANISATION_ID = System.getenv("obOrganisationId") ?: "0015800001041REAAY"

val SCOPES_TPP = System.getenv("scopesTpp") ?: "ASPSPReadAccess TPPReadAccess AuthoritiesReadAccess"
val SCOPES_ASPSP = System.getenv("scopesAspsp") ?: "ASPSPReadAccess TPPReadAll AuthoritiesReadAccess"
