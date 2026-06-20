# CLAUDE.md

## 项目概述
失物招领系统（Lost & Found System）  
一个基于 Spring Boot + Vue3 + Thymeleaf 的失物招领平台，支持用户发布失物/寻物信息、认领申请、留言互动，以及管理员审核发布、审核认领、维护评论、管理用户等功能。富文本编辑器集成，Redis 用于缓存和验证码存储。

## 技术栈

### 后端
- Spring Boot 2.7+
- MyBatis-Plus 3.5+
- MySQL 8.0+
- Redis（验证码、登录态、热点缓存）
- Thymeleaf（管理端视图模板）

### 前端（用户端）
- Vue 3 + Vite
- Pinia（状态管理）
- Vue Router 4
- Axios
- Element Plus（UI 组件库）
- wangEditor（富文本编辑器）

### 管理端
- Thymeleaf 模板引擎
- Bootstrap / AdminLTE（推荐样式库）
- jQuery（DOM 操作）

## 项目结构

### 后端（Spring Boot）
src/main/java/com/lostfound/
├── controller/
│ ├── admin/ # 管理端控制器（返回 Thymeleaf 视图）
│ └── api/ # 用户端 REST API（供 Vue3 调用）
├── service/ # 业务逻辑层（接口 + impl）
├── mapper/ # MyBatis-Plus Mapper 层
├── entity/ # 数据库实体类
├── dto/ # 请求/响应数据传输对象
├── config/ # 配置类（跨域、Redis、MyBatis-Plus）
├── common/ # 统一响应、全局异常、工具类
├── interceptor/ # 登录/权限拦截器
└── utils/ # JWT、XSS 过滤等工具

text

### 前端（Vue3 客户端）
lostfound-client/
├── src/
│ ├── api/ # 接口请求封装（按模块：auth, lost, claim, comment）
│ ├── assets/ # 静态资源（样式、图片）
│ ├── components/ # 公共组件（富文本编辑器、评论列表、卡片等）
│ ├── views/ # 页面视图（首页、详情、发布、个人中心等）
│ ├── router/ # 路由配置 + 导航守卫
│ ├── store/ # Pinia 状态管理（用户、应用状态）
│ ├── hooks/ # 组合式函数（分页、表单等）
│ └── utils/ # 工具函数（token、日期、校验）


### 管理端（Thymeleaf 模板）
src/main/resources/templates/admin/
├── layout.html # 主布局（header + sidebar + content）
├── login.html # 管理员登录页
├── dashboard.html # 控制台首页
├── lost/
│ ├── list.html # 失物/寻物列表
│ └── audit.html # 发布审核页
├── claim/
│ ├── list.html # 认领申请列表
│ └── audit.html # 认领审核页
├── comment/
│ └── list.html # 评论维护列表
├── user/
│ ├── list.html # 用户列表
│ └── edit.html # 用户编辑页
└── fragments/ # 复用片段（header、sidebar、分页）

text

## 常用开发命令

