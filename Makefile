name := uk-functional-tests
repo := sbat-gcr-develop
tag  := latest

docker:
	docker build -t eu.gcr.io/${repo}/securebanking/tests/${name}:${tag} .
	docker push eu.gcr.io/${repo}/securebanking/tests/${name}:${tag}
