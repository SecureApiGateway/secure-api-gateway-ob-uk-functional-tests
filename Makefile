service := pr/uk-functional-tests
repo := europe-west4-docker.pkg.dev/sbat-gcr-develop/sapig-docker-artifact
profile := dev-ob
tests := tests_v3_1_10

docker:
ifndef tag
	$(warning no tag supplied; latest assumed)
	$(eval tag=latest)
endif
ifndef setlatest
	$(warning no setlatest true|false supplied; false assumed)
	$(eval setlatest=false)
endif
	if [ "${setlatest}" = "true" ]; then \
		docker build -t ${repo}/securebanking/${service}:${tag} -t ${repo}/securebanking/${service}:latest . ; \
		docker push ${repo}/securebanking/${service} --all-tags; \
    else \
   		docker build  -t ${repo}/securebanking/${service}:${tag} .; \
   		docker push ${repo}/securebanking/${service}:${tag}; \
   	fi;

runTests:
	@echo "Running tests suite '${tests}' against '$(profile)' environment"
	sleep 5s
	./gradlew cleanTest ${tests} -Pprofile=${profile}