./mvnw -Pjar clean package -U
rm -rf conf/install.lock
java -Dsws.run.mode=dev -jar install-web-starter.jar conf/demo-install.json ${1}