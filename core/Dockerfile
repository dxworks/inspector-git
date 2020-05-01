FROM classmethod/openjdk-with-git:8-jdk

ARG JAR_FILE
ADD target/${JAR_FILE} /root/ig/ig.jar
ADD bin/ig.sh /root/ig/ig.sh

WORKDIR /root/ig
