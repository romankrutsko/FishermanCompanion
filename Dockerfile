# Use an official OpenJDK runtime as a parent image
FROM openjdk:17-alpine

# Build the application
RUN ./gradlew build

# Start the application
CMD ["java", "-jar", "build/libs/companion-0.0.1-SNAPSHOT.jar"]
