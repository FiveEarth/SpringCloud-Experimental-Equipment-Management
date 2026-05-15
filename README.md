实验室设备一体化管理系统（Spring Cloud + Vue）

## 项目概述
这是一个本科毕业设计。
面向高校实验室场景的设备管理平台，基于 Spring Cloud 微服务与 Vue 3 SPA。覆盖设备台账、预约、申领/归还审批、维修、报废与实验室资源管理，并提供角色权限控制（管理员/教师/学生/维修）。

## 架构说明
- Spring Cloud Gateway：统一入口与路由
- Nacos：服务注册与配置中心
- Sentinel：流量防护（可选 Dashboard）
- OpenFeign：服务间调用
- MyBatis + MySQL：数据持久化
- Redis：缓存与加速（在 `service-device` 中配置）

## 模块划分
### 后端（Maven 多模块）
- `gateway`（默认 8888）：API 网关与路由
- `services`（业务服务）
  - `service-user`（8001）
  - `service-apply`（8002）
  - `service-maintain`（8003）
  - `service-lab`（8004）
  - `service-device`（8005）
  - `service-scrap`（8006）
  - `service-reserve`（8007）
- `model`：公共实体与依赖

### 前端
- `device-manage`：Vue 3 + Element Plus（开发端口默认 8081）

## 网关路由
配置文件：`gateway/src/main/resources/application-route.yml`
- `/api/user/**` -> `service-user`
- `/api/apply/**` -> `service-apply`
- `/api/maintain/**` -> `service-maintain`
- `/api/lab/**` -> `service-lab`
- `/api/device/**` -> `service-device`
- `/api/scrap/**` -> `service-scrap`
- `/api/reserve/**` -> `service-reserve`

## 环境要求
- JDK 17
- Maven 3.8+
- Node.js 16+
- MySQL 5.7+（默认库名 `gra`）
- Redis 6+
- Nacos 2.x（注册/配置中心）
- Sentinel Dashboard（可选）

## 配置位置
- 服务配置：`services/*/src/main/resources/application.yml`
- 网关配置：`gateway/src/main/resources/application.yml`
- 前端代理：`device-manage/vue.config.js`（或 `VUE_APP_API_TARGET`）
- 数据库脚本：`gra.sql`

## 数据库初始化
导入 `gra.sql` 创建表结构与基础数据。

## 启动顺序（建议）
1) 启动 MySQL、Redis、Nacos
2)（可选）启动 Sentinel Dashboard
3) 启动各业务服务
4) 启动网关
5) 启动前端

## 常用命令
后端（示例：启动用户服务）：
```bash
mvn -pl services/service-user -am spring-boot:run
```

网关：
```bash
mvn -pl gateway -am spring-boot:run
```

Sentinel 控制台：
```bash
java -jar sentinel-dashboard-1.8.8.jar
```

前端：
```bash
cd device-manage
npm install
npm run serve
```

## 备注
- 各服务配置中的 Nacos/数据库地址为示例，需根据环境调整。
- 项目定位为毕业设计/演示场景，生产级约束（如预约冲突、库存预留、幂等控制）可进一步完善。

## 许可证
All will be allowing.

## 致谢
Spring Cloud、Nacos、Sentinel、Vue、Element Plus。
