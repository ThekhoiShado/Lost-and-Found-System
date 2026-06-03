# 失物招领系统 (Lost & Found System)

## 项目简介

失物招领系统是一个基于 **Spring Boot + Vue3 + Thymeleaf** 的综合性平台，旨在帮助用户快速发布失物/寻物信息、提交认领申请、在线留言互动，并支持管理员对发布内容、认领申请、评论和用户进行高效审核与管理。系统集成富文本编辑器、Redis 缓存及验证码、JWT 身份认证等功能，提供流畅的前后端分离体验。

## 技术栈

### 后端
- **核心框架**：Spring Boot 2.7+
- **持久层**：MyBatis-Plus 3.5+
- **数据库**：MySQL 8.0+
- **缓存/验证码**：Redis 6.0+
- **模板引擎**：Thymeleaf (管理端)
- **认证**：JWT (用户端) + Session (管理端)
- **工具**：Lombok、HikariCP、Jackson

### 前端（用户端）
- **框架**：Vue 3 + Vite
- **状态管理**：Pinia
- **路由**：Vue Router 4
- **HTTP 客户端**：Axios
- **UI 库**：Element Plus
- **富文本编辑器**：wangEditor

### 管理端
- Thymeleaf + Bootstrap 5
- jQuery + DataTables（可选）

## 功能特性

### 用户端
- 用户注册 / 登录（支持手机/邮箱验证码）
- 发布失物 / 寻物信息（富文本编辑、图片上传）
- 查看失物 / 寻物列表（分页、筛选、关键词搜索）
- 信息详情页（展示富文本内容、联系方式、留言板）
- 提交认领申请（填写认领说明、联系方式）
- 我的发布（管理自己发布的信息）
- 我的认领申请（查看审核进度）
- 个人资料编辑（头像、密码、联系方式）
- 留言互动（发表评论、回复他人、点赞）

### 管理端（Thymeleaf）
- 管理员登录（独立 Session）
- 控制台仪表盘（数据统计、待办事项）
- 失物/寻物发布审核（通过 / 驳回）
- 认领申请审核（查看凭证、通过 / 拒绝）
- 评论维护（删除违规评论、置顶）
- 用户管理（禁用 / 启用、重置密码、角色修改）

### 技术亮点
- 富文本内容 XSS 安全过滤
- JWT 无状态认证（用户端）
- Redis 缓存热点数据（首页列表、验证码）
- MyBatis-Plus 逻辑删除 + 分页查询
- 全局异常处理与统一响应封装
- 前后端分离 + 管理端模板引擎混合架构

## 系统架构
┌─────────────────┐ ┌─────────────────────────────────┐
│ Vue3 用户端 │────▶│ Spring Boot 后端 │
│ (localhost:5173)│ │ ┌────────────┐ ┌──────────┐ │
└─────────────────┘ │ │ REST API │ │ Thymeleaf│◀─┼─ 管理端访问
│ └─────┬──────┘ │ 管理端 │ │ (浏览器)
│ │ └──────────┘ │
│ ┌─────▼──────┐ │
│ │ Service层 │ │
│ └─────┬──────┘ │
│ ┌─────▼──────┐ ┌──────────┐ │
│ │ Mapper │────│ MySQL │ │
│ └────────────┘ └──────────┘ │
│ ┌────────────┐ ┌──────────┐ │
│ │ Redis │ │ 文件存储 │ │
│ └────────────┘ └──────────┘ │
└─────────────────────────────────┘

text

## 快速开始

### 环境要求
- JDK 11 或 17
- Node.js 16+
- MySQL 8.0+
- Redis 6.0+
- Maven 3.6+

