.SHELLFLAGS := -eu -o pipefail -c
MAKEFLAGS += --warn-undefined-variables
SHELL = /bin/bash
.SUFFIXES:

export GCLOUD_PROJECT := $(shell gcloud config get-value project 2>/dev/null)

templates = kustomization.yaml \
            skaffold.yaml
manifest = kustomize_generated_manifest.yaml
targets = target/zeebe-get-started-java-client-0.1.0-jar-with-dependencies.jar

.PHONY: all
all: kustomize

.PHONY: clean
clean:
	rm $(templates) $(manifest)

#runs kustomize to make one big yaml manifest
.PHONY: manifest
manifest: $(templates)
	kustomize build > $(manifest)

#runs kubectl apply on the generated manifest
.PHONY: apply
apply: manifest $(templates)
	kubectl apply -f $(manifest)

#runs skaffold
.PHONY: skaffold
skaffold: $(templates) $(targets)
	skaffold run

#calls envsubst to replace things like GCLOUD_PROJECT
$(templates): %.yaml: %.yaml.tmpl
	envsubst < $^ > $@

#run maven clean package
$(targets):
	docker run -it --rm --name my-maven-project -v "$(CURDIR)":/usr/src/mymaven -w /usr/src/mymaven maven:3-jdk-8-slim mvn clean package
