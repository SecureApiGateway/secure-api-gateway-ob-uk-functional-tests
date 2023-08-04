package com.forgerock.sapi.gateway.ob.uk.framework.configuration

// OB directory access token to create an SSA
val OB_SOFTWARE_ID = System.getenv("obSoftwareId") ?: "Y6NjA9TOn3aMm9GaPtLwkp"
val OB_ORGANISATION_ID = System.getenv("obOrganisationId") ?: "0015800001041REAAY"

val SCOPES_TPP = System.getenv("scopesTpp") ?: "ASPSPReadAccess TPPReadAccess AuthoritiesReadAccess"
val SCOPES_ASPSP = System.getenv("scopesAspsp") ?: "ASPSPReadAccess TPPReadAll AuthoritiesReadAccess"
