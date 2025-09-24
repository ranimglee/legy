####################################
# 1) BUILD STAGE: compile with Maven
####################################
FROM maven:3.9.9-eclipse-temurin-21 AS builder

# 1.1 Provide a profile argument (default = dev)
ARG PROFILE=dev
ENV PROFILE=${PROFILE}

# 1.2 Set the working directory
WORKDIR /workspace

# 1.3 Copy only pom.xml and module poms first (for cache optimization)
COPY pom.xml .
COPY leggy-application/pom.xml leggy-application/pom.xml
COPY ordering/pom.xml ordering/pom.xml
COPY delivery/pom.xml delivery/pom.xml
COPY payment/pom.xml payment/pom.xml
COPY restaurant/pom.xml restaurant/pom.xml
COPY user/pom.xml user/pom.xml
COPY social/pom.xml social/pom.xml
COPY recommendation/pom.xml recommendation/pom.xml
COPY support/pom.xml support/pom.xml
COPY shared-kernel/pom.xml shared-kernel/pom.xml
COPY analytics/pom.xml analytics/pom.xml


# 1.4 Download dependencies (cache layer)
RUN mvn dependency:go-offline -B

# 1.5 Now copy the full source code
COPY . .

# 1.6 Build all modules (skip tests, activate profile)
RUN mvn clean install -DskipTests -P ${PROFILE}


####################################
# 2) RUNTIME STAGE: minimal runtime
####################################
FROM eclipse-temurin:21-jdk-jammy

# 2.1 Create non-root user
RUN addgroup --system spring && adduser --system spring --ingroup spring

# 2.2 Set working directory
WORKDIR /app

# 2.3 Copy only the application jar
COPY --from=builder /workspace/leggy-application/target/leggy-application-0.0.1-SNAPSHOT.jar app.jar

# 2.4 Set permissions
USER spring:spring

# 2.5 Expose application port
EXPOSE 8084

# 2.6 Start the application
ENTRYPOINT ["java", "-jar", "app.jar"]