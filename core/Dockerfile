FROM classmethod/openjdk-with-git:8-jdk

ARG JAR_FILE
ADD target/${JAR_FILE} /root/ig/ig.jar

ENTRYPOINT ["java", "-jar", "/root/ig/ig.jar"]
