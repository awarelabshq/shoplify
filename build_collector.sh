build_collector() {
    echo "Building shoplify otel collector..."
    gcloud builds submit --config cloudbuild-collector-staging.yaml .
    echo "Pushed collector..."
    cd ..
}

build_collector