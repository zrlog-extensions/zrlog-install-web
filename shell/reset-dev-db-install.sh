./mvnw -Pjar clean package -U
rm -rf conf/install.lock
export TZ="Asia/Shanghai"
java -Dsws.run.mode=dev -jar zrlog-install-web-starter.jar conf/dev-install.json