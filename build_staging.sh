#!/bin/bash

# Parse command-line arguments
while [[ $# -gt 0 ]]; do
    key="$1"
    case $key in
        --projects=*)
        PROJECTS="${1#*=}"
        shift
        ;;
        *)
        echo "Unknown option: $1"
        exit 1
        ;;
    esac
    shift
done

build_frontendservice() {
    echo "Building shoplify frontend service..."
    cd frontendservice
    gcloud builds submit --config cloudbuild-staging.yaml .
    echo "Pushed frontend..."
    cd ..
}

build_userservice() {
    echo "Building shoplify user service..."
    cd userservice
    gcloud builds submit --config cloudbuild-staging.yaml .
    echo "Pushed userservice..."
    cd ..
}

build_productservice() {
    echo "Building shoplify product service..."
    cd productservice
    gcloud builds submit --config cloudbuild-staging.yaml .
    echo "Pushed productservice..."
    cd ..
}

build_riskservice() {
    echo "Building shoplify risk service..."
    cd riskservice
    gcloud builds submit --config cloudbuild-staging.yaml .
    echo "Pushed riskservice..."
    cd ..
}

echo "Building shoplify"

./gradlew clean
./gradlew build -x test -Pprofile=staging

if [ "$PROJECTS" == "all" ]; then
    echo "Building all projects..."
    # Add other project build commands here
    build_frontendservice
    build_userservice
    build_productservice
    build_riskservice
else
    # Check if featureservice is in the project list
    if [[ $PROJECTS == *"frontend"* ]]; then
        build_frontendservice
    fi
    # Check if adminservice is in the project list
    if [[ $PROJECTS == *"user"* ]]; then
        build_userservice
    fi
    # Check if receiverservice is in the project list
    if [[ $PROJECTS == *"product"* ]]; then
        build_productservice
    fi
    # Check if datagenservice is in the project list
    if [[ $PROJECTS == *"risk"* ]]; then
        build_riskservice
    fi
fi
