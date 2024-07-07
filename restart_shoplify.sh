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

riskservice() {
  echo "Restarting riskservice"
  docker stop shoplify_riskservice_container
  docker rm shoplify_riskservice_container
  docker pull us-west4-docker.pkg.dev/testchimp/tc-image-repo/shoplify-riskservice:staging_latest
  docker run -d --name shoplify_riskservice_container -p 8084:8084 -v /var/logs/riskservice:/var/logs/riskservice --network shoplify_network --network-alias riskservice us-west4-docker.pkg.dev/testchimp/tc-image-repo/shoplify-riskservice:staging_latest
}

userservice() {
  echo "Restarting userservice"
  docker stop shoplify_userservice_container
  docker rm shoplify_userservice_container
  docker pull us-west4-docker.pkg.dev/testchimp/tc-image-repo/shoplify-userservice:staging_latest
  docker run -d --name shoplify_userservice_container -p 8082:8082 -v /var/logs/userservice:/var/logs/userservice --network shoplify_network --network-alias userservice us-west4-docker.pkg.dev/testchimp/tc-image-repo/shoplify-userservice:staging_latest
}

productservice() {
  echo "Restarting productservice"
  docker stop shoplify_productservice_container
  docker rm shoplify_productservice_container
  docker pull us-west4-docker.pkg.dev/testchimp/tc-image-repo/shoplify-productservice:staging_latest
  docker run -d --name shoplify_productservice_container -p 8083:8083 -v /var/logs/productservice:/var/logs/productservice --network shoplify_network --network-alias productservice us-west4-docker.pkg.dev/testchimp/tc-image-repo/shoplify-productservice:staging_latest
}

frontendservice() {
  echo "Restarting frontendservice"
  docker stop shoplify_frontendservice_container
  docker rm shoplify_frontendservice_container
  docker pull us-west4-docker.pkg.dev/testchimp/tc-image-repo/shoplify-frontendservice:staging_latest
  docker run -d --name shoplify_frontendservice_container -p 8081:8081 -v /var/logs/frontendservice:/var/logs/frontendservice --network shoplify_network --network-alias frontendservice us-west4-docker.pkg.dev/testchimp/tc-image-repo/shoplify-frontendservice:staging_latest
}

if [ "$PROJECTS" == "all" ]; then
    echo "Building all projects..."
    # Add other project build commands here
    frontendservice
    userservice
    productservice
    riskservice
else
    # Check if featureservice is in the project list
    if [[ $PROJECTS == *"frontend"* ]]; then
        frontendservice
    fi
    # Check if adminservice is in the project list
    if [[ $PROJECTS == *"user"* ]]; then
        userservice
    fi
    # Check if receiverservice is in the project list
    if [[ $PROJECTS == *"product"* ]]; then
        productservice
    fi
    # Check if datagenservice is in the project list
    if [[ $PROJECTS == *"risk"* ]]; then
        riskservice
    fi
fi

docker rmi $(docker images -f "dangling=true" -q)