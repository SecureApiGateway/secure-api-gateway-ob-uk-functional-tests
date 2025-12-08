service := pr/uk-functional-tests
repo := europe-west4-docker.pkg.dev/sbat-gcr-develop/sapig-docker-artifact
profile := dev-cdk-ob
tests := tests_v4_0_0
latesttagversion := latest

docker:
ifndef tag
	$(warning no tag supplied; latest assumed)
	$(eval TAG=latest)
else
	$(eval TAG=$(shell echo $(tag) | tr A-Z a-z))
endif
ifndef setlatest
	$(warning no setlatest true|false supplied; false assumed)
	$(eval setlatest=false)
endif
	@if [ "${setlatest}" = "true" ]; then \
		docker build --build-arg FR_ARTIFACTORY_USER=${FR_ARTIFACTORY_USER} --build-arg FR_ARTIFACTORY_USER_ENCRYPTED_PASSWORD=${FR_ARTIFACTORY_USER_ENCRYPTED_PASSWORD} -t ${repo}/securebanking/${service}:${TAG} -t ${repo}/securebanking/${service}:${latesttagversion} . ; \
		docker push ${repo}/securebanking/${service} --all-tags; \
    else \
   		docker build --build-arg FR_ARTIFACTORY_USER=${FR_ARTIFACTORY_USER} --build-arg FR_ARTIFACTORY_USER_ENCRYPTED_PASSWORD=${FR_ARTIFACTORY_USER_ENCRYPTED_PASSWORD} -t ${repo}/securebanking/${service}:${TAG} . ; \
   		docker push ${repo}/securebanking/${service}:${TAG}; \
   	fi;

runTests:
	@echo "Running tests suite '${tests}' against '${profile}' environment"
	sleep 5s
	./gradlew cleanTest ${tests} -Pprofile=${profile}