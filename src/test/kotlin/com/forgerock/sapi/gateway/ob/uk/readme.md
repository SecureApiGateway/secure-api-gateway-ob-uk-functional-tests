### Open banking Directory API
- [Directory api swagger(master) file](https://raw.githubusercontent.com/OpenBankingUK/directory-api-specs/master/directory-api-swagger.yaml)
- [Directory api Swagger editor](https://editor.swagger.io/?url=https://raw.githubusercontent.com/OpenBankingUK/directory-api-specs/master/directory-api-swagger.yaml)
### Guide line
- https://openbanking.atlassian.net/wiki/spaces/DZ/pages/1322979574/Open+Banking+Directory+Usage+-+eIDAS+release+Production+-+v1.9

### How tests are organised
Functional tests should be placed under package: `com.forgerock.sapi.gateway.ob.uk.tests.functional`

Each subpackage corresponds to a particular OBIE Read/Write API i.e. `com.forgerock.sapi.gateway.on.uk.tests.functional.account` contains tests for [Account and Transaction API](https://openbankinguk.github.io/read-write-api-site3/v3.1.10/profiles/account-and-transaction-api-profile.html)

Within a particular API package, the subpackages correspond to particular resources within that API i.e. `com.forgerock.sapi.gateway.ob.uk.tests.functional.account.direct.debits` contains tests for the following resource: [Direct Debits](https://openbankinguk.github.io/read-write-api-site3/v3.1.10/resources-and-data-models/aisp/direct-debits.html)

When we reach a package for a resource, then we use the following convention to organise the package:
- api.$version subpackages, contain test logic which can exercise functionality for a particular API version and any functionally compatible versions (NOTE: code in these packages should not contain any JUnit annotations)
- junit.$version subpackages, contains JUnit annotated classes and methods which invoke the test methods in api.$version  
- legacy, contains tests for legacy versions (OBIE version < 3.1.8) which were written prior to this convention being adopted

The code in the JUnit packages should be minimal, it should simply invoke the corresponding function in the api package. 

Making this split allows us to reuse test logic across different API versions. OBIE often release new versions where there
are no changes to a particular API i.e. the accounts API in 3.1.8, 3.1.9 and 3.1.10 is identical. We would like to be able
to invoke the 3.1.8 test logic and point it at API endpoints for each of the aforementioned versions.

#### API subpackage organisation
Example package: `com.forgerock.sapi.gateway.ob.uk.tests.functional.account.balances.api.v3_1_8`

Classes within an API package should be named after the API operation that they test, within this package we see classes for the `GetAccountBalances` and `GetBalances` operations, NOTE: there is no suffix of `Test` as these classes should not use any JUnit annotations.

These classes should allow the version number to be plugged in, in order to allow the logic for the test to be run against a particular API endpoint version. 
In this example, we can use logic written to be compatible with v3.1.8 against v3.1.9 by creating an instance of GetBalances passing OBVersion.v3_1_9 as a constructor param.

Methods within these classes should contain logic to exercise a particular test case.

#### JUnit subpackage organisation
Example package: `com.forgerock.sapi.gateway.ob.uk.tests.functional.account.balances.junit.v3_1_8`

For each class in the corresponding api package, there should be 1 class in this package with the `Test` suffix applied to the name. e.g. `com.forgerock.uk.openbanking.tests.functional.account.balances.junit.v3_1_8.GetAccountBalancesTest`

These classes should contain a method for each test case in the corresponding API package. These methods must use the JUnit @Test annotations and our custom @EnabledIfVersion annotation to control what gets executed. There should be no other logic in the methods, just a call to the particular API method.

When support is added for a new OBIE API version, if there is no change in the spec for a particular resource, then all we need to do is add a new junit class to a package for the new version and call the existing api methods, passing the new version as a constructor param.


