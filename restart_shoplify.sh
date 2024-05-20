docker stop shoplify_userservice_container
docker rm shoplify_userservice_container
docker pull gcr.io/xenon-height-388203/aware-docker-repo/shoplify-userservice:staging_latest
docker run -d --name shoplify_userservice_container -p 8082:8082 -v /var/logs/userservice:/var/logs/userservice --network shoplify_network --network-alias userservice gcr.io/xenon-height-388203/aware-docker-repo/shoplify-userservice:staging_latest

docker stop shoplify_productservice_container
docker rm shoplify_productservice_container
docker pull gcr.io/xenon-height-388203/aware-docker-repo/shoplify-productservice:staging_latest
docker run -d --name shoplify_productservice_container -p 8083:8083 -v /var/logs/productservice:/var/logs/productservice --network shoplify_network --network-alias productservice gcr.io/xenon-height-388203/aware-docker-repo/shoplify-productservice:staging_latest

docker stop shoplify_frontendservice_container
docker rm shoplify_frontendservice_container
docker pull gcr.io/xenon-height-388203/aware-docker-repo/shoplify-frontendservice:staging_latest
docker run -d --name shoplify_frontendservice_container -p 8081:8081 -v /var/logs/frontendservice:/var/logs/frontendservice --network shoplify_network --network-alias frontendservice gcr.io/xenon-height-388203/aware-docker-repo/shoplify-frontendservice:staging_latest

docker rmi $(docker images -f "dangling=true" -q)