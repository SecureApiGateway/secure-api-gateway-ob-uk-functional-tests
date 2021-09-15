### OpenSSL config files
> :warning: **Please, when you generate and copy the new certificates to `automating-testing`, update the `automating-testing/readme.md` file with the new certificates data**

> :information_source: A Gradle task will copy the certificates from `automating-testing` to `resources/com/forgerock/uk/openbanking/eidas` directory before build.
> 
The config files hold some properties that we need to generate the CSR for OBIE
- openssl/config/**obwac.conf**
- openssl/config/**obseal.conf**
> run the commands on `certs` root directory
```shell
cd certs
```
### generate obwac.csr
```shell
# with no protected key: -nodes It tells OpenSSL not to encrypt the key
openssl req -new -config openssl/config/obwac.conf -nodes -out OBWac.csr -keyout OBWac.key
```
```shell
# with protected key, in the config file obwac.conf the default value is encrypt the key
openssl req -new -config openssl/config/obwac.conf -passout file:openssl/config/passphrase.txt -out OBWac.csr -keyout OBWac.key
```
```shell
# to check if key is encrypted
openssl rsa -text -noout -in OBWac.key
# 'Enter pass phrase for OBWac.key:' will appears if the key is encrypted
```
### Generate obseal.csr
```shell
# with a new no protected key: -nodes It tells OpenSSL not to encrypt the key
openssl req -new -config openssl/config/obseal.conf -nodes -out OBSeal.csr -keyout OBSeal.key
```
```shell
# with a new protected key, in the config file obseal.conf the default value is encrypt the key
openssl req -new -config openssl/config/obseal.conf -passout file:openssl/config/passphrase.txt -out OBSeal.csr -keyout OBSeal.key
```
```shell
# with existing key protected by the passphrase file
openssl req -new -config openssl/config/obseal.conf -passin file:openssl/config/passphrase.txt -out OBSeal.csr -key OBWac.key
```
```shell
# with no protected existing key
openssl req -new -config openssl/config/obseal.conf -out OBSeal.csr -key OBWac.key
```
### Check the key
```shell
# to check if key is encrypted
openssl rsa -text -noout -in OBWac.key
# 'Enter pass phrase for OBWac.key:' will appears if the key is encrypted
```
### Validate obseal CSR
```shell
openssl asn1parse -in OBWac.csr -inform PEM
```
### Super command
> run that command on `certs` folder to generate the CSR
```shell
GFOLDER=$(date +"%F_%l_%M_%S") && \
mkdir automating-testing/$GFOLDER && \
openssl req -new -config openssl/config/obwac.conf -nodes -out automating-testing/$GFOLDER/OBWac.csr -keyout automating-testing/$GFOLDER/OBWac.key && \
openssl req -new -config openssl/config/obseal.conf -nodes -out automating-testing/$GFOLDER/OBSeal.csr -keyout automating-testing/$GFOLDER/OBSeal.key
```
### Script
```shell
./bin/generate-eidas-csr.sh
```
> You will find the csr generated for OBSeal and OBWac in `automatic-testing/{DATE}/`, and now you can replace the existing ones in the `automatic-testing` root folder to use them.

> :warning: **Please, when you copy the new certificates, update the `automating-testing/readme.md` file with the new certificates data**

> :information_source: A Gradle task will copy the certificates from `automating-testing` to the resources directory before build.
