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

echo "Building shoplify"

./gradlew clean
./gradlew build -x test -Pprofile=staging

build_userservice
build_productservice
build_frontendservice
