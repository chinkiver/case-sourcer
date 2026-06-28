# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## 项目概览

律所账户台账系统（`律所账户台账系统`）。三个子项目：

- `case-server` — Spring Boot 3.5 后端 API（JDK 17 + MyBatis-Plus + Spring Security + JWT + MySQL 8）
- `case-web` — Vue 3 + TS 管理台（Element Plus + Pinia + Vite）
- `case-mp` — UniApp 微信小程序（律师端）

环境要求：JDK 17+、Maven 3.9+、Node.js 18+（web 推荐 20.19+ / 22.12+，见 `case-web/package.json` 的 `engines`）、MySQL 8.0。Redis 已在配置中但当前不强制使用。MySQL 用户/库默认 `root` / `case_ledger`，可在 `case-server/src/main/resources/application.yml` 修改。

## 常用命令

### case-server（后端）

在 `case-server/` 目录下：

```bash
./mvnw package -DskipTests                # 打包为可执行 jar
./mvnw test                               # 运行测试（目前仅一个 SpringBootTest contextLoads）
java -jar target/case-server-0.0.1-SNAPSHOT.jar   # 启动
```

启动时会执行 `src/main/resources/init.sql` 建表，`demo-data.sql`（可选）写入示例数据。默认管理员账号 `admin` / `admin123`。生产部署需将 `spring.sql.init.mode` 设为 `never` 避免重复初始化（README 中的提示，但当前 `application.yml` 未显式配置 init mode；如需改动按字段添加）。

数据库变更：写新 SQL 到 `src/main/resources/changes.sql`（沿用当前约定）。中间表缺失的审计字段由现有 `changes.sql` 补齐。

### case-web（管理台）

在 `case-web/` 目录下：

```bash
npm install
npm run dev          # vite 开发服，baseURL 默认 http://localhost:8080
npm run build        # vue-tsc 类型检查 + vite 打包
npm run type-check   # 仅类型检查
npm run lint         # oxlint + eslint --fix
npm run format       # prettier
```

后端地址通过 `VITE_API_BASE_URL` 覆盖（见 `src/utils/request.ts`）。运行单测脚本未配置。

### case-mp（小程序）

在 `case-mp/` 目录下：

```bash
npm install
npm run dev:mp-weixin   # 编译为微信小程序
npm run type-check
```

将 `dist/build/mp-weixin` 用微信开发者工具导入。`src/utils/request.ts` 中 `BASE_URL` 当前硬编码为 `http://localhost:8080`，真机调试需要改为局域网 IP。

## 架构

### 后端分层（`case-server/src/main/java/com/lawfirm/caseledger`）

- `controller/` — REST 端点，路径前缀 `/api/...`，每个写操作都用 `@PreAuthorize("hasAuthority('xxx:yyy')")` 做权限校验。例：`ProjectController` 对应 `project:add` / `project:edit` / `project:list`。
- `service/` — 业务逻辑。除 `AuthService`（接口 + `impl/AuthServiceImpl`）外，其余 Service 直接以具体类承载，没有 interface/impl 拆分。新增业务时直接写 `@Service class` 即可。
- `mapper/` — MyBatis-Plus BaseMapper 接口。**项目当前没有 XML mapper 文件**（`resources/mapper/` 为空），所有 SQL 走 MP 的 QueryWrapper / LambdaQuery。复杂 SQL 需要新增时在 `resources/mapper/` 加 XML 并在 `application.yml` 已配置的 `classpath*:/mapper/**/*.xml` 下生效。
- `entity/` — 持久化对象，全部继承 `BaseEntity`（`id` 自增、`deleted` 逻辑删除、`create_time`/`update_time` 由 `MybatisPlusConfig#metaObjectHandler` 自动填充）。
- `dto/` + `common/Result.java` — API 返回统一包装 `{code, message, data, timestamp}`，成功 `code=200`，业务异常通过 `common/BusinessException` 抛出，由 `GlobalExceptionHandler` 统一转为 `Result.error`。
- `security/` — `SecurityConfig`（无状态 + JWT 过滤器 + CORS 全放开）、`JwtAuthenticationFilter`（解析 `Authorization: Bearer ...`）、`JwtUtil`（HS256）、`SecurityUser`（`UserDetails` 实现，承载 userId/realName/avatar）、`UserDetailsServiceImpl`（按用户名加载用户与权限码）。
- `common/` — `Result`、`PageResult`、`BusinessException`、`GlobalExceptionHandler`。

