FROM gcr.io/distroless/java17-debian11
COPY build/libs/mine-saker-api-all.jar app/app.jar

ARG MAX_HEAP="-XX:MaxRAMPercentage=65"
ENV JDK_JAVA_OPTIONS="$MAX_HEAP"

ENV PORT=8080
EXPOSE $PORT
WORKDIR app
CMD ["app.jar"]
