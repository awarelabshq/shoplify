    echo "Building userservice..."
    cd userservice
    gcloud builds submit --config cloudbuild.yaml .
    echo "Pushed userservice..."
    cd ..