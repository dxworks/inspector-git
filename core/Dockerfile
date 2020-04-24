FROM classmethod/openjdk-with-git:8-jdk

ARG JAR_FILE
ADD target/${JAR_FILE} /root/fpptIg/fpptIg.jar

ENTRYPOINT ["java", "-jar", "/root/fpptIg/fpptIg.jar"]
