# Use official Java image
FROM eclipse-temurin:21-jdk

# Set working directory
WORKDIR /app

# Copy project files
COPY . .

# Give permission to mvnw
RUN chmod +x mvnw

# Build the application
RUN ./mvnw clean package -DskipTests

# Run the jar
CMD ["java", "-jar", "target/telegram-approval-backend-0.0.1-SNAPSHOT.jar"]