### 后端（Maven）
```bash
# 编译打包
mvn clean package

# 运行（开发环境）
mvn spring-boot:run

# 跳过测试打包
mvn clean package -DskipTests
前端（Vue3）
bash
# 安装依赖
npm install

# 开发服务器（默认端口 5173）
npm run dev

# 生产构建
npm run build

# 预览构建结果
npm run preview
Redis 启动（本地开发）
bash
# Linux/macOS
redis-server

# Windows（需安装）
redis-server.exe
MySQL 数据库初始化
sql
-- 执行项目中的 schema.sql（位于 src/main/resources/db/）
CREATE DATABASE lost_found CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE lost_found;
SOURCE /path/to/schema.sql;
代码风格规范
后端
类名使用 大驼峰（PascalCase），方法/变量名使用 小驼峰（camelCase）。

常量全大写 + 下划线（如 MAX_RETRY_COUNT）。

Controller 层只做参数校验和调用 Service，不写业务逻辑。

Service 层需有接口和实现类，使用 @Service 注解。

所有 REST API 统一返回 Result<T> 结构，包含 code、msg、data。

数据库实体类使用 Lombok @Data，表名用 @TableName，主键用 @TableId(type = IdType.AUTO)。

日志使用 Slf4j，避免使用 System.out.println()。

前端
组件文件名使用 大驼峰（如 RichTextEditor.vue）。

组合式 API 优于选项式 API。

组件 <script setup> 中按 imports → props/emits → reactive refs → computed → methods → watch → lifecycle 顺序组织。

API 请求统一封装在 api/ 目录下，每个模块一个文件。

Pinia store 使用 defineStore，持久化插件可选 pinia-plugin-persistedstate。

路由懒加载：component: () => import('@/views/Home.vue')。

样式使用 scoped 避免污染，全局样式放在 assets/styles/global.css。

管理端（Thymeleaf）
HTML 标签缩进使用 2 或 4 空格，保持一致。

使用 th:each、th:if 等标准属性，避免内联 Java 代码。

片段文件放在 fragments/ 目录，通过 th:replace 或 th:insert 引用。

静态资源（CSS/JS）统一放在 src/main/resources/static/admin/，引用时使用 @{/admin/css/admin.css}。

环境要求
JDK 11+（推荐 11 或 17）

Node.js 16+（Vue3 开发）

MySQL 8.0+

Redis 6.0+（可选，开发时可关闭验证码功能）

Maven 3.6+

重要配置文件
后端 application.yml
yaml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/lost_found?useUnicode=true&characterEncoding=utf8&serverTimezone=Asia/Shanghai
    username: ${DB_USERNAME:root}
    password: ${DB_PASSWORD:}
  redis:
    host: localhost
    port: 6379
    password:
  thymeleaf:
    cache: false   # 开发关闭缓存
    prefix: classpath:/templates/
    suffix: .html
server:
  port: 8080
mybatis-plus:
  global-config:
    db-config:
      logic-delete-field: deleted   # 全局逻辑删除字段
前端 .env.development
text
VITE_API_BASE_URL = http://localhost:8080/api
VITE_UPLOAD_URL = http://localhost:8080/upload
注意事项
富文本 XSS 防护
后端必须在保存富文本内容时使用 HtmlFilterUtil 过滤危险标签（script、iframe 等）。

用户端和管理端分离
用户端使用 JWT 或 Redis session，管理端使用独立 Session（Spring Security 可结合使用）。

Redis 验证码
注册时验证码有效期 5 分钟，限制同一手机/邮箱 1 分钟内只能发送一次。

认领审核流程
管理员通过认领后，失物状态变为“已认领”；用户可在个人中心查看结果。

分页统一
用户端使用 MyBatis-Plus Page 对象，前端接收 { records, total, current, size }。

跨域配置
开发环境需在 Spring Boot 中配置 CORS 允许 http://localhost:5173。

部署
前端 npm run build 后将 dist 内容复制到后端 src/main/resources/static/ 下，或使用 Nginx 反向代理。

数据库表关键字段（供参考）
user：id, username, password, role(user/admin), status(0正常/1禁用), avatar, phone, email

lost_item：id, user_id, title, content, type(1失物/2寻物), status(0待审核/1已发布/2已认领/3已结束), contact

claim：id, lost_item_id, claim_user_id, claimant_name, claim_detail, status(0待审核/1通过/2拒绝)

comment：id, lost_item_id, user_id, content, parent_id, top(置顶标记), create_time

快速启动（开发环境）
启动 MySQL 和 Redis。

执行 schema.sql 初始化数据库。

后端：mvn spring-boot:run（默认端口 8080）。

前端：cd lostfound-client && npm install && npm run dev（默认端口 5173）。

管理端访问：http://localhost:8080/admin/login（默认管理员账号可在 data.sql 中预置）。

用户端访问：http://localhost:5173。

贡献指南
新建功能分支：git checkout -b feature/xxx

提交前运行测试：mvn test（后端）和 npm run test（前端，若有）

提交信息遵循 Conventional Commits 规范（如 feat: 添加认领申请功能）

合并前需至少一位 reviewer 批准

常见问题
Q：后端接口 401 错误？
A：检查前端 Axios 拦截器是否携带了 Authorization: Bearer <token>，或 Redis 中 token 已过期。

Q：富文本图片上传失败？
A：确认后端配置了静态资源映射（/upload/** 映射到本地目录），且前端 UPLOAD_URL 正确。

Q：Thymeleaf 页面无法加载静态资源？
A：检查 application.yml 中 spring.mvc.static-path-pattern 和资源目录位置，推荐使用 @{/admin/css/...} 方式引用。

Q：MyBatis-Plus 分页不生效？
A：确保配置了 MybatisPlusInterceptor 并添加 PaginationInnerInterceptor。