# FROM openjdk:16-jdk
# COPY target/*.jar /app/service.jar
# CMD ["java", "-jar", "/app/service.jar"]

#FROM openjdk:jre

FROM openjdk:16-jdk

ADD target/${project.build.finalName}-jar-with-dependencies.jar /opt/ride-schedules.jar
ADD target/docker-extra/run-java/run-java.sh /opt

# See https://github.com/fabric8io-images/run-java-sh/ for more information
# about run-java.sh
CMD JAVA_MAIN_CLASS=com.ride.schedules.RideSchedulesApplication sh /opt/run-java.sh
