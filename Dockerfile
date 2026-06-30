# ============================================================
# Dockerfile - Spring Boot 菜品管理系统
# 基础镜像：Eclipse Temurin JDK 21（项目使用了 --enable-preview）
# ============================================================

# 第一阶段：构建阶段
FROM eclipse-temurin:21-jdk AS builder
WORKDIR /app
# 复制 Maven 配置文件，利用 Docker 缓存加速依赖下载
COPY pom.xml .
RUN --mount=type=cache,target=/root/.m2 mvn dependency:go-offline -B
# 复制源代码并打包
COPY src ./src
RUN mvn package -DskipTests -B

# 第二阶段：运行阶段
FROM eclipse-temurin:21-jre
WORKDIR /app
# 从构建阶段复制 JAR 包
COPY --from=builder /app/target/*.jar app.jar
# 创建图片存储目录
RUN mkdir -p /app/images
# 暴露服务端口
EXPOSE 8080
# 启动应用
ENTRYPOINT ["java", "--enable-preview", "-jar", "app.jar"]
