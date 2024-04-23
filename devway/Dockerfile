# Use an official OpenJDK runtime as a parent image
FROM openjdk:17

RUN #microdnf install findutils

# Set the working directory in the container
WORKDIR /app

## Copy the entire project directory (including gradlew and gradle/) into the working directory
#COPY . .
#
## Ensure the Gradle wrapper script is executable
#RUN chmod +x ./gradlew
#
## Run the Gradle build
#RUN ./gradlew build
#
## Remove any unwanted jars
#RUN rm build/libs/*-plain.jar

# Copy the JAR file from build output to a simpler path
COPY build/libs/*.jar app.jar

# Run the JAR file
ENTRYPOINT ["java","-Djava.security.egd=file:/dev/./urandom","-jar","/app/app.jar"]
