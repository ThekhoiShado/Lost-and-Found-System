# 失物招领系统 (Lost & Found System)

> 一个基于 **Spring Boot 4.0 + Vue 3 + Thymeleaf** 的综合失物招领平台，支持用户发布失物/寻物信息、认领申请、留言互动，以及管理员后台审核与维护。

[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-4.0.6-brightgreen.svg)](https://spring.io/projects/spring-boot)
[![Vue](https://img.shields.io/badge/Vue-3.5-blue.svg)](https://vuejs.org/)
[![MyBatis-Plus](https://img.shields.io/badge/MyBatis--Plus-3.5.16-orange.svg)](https://baomidou.com/)
[![License](https://img.shields.io/badge/license-MIT-green.svg)](LICENSE)

---

## 📋 目录

- [项目简介](#项目简介)
- [技术栈](#技术栈)
- [功能特性](#功能特性)
- [系统架构](#系统架构)
- [项目结构](#项目结构)
- [快速开始](#快速开始)
- [API 文档](#api-文档)
- [数据库设计](#数据库设计)
- [部署指南](#部署指南)
- [常见问题](#常见问题)
- [贡献指南](#贡献指南)

---

## 项目简介

失物招领系统旨在为社区、校园、企业等场景提供一个便捷的失物招领信息发布与匹配平台。用户可发布捡到物品（失物招领）或丢失物品（寻物启事）的信息，其他用户可浏览信息、提交认领申请、在线留言互动。管理端提供完整的内容审核、认领审核、评论维护和用户管理功能。

**核心流程：**
```
用户发布信息 → 管理员审核通过 → 信息公开展示 → 失主提交认领 → 管理员审核认领 → 认领完成
```

---

## 技术栈

### 后端

| 技术 | 版本 | 说明 |
|------|------|------|
| Spring Boot | 4.0.6 | 核心框架 |
| MyBatis-Plus | 3.5.16 | ORM 持久层 |
| MySQL | 8.0+ | 关系型数据库 |
| Redis | 6.0+ | 缓存与验证码存储 |
| Thymeleaf | — | 管理端模板引擎 |
| JWT (jjwt) | 0.12.6 | 用户端无状态认证 |
| Lombok | — | 代码简化 |
| Hutool | 5.8.34 | Java 工具库 |
| HikariCP | — | 数据库连接池 |
| Maven | 3.6+ | 项目构建 |

### 前端（用户端）

| 技术 | 版本 | 说明 |
|------|------|------|
| Vue | 3.5.32 | 前端框架 |
| Vite | 8.0.8 | 构建工具 |
| Pinia | 2.3.1 | 状态管理 |
| Vue Router | 4.5.1 | 路由管理 |
| Axios | 1.8.4 | HTTP 客户端 |
| Element Plus | 2.10.2 | UI 组件库 |
| wangEditor | 5.1.23 | 富文本编辑器 |
| TypeScript | ~6.0.0 | 类型支持 |

### 管理端

| 技术 | 说明 |
|------|------|
| Thymeleaf | 服务端模板渲染 |
| CSS3 | 自定义管理端样式 |
| Session | 管理员登录状态 |

---

## 功能特性

### 🌐 用户端（Vue 3 SPA）

| 模块 | 功能 |
|------|------|
| **用户认证** | 用户名密码注册/登录、验证码发送（支持手机/邮箱） |
| **首页浏览** | 失物/寻物信息分页列表、类型筛选、分类筛选、关键词搜索 |
| **信息详情** | 富文本内容展示、发布者信息、浏览次数统计 |
| **发布信息** | 富文本编辑（wangEditor）、图片上传、分类选择、地点日期填写 |
| **编辑/删除** | 发布者编辑或删除自己的信息（逻辑删除） |
| **认领申请** | 填写认领说明、上传凭证图片、查看审核进度 |
| **评论互动** | 发表评论/回复（楼中楼）、点赞/取消点赞 |
| **个人中心** | 个人资料编辑、密码修改、我的发布、我的认领 |
| **路由守卫** | 未登录自动跳转登录页，登录后回跳原页面 |

### 🔧 管理端（Thymeleaf 页面）

| 模块 | 功能 |
|------|------|
| **管理员登录** | Session 认证，BCrypt 密码校验 |
| **控制台** | 系统概览 |
| **发布审核** | 审核失物/寻物信息（通过/驳回） |
| **认领审核** | 查看认领凭证、审核通过/拒绝并填写备注 |
| **评论维护** | 删除违规评论、置顶/取消置顶 |
| **用户管理** | 用户列表、启用/禁用账号 |
| **数据管理** | 失物列表、认领列表、评论列表的分页查询与搜索 |

### ⚡ 技术亮点

- **前后端分离**：Vue 3 SPA 通过 REST API 与后端交互，管理端使用 Thymeleaf 混合架构
- **双认证体系**：用户端 JWT 无状态认证 + 管理端 Session 认证
- **XSS 防护**：富文本内容入库前使用 `HtmlFilterUtil` 过滤危险标签
- **验证码机制**：Redis 存储验证码，5 分钟有效期，60 秒发送间隔
- **逻辑删除**：MyBatis-Plus 全局逻辑删除，数据可恢复
- **统一响应**：`Result<T>` 统一封装，包含 `code`、`msg`、`data`
- **全局异常处理**：`GlobalExceptionHandler` 统一拦截业务异常
- **文件上传**：图片上传至本地目录，支持富文本编辑器内嵌图片
- **楼中楼评论**：支持一级评论与二级回复，`parent_id` + `reply_to_user_id` 双层关联
- **评论点赞**：独立点赞记录表，支持点赞/取消点赞切换

---

## 系统架构

```
┌──────────────────────────────────────────────────────────────┐
│                        用户端 (Vue 3)                         │
│                    http://localhost:5173                      │
│  首页 │ 详情 │ 发布 │ 登录 │ 注册 │ 个人中心 │ 我的发布/认领   │
└──────────────────────┬───────────────────────────────────────┘
                       │ REST API (/api/*)
                       │ JWT Bearer Token
┌──────────────────────▼───────────────────────────────────────┐
│                   Spring Boot 后端 (:8080)                    │
│  ┌─────────────────────┐    ┌──────────────────────────┐     │
│  │   REST API 控制器    │    │   Thymeleaf 管理端控制器   │     │
│  │  Auth / Lost / Claim │    │  /admin/*  (Session 认证) │     │
│  │  Comment / User /    │    │  仪表盘 / 审核 / 管理     │     │
│  │  Upload              │    └──────────┬───────────────┘     │
│  └─────────┬────────────┘               │                     │
│            │                             │                     │
│  ┌─────────▼─────────────────────────────▼───────────────┐    │
│  │                    Service 业务层                        │    │
│  │  UserService │ LostItemService │ ClaimService           │    │
│  │  CommentService │ VerifyCodeService │ FileUploadService │    │
│  └─────────┬───────────────────────────────┬──────────────┘    │
│            │                               │                   │
│  ┌─────────▼──────────┐    ┌──────────────▼──────────────┐    │
│  │   MyBatis-Plus      │    │        Redis                 │    │
│  │   Mapper 数据访问    │    │   验证码 / 缓存 / Token      │    │
│  └─────────┬──────────┘    └─────────────────────────────┘    │
│            │                                                   │
└────────────┼───────────────────────────────────────────────────┘
             │
    ┌────────▼────────┐    ┌────────────────┐
    │   MySQL 8.0+    │    │   本地文件存储   │
    │   lost_found    │    │   ./uploads/    │
    └─────────────────┘    └────────────────┘
```

---

## 项目结构

```
lostfound/                               # 后端项目（Spring Boot）
├── src/main/java/com/lostfound/
│   ├── controller/
│   │   ├── admin/
│   │   │   ├── AdminController.java     # 管理端页面路由（Thymeleaf 视图）
│   │   │   └── AdminApiController.java  # 管理端 REST API
│   │   └── api/
│   │       ├── AuthController.java      # 用户认证 API
│   │       ├── ClaimController.java     # 认领申请 API
│   │       ├── CommentController.java   # 评论互动 API
│   │       ├── LostItemController.java  # 失物/寻物信息 API
│   │       ├── UploadController.java    # 文件上传 API
│   │       └── UserController.java      # 用户资料 API
│   ├── service/
│   │   ├── ClaimService.java            # 认领业务接口
│   │   ├── CommentService.java          # 评论业务接口
│   │   ├── FileUploadService.java       # 文件上传业务接口
│   │   ├── LostItemService.java         # 失物业务接口
│   │   ├── UserService.java             # 用户业务接口
│   │   ├── VerifyCodeService.java       # 验证码业务接口
│   │   └── impl/                        # 接口实现类
│   ├── mapper/                          # MyBatis-Plus Mapper
│   │   ├── UserMapper.java
│   │   ├── LostItemMapper.java
│   │   ├── ClaimMapper.java
│   │   ├── CommentMapper.java
│   │   └── CommentLikeMapper.java
│   ├── entity/                          # 数据库实体
│   │   ├── User.java
│   │   ├── LostItem.java
│   │   ├── Claim.java
│   │   ├── Comment.java
│   │   └── CommentLike.java
│   ├── dto/                             # 请求/响应 DTO
│   │   ├── LoginRequest.java / LoginResponse.java
│   │   ├── RegisterRequest.java
│   │   ├── SendCodeRequest.java
│   │   ├── PublishLostRequest.java
│   │   ├── ClaimRequest.java
│   │   └── CommentRequest.java
│   ├── config/                          # 配置类
│   │   ├── WebMvcConfig.java            # CORS + 静态资源映射
│   │   ├── MybatisPlusConfig.java       # 分页插件 + 逻辑删除
│   │   ├── RedisConfig.java             # Redis 序列化配置
│   │   └── MetaObjectHandlerConfig.java # 自动填充
│   ├── common/                          # 通用组件
│   │   ├── Result.java                  # 统一响应体
│   │   ├── BusinessException.java       # 业务异常
│   │   └── GlobalExceptionHandler.java  # 全局异常处理
│   ├── interceptor/
│   │   └── JwtInterceptor.java          # JWT 登录拦截器
│   ├── utils/
│   │   ├── JwtUtil.java                 # JWT 生成/解析工具
│   │   └── HtmlFilterUtil.java          # XSS 过滤工具
│   └── LostfoundApplication.java        # 启动类
├── src/main/resources/
│   ├── application.yml                  # 主配置文件
│   ├── db/schema.sql                    # 数据库建表脚本
│   ├── static/admin/css/
│   │   └── admin.css                    # 管理端样式
│   └── templates/admin/                 # Thymeleaf 管理端模板
│       ├── layout.html                  # 主布局框架
│       ├── login.html                   # 管理员登录
│       ├── dashboard.html               # 控制台首页
│       ├── lost/
│       │   ├── list.html                # 失物/寻物列表
│       │   └── audit.html               # 发布审核
│       ├── claim/
│       │   ├── list.html                # 认领申请列表
│       │   └── audit.html               # 认领审核
│       ├── comment/
│       │   └── list.html                # 评论维护
│       ├── user/
│       │   ├── list.html                # 用户列表
│       │   └── edit.html                # 用户编辑
│       └── fragments/
│           └── header.html              # 页面头部
└── pom.xml                              # Maven 依赖

lostfound-client/                        # 前端项目（Vue 3）
├── src/
│   ├── api/                             # API 接口封装
│   │   ├── auth.ts                      # 认证接口
│   │   ├── lost.ts                      # 失物/寻物接口
│   │   ├── claim.ts                     # 认领接口
│   │   ├── comment.ts                   # 评论接口
│   │   ├── user.ts                      # 用户接口
│   │   └── request.ts                   # Axios 实例 + 拦截器
│   ├── assets/styles/
│   │   └── global.css                   # 全局样式
│   ├── views/                           # 页面视图
│   │   ├── home/
│   │   │   └── HomeView.vue             # 首页
│   │   ├── detail/
│   │   │   └── DetailView.vue           # 信息详情
│   │   ├── publish/
│   │   │   └── PublishView.vue          # 发布/编辑信息
│   │   ├── user/
│   │   │   ├── LoginView.vue            # 登录
│   │   │   ├── RegisterView.vue         # 注册
│   │   │   ├── ProfileView.vue          # 个人中心
│   │   │   └── MyPostsView.vue          # 我的发布
│   │   └── claim/
│   │       └── MyClaimsView.vue         # 我的认领
│   ├── router/
│   │   └── index.ts                     # 路由配置 + 导航守卫
│   ├── store/modules/
│   │   └── user.ts                      # 用户状态管理 (Pinia)
│   ├── utils/
│   │   └── index.ts                     # 工具函数
│   ├── App.vue                          # 根组件
│   ├── main.ts                          # 入口文件
│   └── env.d.ts                         # 类型声明
├── index.html
├── vite.config.ts                       # Vite 配置
├── tsconfig.json                        # TypeScript 配置
└── package.json                         # NPM 依赖
```

---

## 快速开始

### 环境要求

| 软件 | 版本要求 | 说明 |
|------|----------|------|
| JDK | 17+ | 后端运行环境 |
| Node.js | 20.19+ 或 22.12+ | 前端构建环境 |
| MySQL | 8.0+ | 数据库 |
| Redis | 6.0+ | 缓存与验证码（可选，开发时可关闭） |
| Maven | 3.6+ | 后端构建 |

### 1. 克隆项目

```bash
git clone <仓库地址>
cd liuqing66
```

### 2. 初始化数据库

使用 MySQL 客户端执行建表脚本：

```bash
mysql -u root -p < lostfound/src/main/resources/db/schema.sql
```

> 脚本会自动创建 `lost_found` 数据库（utf8mb4 字符集），包含全部 6 张表和默认管理员账号。

**默认管理员账号：**
- 用户名：`admin`
- 密码：`admin123`

### 3. 配置后端

编辑 `lostfound/src/main/resources/application.yml`，修改数据库和 Redis 连接信息：

```yaml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/lost_found?useUnicode=true&characterEncoding=UTF-8&serverTimezone=Asia/Shanghai
    username: <你的数据库用户名>
    password: <你的数据库密码>
  data:
    redis:
      host: localhost
      port: 6379
      password:         # Redis 密码（无密码则留空）
```

### 4. 启动后端

```bash
cd lostfound
mvn spring-boot:run
```

后端服务运行在 **http://localhost:8080**

### 5. 启动前端（用户端）

```bash
cd lostfound-client
npm install
npm run dev
```

前端开发服务器运行在 **http://localhost:5173**

> Vite 已配置代理，开发环境下 `/api` 和 `/uploads` 请求自动转发到后端 `localhost:8080`。

### 6. 访问管理端

浏览器直接访问 **http://localhost:8080/admin/login**

使用默认管理员账号登录即可进入管理后台。

### 快速启动一览

| 服务 | 地址 | 说明 |
|------|------|------|
| 用户端 | http://localhost:5173 | Vue 3 SPA |
| 后端 API | http://localhost:8080/api | REST 接口 |
| 管理端 | http://localhost:8080/admin/login | Thymeleaf 页面 |

---

## API 文档

### 通用说明

- **基础路径**：`http://localhost:8080`
- **认证方式**：用户端在请求头携带 `Authorization: Bearer <token>`
- **响应格式**：统一 `Result<T>` 结构

```json
{
  "code": 200,
  "msg": "操作成功",
  "data": {}
}
```

| code | 含义 |
|------|------|
| 200 | 成功 |
| 400 | 参数错误 |
| 401 | 未登录 |
| 403 | 无权限 |
| 500 | 服务器错误 |

### 认证接口 `/api/auth`

| 方法 | 路径 | 认证 | 说明 |
|------|------|------|------|
| POST | `/api/auth/login` | 否 | 用户登录，返回 JWT token |
| POST | `/api/auth/register` | 否 | 用户注册（需验证码） |
| POST | `/api/auth/send-code` | 否 | 发送手机/邮箱验证码 |

**登录请求示例：**
```json
{
  "username": "user123",
  "password": "123456"
}
```

**登录响应示例：**
```json
{
  "code": 200,
  "msg": "登录成功",
  "data": {
    "token": "eyJhbGciOi...",
    "user": { "id": 1, "username": "user123", "nickname": "小明" }
  }
}
```

### 失物/寻物接口 `/api/lost`

| 方法 | 路径 | 认证 | 说明 |
|------|------|------|------|
| GET | `/api/lost/list` | 否 | 分页查询已发布列表 |
| GET | `/api/lost/{id}` | 否 | 获取详情（含浏览计数） |
| POST | `/api/lost/publish` | 是 | 发布失物/寻物信息 |
| PUT | `/api/lost/{id}` | 是 | 更新信息（仅发布者） |
| DELETE | `/api/lost/{id}` | 是 | 删除信息（逻辑删除） |
| GET | `/api/lost/my` | 是 | 我的发布列表 |

**列表查询参数：**

| 参数 | 类型 | 默认值 | 说明 |
|------|------|--------|------|
| current | int | 1 | 当前页码 |
| size | int | 10 | 每页条数 |
| type | int | — | 类型：1-失物招领，2-寻物启事 |
| category | string | — | 物品分类 |
| keyword | string | — | 标题/内容关键词搜索 |

### 认领接口 `/api/claim`

| 方法 | 路径 | 认证 | 说明 |
|------|------|------|------|
| POST | `/api/claim/add` | 是 | 提交认领申请 |
| GET | `/api/claim/my` | 是 | 我的认领申请列表 |
| GET | `/api/claim/{id}` | 否 | 认领详情 |

### 评论接口 `/api/comment`

| 方法 | 路径 | 认证 | 说明 |
|------|------|------|------|
| POST | `/api/comment/add` | 是 | 发表评论/回复 |
| GET | `/api/comment/list/{lostItemId}` | 否 | 获取评论树（含回复嵌套） |
| DELETE | `/api/comment/{id}` | 是 | 删除自己的评论 |
| POST | `/api/comment/like/{commentId}` | 是 | 点赞/取消点赞 |

### 用户接口 `/api/user`

| 方法 | 路径 | 认证 | 说明 |
|------|------|------|------|
| GET | `/api/user/profile` | 是 | 获取个人信息 |
| PUT | `/api/user/profile` | 是 | 更新个人资料 |
| PUT | `/api/user/password` | 是 | 修改密码 |

### 文件上传 `/api/upload`

| 方法 | 路径 | 认证 | 说明 |
|------|------|------|------|
| POST | `/api/upload/image` | 否 | 上传图片（multipart/form-data） |

### 管理端 API `/admin/api`

| 方法 | 路径 | 说明 |
|------|------|------|
| GET | `/admin/api/lost/list` | 分页查询失物/寻物 |
| PUT | `/admin/api/lost/audit/{id}` | 审核发布（通过/驳回） |
| DELETE | `/admin/api/lost/{id}` | 删除失物/寻物 |
| GET | `/admin/api/claim/list` | 分页查询认领 |
| GET | `/admin/api/claim/{id}` | 认领详情 |
| PUT | `/admin/api/claim/audit/{id}` | 审核认领（通过/拒绝+备注） |
| GET | `/admin/api/comment/list` | 分页查询评论 |
| DELETE | `/admin/api/comment/{id}` | 删除评论 |
| PUT | `/admin/api/comment/top/{id}` | 置顶/取消置顶 |
| GET | `/admin/api/user/list` | 分页查询用户 |
| PUT | `/admin/api/user/status/{id}` | 更新用户状态（启用/禁用） |

---

## 数据库设计

### E-R 概览

```
user ──1:N──→ lost_item ──1:N──→ claim
  │               │                  │
  │               └──1:N──→ comment ──1:N──→ comment_like
  │                              │
  └──────────────────────────────┘
```

### 核心表

| 表名 | 说明 | 关键字段 |
|------|------|----------|
| `user` | 用户表 | username, password(BCrypt), role(user/admin), status |
| `lost_item` | 失物/寻物信息表 | type(1失物/2寻物), status(0待审核/1已发布/2已认领/3已结束), content(富文本), category, cover_image |
| `claim` | 认领申请表 | status(0待审核/1通过/2拒绝), proof_images, audit_remark |
| `comment` | 评论表 | parent_id(楼中楼), reply_to_user_id, top(置顶), like_count |
| `verify_code` | 验证码表 | target(手机/邮箱), code, type, expire_time |
| `comment_like` | 点赞记录表 | comment_id + user_id 联合唯一索引 |

> 详见 `lostfound/src/main/resources/db/schema.sql`，包含完整 DDL 和注释。

---

## 部署指南

### 后端打包

```bash
cd lostfound
mvn clean package -DskipTests
java -jar target/lostfound-1.0.0.jar
```

### 前端构建

```bash
cd lostfound-client
npm run build
# 产出在 dist/ 目录
```

### Nginx 反向代理（推荐）

```nginx
server {
    listen 80;
    server_name lostfound.example.com;

    # 前端静态资源
    location / {
        root /var/www/lostfound-client/dist;
        try_files $uri $uri/ /index.html;
    }

    # 后端 API
    location /api/ {
        proxy_pass http://localhost:8080/api/;
        proxy_set_header Host $host;
        proxy_set_header X-Real-IP $remote_addr;
    }

    # 管理端
    location /admin/ {
        proxy_pass http://localhost:8080/admin/;
        proxy_set_header Host $host;
    }

    # 上传文件
    location /uploads/ {
        proxy_pass http://localhost:8080/uploads/;
    }
}
```

### 生产环境 Checklist

- [ ] 修改 `jwt.secret` 为复杂随机字符串
- [ ] 修改数据库密码为强密码
- [ ] 设置 Redis 密码（若生产使用）
- [ ] 修改默认管理员密码
- [ ] `upload.local-path` 设为绝对路径并确保可写
- [ ] 关闭 MyBatis SQL 日志输出（修改 `log-impl`）
- [ ] 配置 HTTPS 证书
- [ ] 使用独立 `application-prod.yml` 管理生产配置

---

## 常见问题

<details>
<summary><b>Q: 前端调用接口报 401 错误？</b></summary>

检查 Axios 拦截器是否正确携带了 `Authorization: Bearer <token>` 请求头，以及 Redis 中 token 是否已过期（默认 24 小时）。
</details>

<details>
<summary><b>Q: 富文本图片上传失败？</b></summary>

1. 确认 `upload.local-path` 目录存在且可写
2. 确认 `WebMvcConfig` 已配置 `/uploads/**` 静态资源映射
3. 开发环境下确认 Vite 代理配置了 `/uploads` 路径
</details>

<details>
<summary><b>Q: 跨域错误（CORS）？</b></summary>

开发环境已在 `WebMvcConfig` 中配置允许 `localhost:5173` 跨域。生产环境建议通过 Nginx 反向代理统一域名，避免跨域问题。
</details>

<details>
<summary><b>Q: 管理端登录后很快就退出？</b></summary>

调整 `application.yml` 中 `server.servlet.session.timeout` 值（默认 7200 秒 = 2 小时）。
</details>

<details>
<summary><b>Q: MyBatis-Plus 分页不生效？</b></summary>

确认 `MybatisPlusConfig` 中已注册 `PaginationInnerInterceptor` 分页插件。
</details>

<details>
<summary><b>Q: 验证码收不到？</b></summary>

开发环境下验证码会打印到后端控制台日志。确保 Redis 服务正常运行，且 `verify-code.send-interval` 未限制发送频率。
</details>

---

## 贡献指南

欢迎提交 Issue 和 Pull Request！

### 代码规范

- **后端**：遵循 Java 命名规范，Controller 层只做参数校验，业务逻辑在 Service 层
- **前端**：使用组合式 API（`<script setup>`），组件按 imports → props → refs → methods → lifecycle 顺序组织
- **管理端**：使用 Thymeleaf 标准属性，避免内联 Java 代码
- **提交信息**：遵循 [Conventional Commits](https://www.conventionalcommits.org/) 规范

### 提交流程

```bash
git checkout -b feature/xxx    # 新建功能分支
git commit -m "feat: 添加XXX功能"
git push origin feature/xxx
# 提交 Pull Request
```

---

## 许可证

本项目基于 [MIT](LICENSE) 许可证开源。

---

> **感谢使用失物招领系统！** 如有任何问题或建议，欢迎提交 Issue 或参与贡献。
