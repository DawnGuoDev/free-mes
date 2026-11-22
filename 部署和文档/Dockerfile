# syntax=docker/dockerfile:1.4
# 后端从源码构建：先安装 metaxk-pro，再打包 new_open_mes_server。
FROM maven:3.8.6-openjdk-11 AS builder
WORKDIR /workspace

COPY source-code/metaxk-pro /workspace/metaxk-pro
COPY source-code/new_open_mes_server /workspace/new_open_mes_server

RUN set -eux; \
    cd /workspace/metaxk-pro; \
    mvn -B -DskipTests install; \
    cd /workspace/new_open_mes_server; \
    mvn -B -DskipTests package

FROM eclipse-temurin:11-jre
RUN set -eux; \
    mkdir -p /metaxk-mes-server /metaxk-mes-server/logs
WORKDIR /metaxk-mes-server

COPY --from=builder /workspace/new_open_mes_server/metaxk-server/target/metaxk-server.jar /metaxk-mes-server/app.jar

ENV TZ=Asia/Shanghai \
    JAVA_OPTS="-Xms512m -Xmx512m -Djava.security.egd=file:/dev/./urandom" \
    ARGS=""

EXPOSE 48080
CMD ["sh", "-c", "java ${JAVA_OPTS} -jar /metaxk-mes-server/app.jar ${ARGS}"]
