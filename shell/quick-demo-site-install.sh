./mvnw -Pjar clean package -U
rm -rf conf/install.lock
export TZ="Asia/Shanghai"
java -Dsws.run.mode=dev -jar install-web-starter.jar conf/demo-install.json ${1}