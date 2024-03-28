name := pr/uk-functional-tests
repo := europe-west4-docker.pkg.dev/sbat-gcr-develop/sapig-docker-artifact
tag  := latest
profile := dev-ob
tests := tests_v3_1_10

docker:
	docker build -t ${repo}/securebanking/${name}:${tag} .
	docker push ${repo}/securebanking/${name}:${tag}

runTests:
	@echo "Running tests suite '${tests}' against '$(profile)' environment"
	sleep 5s
	./gradlew cleanTest ${tests} -Pprofile=${profile}