### 1. 克隆项目
```bash
git clone https://github.com/your-repo/lost-found-system.git
cd lost-found-system
2. 初始化数据库
创建数据库 lost_found（字符集 utf8mb4）

执行项目中的 schema.sql 脚本（位于 src/main/resources/db/）

bash
mysql -u root -p < src/main/resources/db/schema.sql
3. 配置后端
修改 src/main/resources/application.yml 中的数据库、Redis 等连接信息。

yaml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/lost_found?...
    username: root
    password: your_password
  data:
    redis:
      host: localhost
      port: 6379
4. 启动后端
bash
mvn clean install
mvn spring-boot:run
后端运行在 http://localhost:8080

5. 启动前端（用户端）
bash
cd lostfound-client   # 前端项目目录
npm install
npm run dev
前端运行在 http://localhost:5173

6. 访问管理端
打开浏览器访问 http://localhost:8080/admin/login

默认管理员账号：admin

默认密码：admin123（请在生产环境修改）

项目结构
text
lost-found-system/
├── src/
│   ├── main/
│   │   ├── java/com/lostfound/       # 后端源码
│   │   │   ├── controller/           # 控制器（api + admin）
│   │   │   ├── service/              # 业务逻辑
│   │   │   ├── mapper/               # MyBatis-Plus Mapper
│   │   │   ├── entity/               # 实体类
│   │   │   ├── dto/                  # 数据传输对象
│   │   │   ├── config/               # 配置类
│   │   │   ├── common/               # 通用组件
│   │   │   └── utils/                # 工具类
│   │   └── resources/
│   │       ├── application.yml       # 配置文件
│   │       ├── db/schema.sql         # 数据库脚本
│   │       ├── mapper/*.xml          # MyBatis XML映射
│   │       └── templates/admin/      # Thymeleaf管理端模板
│   └── test/                         # 单元测试
├── lostfound-client/                  # Vue3 前端项目
│   ├── src/
│   │   ├── api/                      # API接口封装
│   │   ├── assets/                   # 静态资源
│   │   ├── components/               # 公共组件
│   │   ├── views/                    # 页面视图
│   │   ├── router/                   # 路由配置
│   │   ├── store/                    # Pinia状态管理
│   │   └── utils/                    # 工具函数
│   ├── package.json
│   └── vite.config.js
└── pom.xml
数据库设计简要
核心表结构（详见 schema.sql）：

user：用户（包含角色、状态）

lost_item：失物/寻物信息（类型、状态、富文本内容）

claim：认领申请（关联失物、申请人、审核状态）

comment：评论/留言（支持楼中楼、置顶）

API 文档
认证接口
方法	路径	说明
POST	/api/auth/login	用户登录（返回 JWT token）
POST	/api/auth/register	用户注册（需验证码）
POST	/api/auth/send-code	发送验证码（手机/邮箱）
失物/寻物接口
方法	路径	说明
GET	/api/lost/list	分页获取失物/寻物列表
GET	/api/lost/{id}	获取详情及留言
POST	/api/lost/publish	发布失物/寻物（需认证）
PUT	/api/lost/{id}	更新信息（仅发布者）
DELETE	/api/lost/{id}	删除信息（逻辑删除）
认领接口
方法	路径	说明
POST	/api/claim/add	提交认领申请（需认证）
GET	/api/claim/my	当前用户的认领申请列表
GET	/api/claim/{id}	申请详情
评论接口
方法	路径	说明
POST	/api/comment/add	发表评论/回复
GET	/api/comment/list/{lostItemId}	获取某失物的评论树
DELETE	/api/comment/{id}	删除自己的评论
所有 /api/* 请求除登录/注册外，均需在 Header 中携带 Authorization: Bearer <token>

部署指南
生产环境部署（推荐）
后端打包

bash
mvn clean package -DskipTests
java -jar target/lostfound-1.0.0.jar --spring.profiles.active=prod
前端构建

bash
cd lostfound-client
npm run build
# 将 dist/ 目录内容放置到 Nginx 或后端 static/ 目录
Nginx 反向代理（前后端分离）

nginx
server {
    listen 80;
    server_name lostfound.example.com;
    
    location / {
        root /var/www/lostfound-client/dist;
        try_files $uri $uri/ /index.html;
    }
    
    location /api/ {
        proxy_pass http://localhost:8080/api/;
    }
    
    location /admin/ {
        proxy_pass http://localhost:8080/admin/;
    }
}
修改生产配置

使用独立 MySQL/Redis 实例

配置 HTTPS 证书

设置 jwt.secret 为复杂密钥

修改 upload.local-path 为绝对路径并配置静态资源映射

常见问题
Q: 前端调用接口出现跨域错误？
A: 开发环境已在 application.yml 配置了允许 localhost:5173，生产环境请通过 Nginx 代理解决。

Q: 富文本图片无法上传？
A: 检查后端的 upload.local-path 目录是否存在且可写，并确认 WebMvcConfig 中已将 /uploads/** 映射到本地目录。

Q: 管理员登录后 session 很快过期？
A: 调整 server.servlet.session.timeout 值（单位秒），或在登录时设置 remember-me。

贡献指南
欢迎提交 Issue 和 Pull Request。代码提交前请确保：

后端代码通过 mvn clean test

前端代码遵循 ESLint 规则

提交信息遵循 Conventional Commits 规范（如 feat: 添加..., fix: 修复...）

许可证
本项目基于 MIT 许可证开源。

联系方式
项目维护者：你的名字
邮箱：your.email@example.com
GitHub：项目地址

感谢使用失物招领系统！ 如果你有任何建议或问题，请随时联系我们。