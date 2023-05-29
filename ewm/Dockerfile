FROM amazoncorretto:11-alpine-jdk
ENV JAVA_TOOL_OPTIONS = ""
COPY target/*.jar ewm.jar
ENTRYPOINT ["java","-jar","/ewm.jar"]