# 律所账户台账系统

## 技术栈

- 后端：Spring Boot 3.5 + JDK17 + MyBatis-Plus + Spring Security + JWT + MySQL 8
- 管理台/律师平台：Vue 3 + TypeScript + Element Plus + Pinia
- 移动端：UniApp + Vue3 + 微信小程序

## 环境要求

- JDK 17+
- Maven 3.9+
- Node.js 18+
- MySQL 8.0
- Redis 7.0+（可选，当前未强制使用）

## 数据库准备

1. 安装并启动 MySQL 8
2. 创建数据库和用户：

```sql
CREATE DATABASE IF NOT EXISTS case_ledger DEFAULT CHARSET utf8mb4 COLLATE utf8mb4_unicode_ci;
CREATE USER IF NOT EXISTS 'root'@'%' IDENTIFIED BY 'root';
GRANT ALL PRIVILEGES ON case_ledger.* TO 'root'@'%';
FLUSH PRIVILEGES;
```

3. 如需修改连接信息，编辑 `case-server/src/main/resources/application.yml`

## 启动方式

### 后端

```bash
cd case-server
./mvnw package -DskipTests
java -jar target/case-server-0.0.1-SNAPSHOT.jar
```

启动后会自动执行 `schema.sql` 建表和 `data.sql` 初始化基础数据。

默认管理员账号：`admin` / `admin123`

### Web 管理台

```bash
cd case-web
npm install
npm run dev
```

### 微信小程序

```bash
cd case-mp
npm install
npm run dev:mp-weixin
```

然后用微信开发者工具导入 `case-mp/dist/build/mp-weixin`。

## 核心功能

- 项目管理：案件录入、编辑、查询、归档
- 收入登记：按项目登记收入，自动计算回款比例
- 个人账户：自动计算律师提成、暂扣档案费、账户余额
- 权限控制：管理员、财务、行政、律师多角色
- 微信小程序：账户看板、最近流水

## 生产部署

1. 修改 `application.yml` 中的 MySQL、Redis、JWT 密钥等配置
2. 首次部署后，将 `spring.sql.init.mode` 改为 `never`，避免重复初始化数据
3. 使用 Nginx 反向代理前端静态资源和后端 API
