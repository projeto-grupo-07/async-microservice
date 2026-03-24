
FROM maven:3.9.5-eclipse-temurin-21 AS builder
WORKDIR /workspace

# Copia apenas o pom para aproveitar cache de dependências
COPY pom.xml ./

# Baixa dependências para cache
RUN mvn -B -f pom.xml -DskipTests dependency:go-offline

# Copia o código e empacota o artefato
COPY src ./src
RUN mvn -B -DskipTests package

FROM eclipse-temurin:21-jre
WORKDIR /app

# Copia o JAR gerado pelo stage de build
COPY --from=builder /workspace/target/*.jar ./app.jar

# Perfil e opções de JVM configuráveis em tempo de execução
ENV SPRING_PROFILES_ACTIVE=prod
ENV JAVA_OPTS=""

# Exponha a porta usada pela aplicação (8081) para evitar conflito com outra app na 8080
EXPOSE 8081

# Se sua aplicação usa features preview da JVM em tempo de execução, ative --enable-preview
# ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS --enable-preview -jar /app/app.jar"]
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar /app/app.jar"]
