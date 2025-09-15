# GameVault Store Service

微服务项目的一部分，负责游戏商店相关功能（游戏浏览、购物车、订单）。

## 技术栈
- Java 17
- Spring Boot 3.3
- Maven
- PostgreSQL 16
- Docker + Docker Compose

## 功能进度
- [x] 游戏实体类与数据库映射
- [x] 基础 CRUD 接口
- [ ] 购物车与结算
- [ ] 用户钱包对接
- [ ] 集成测试

## 本地运行
```bash
# 启动 PostgreSQL（docker-compose.yml 提供）
docker compose up -d

# 启动服务
mvn spring-boot:run
