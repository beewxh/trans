# 交易管理系统

这是一个简单的交易管理系统，用于记录和管理金融交易。

[English](README.md) | [中文](README(chinese).md)

## 技术栈

- Java 21
- Spring Boot 3.2.3
- Maven

## 外部库依赖

项目使用了以下主要的外部库：

### 1. Spring Boot 相关库

#### spring-boot-starter-web (3.2.3)
- 用途：提供Web应用开发支持
- 主要功能：
  - 内嵌Tomcat服务器
  - Spring MVC框架
  - RESTful API支持
  - JSON序列化/反序列化
  - 基本的Web应用配置

#### spring-boot-starter-validation (3.2.3)
- 用途：提供参数验证支持
- 主要功能：
  - JSR-380 (Bean Validation 2.0) 实现
  - 注解式参数验证
  - 自定义验证器支持
  - 验证信息国际化

### 2. 开发工具库

#### Lombok
- 用途：通过注解简化Java代码
- 主要功能：
  - `@Data`：自动生成getter/setter/toString等方法
  - `@Slf4j`：自动创建日志对象
  - `@Builder`：实现建造者模式
  - `@AllArgsConstructor`/`@NoArgsConstructor`：自动生成构造函数

### 3. 测试相关库

#### spring-boot-starter-test
- 用途：提供测试支持
- 主要功能：
  - JUnit 5测试框架
  - Mockito模拟测试
  - AssertJ断言库
  - Spring Test测试工具

## 版本要求

- Java 21 或更高版本
- Maven 3.6 或更高版本
- Docker 20.10 或更高版本（如果使用Docker部署）

## 构建和运行

### 方式一：直接运行

1. 构建项目：
```bash
mvn clean package
```

2. 运行应用：
```bash
java -jar target/trans-0.0.1-SNAPSHOT.jar
```

### 方式二：使用Docker

1. 构建Docker镜像：
```bash
docker build -t trans-app .
```

2. 运行Docker容器：
```bash
docker run -d -p 8080:8080 --name trans-app trans-app
```

3. 查看容器日志：
```bash
docker logs -f trans-app
```

4. 停止和删除容器：
```bash
docker stop trans-app
docker rm trans-app
``` 