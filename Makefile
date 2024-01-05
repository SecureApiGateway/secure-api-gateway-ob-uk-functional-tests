name := pr/uk-functional-tests
repo := europe-west4-docker.pkg.dev/sbat-gcr-develop/sapig-docker-artifact
tag  := latest

docker:
	docker build -t ${repo}/securebanking/${name}:${tag} .
	docker push ${repo}/securebanking/${name}:${tag}
