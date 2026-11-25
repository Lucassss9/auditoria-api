FROM eclipse-temurin:21-jdk AS build
WORKDIR /app

COPY .mvn/ .mvn/
COPY mvnw pom.xml ./

# O mvnw vem do Windows com CRLF e sem bit de execucao. Sem estas duas linhas
# o build quebra no container com "no such file or directory", que e o shell
# reclamando do \r no fim do shebang.
RUN sed -i 's/\r$//' mvnw && chmod +x mvnw

RUN ./mvnw -B dependency:go-offline

# Inclui src/main/resources/static, que e o front ja compilado pelo ng build.
# Essa pasta esta no .gitignore, entao ela so existe aqui porque o docker build
# le o disco. Rode o ng build antes.
COPY src/ src/

RUN ./mvnw -B clean -DskipTests package


FROM eclipse-temurin:21-jre
WORKDIR /app

RUN useradd --system --uid 1001 auditoria \
    && mkdir -p /app/evidencias \
    && chown -R auditoria:auditoria /app

COPY --from=build /app/target/*.jar app.jar

USER auditoria

# As fotos vao para um volume. Sem volume montado aqui, elas somem no proximo
# deploy e as linhas da tabela evidencia ficam apontando para nada.
ENV EVIDENCIAS_PASTA=/app/evidencias
VOLUME ["/app/evidencias"]

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "/app/app.jar"]
