service := pr/uk-functional-tests
repo := europe-west4-docker.pkg.dev/sbat-gcr-develop/sapig-docker-artifact
profile := dev-ob
tests := tests_v3_1_10

docker: convertToLower
ifndef tag
	$(warning no tag supplied; latest assumed)
	$(eval TAG=latest)
endif
ifndef setlatest
	$(warning no setlatest true|false supplied; false assumed)
	$(eval setlatest=false)
endif
ifneq (,$(findstring rc,$(tag)))
	$(eval service=rc/uk-functional-tests)
endif
ifneq (,$(findstring RC,$(tag)))
	$(eval service=rc/uk-functional-tests)
endif
	if [ "${setlatest}" = "true" ]; then \
		docker build -t ${repo}/securebanking/${service}:${TAG} -t ${repo}/securebanking/${service}:latest . ; \
		docker push ${repo}/securebanking/${service} --all-tags; \
    else \
   		docker build  -t ${repo}/securebanking/${service}:${TAG} . ; \
   		docker push ${repo}/securebanking/${service}:${TAG}; \
   	fi;

convertToLower:
	$(eval TAG=$(shell echo $(tag) | tr A-Z a-z))

runTests:
	@echo "Running tests suite '${tests}' against '$(profile)' environment"
	sleep 5s
	./gradlew cleanTest ${tests} -Pprofile=${profile}