权限模型基于权限码（`sys_permission.permission_code`），不是 RBAC 角色硬编码。前端用 `userStore.hasPermission(code)`，后端用 `@PreAuthorize`。管理端默认账号内置角色 `admin`（在 `UserDetailsServiceImpl` 中处理）。

### 核心业务流

1. **录入收入**（`IncomeService.createIncome`）：插入 `project_income` → 更新 `project.received_amount` + `receive_ratio` → 同时生成若干条 `account_transaction`（流水），按律师分别记账。
2. **账户流水类型**（`AccountTransaction.txnType`）：
   - `项目收款`（正）— 原始回款
   - `账户增加`（正）— 提成，`hostAmount * commissionRate`（`Lawyer.commissionRate`，缺省 `0.85`，对应 `application.yml` 的 `ledger.default-commission-rate`）
   - `暂扣档案费`（负）— `Project.archiveFee`
   - `社保公积金扣款`、`个税扣缴` — 由对应模块录入
   - `个人报销`、`工资发放`、`其他支出` — 支出类
3. **账户看板**（`LedgerService`）：按 `lawyer_id` 聚合 `account_transaction`，输出 `AccountSummaryDto`（收入/支出/当前余额/可提现余额 = `currentBalance - pendingArchiveFee`）。**普通律师只能查自己的账户**（`resolveLawyerId` 根据 `system` / `finance` 角色判断是否可越权查询）。
4. **项目编号**：`ProjectService.generateProjectNo` 自动生成 `MS` + `yyyyMMdd` + 三位流水号。

### 前端结构（`case-web/src`）

- `api/` — 按后端 controller 一对一拆分的 TS 客户端（如 `project.ts`、`ledger.ts`）。
- `utils/request.ts` — 全局 axios 实例：自动注入 Bearer token，401 时清 token 并跳 `/login`，响应拦截器已 `return res.data`（调用方直接拿到 data，无需再 `.data`）。
- `stores/user.ts` — Pinia store，token 存 `js-cookie`（`expires: 1` 天），`userInfo` 含 `permissions` 与 `roles`。`hasPermission(code)` 对 `admin` 角色直接放行。
- `router/index.ts` — 路由守卫：`to.meta.public` 放行（如 `/login`），否则必须有 token 且 `userInfo` 已加载，否则先 `fetchUserInfo()`。
- `layout/MainLayout.vue` + `views/*` — 按业务域组织：`project`、`ledger`、`archive`、`social`、`tax`、`expense`、`import`、`system`。每个视图自带的 `*Dialog.vue` 用于表单。
- `@` 别名指向 `src/`（`vite.config.ts`）。

### 小程序结构（`case-mp/src`）

只有两个页面：`pages/login/login`、`pages/index/index`（账户看板，含余额/暂扣档案费/最近流水）。请求层 `utils/request.ts` 包装 `uni.request`，token 存 `uni.setStorageSync('token')`，401 时清理并 `reLaunch` 回登录。`pages.json` 维护路由。

### 资源 / 配置

- `case-server/src/main/resources/application.yml` — 数据源、Redis、JWT 密钥与有效期（24h）、`ledger.default-commission-rate`、MyBatis-Plus 全局配置（`logic-delete-field: deleted`、`map-underscore-to-camel-case: true`）。
- `case-server/src/main/resources/init.sql` — 建表（sys_user / sys_role / sys_permission / sys_role_permission / sys_user_role / client / lawyer / project / project_income / account_transaction / expense / archive_fee / social_insurance_config / social_insurance_record / tax_record / sys_config）。
- `case-server/src/main/resources/demo-data.sql` — 演示用律师/项目/收入/支出/社保/个税数据。
- `doc/` — 需求参考：`律所2024年个人账户台账V1.0-0223 空白.xlsm` 是原始 Excel 台账模板，新增字段时请对齐它。

## 约定

- 新增 controller 端点时，权限码命名沿用 `<资源>:<动作>`（`project:add`、`income:add`、`ledger:view`），并在 `sys_permission` 表中插入对应记录。
- 新增实体继承 `BaseEntity` 即可获得 id / deleted / 时间戳字段填充。
- 金额用 `BigDecimal`，统一 `setScale(2, RoundingMode.HALF_UP)`。
- 不写 service 接口；写 `@Service` 具体类直接 `@RequiredArgsConstructor` 注入。
- 前端 API 调用方不需要解包 `.data`，因为 `request.ts` 已解开。
- 跨子项目改动时同时更新 `README.md` 与本文件。