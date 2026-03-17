# Startup Guide

## Backend (Spring Cloud)

1. **Nacos**: Ensure Nacos is running (e.g. `47.104.250.164:8848` or local).

2. **Gateway**:
   - Main: `edu.graduation.gateway.GatewayMainApplication`
   - Run from `gateway` module.
   - **开发环境**：使用 profile `dev` 时端口为 **8888**（无需管理员权限），例如：`--spring.profiles.active=dev`。前端代理默认指向 `http://localhost:8888`。
   - 生产/默认：端口 80（需管理员权限时可将前端 `vue.config.js` 的 `VUE_APP_API_TARGET` 改为 `http://localhost:80`）。

3. **Microservices** (each has a main class; ports below):

   | Service        | Main Class                                    | Port |
   |----------------|------------------------------------------------|------|
   | service-user   | edu.graduation.user.UserMainApplication        | 8001 |
   | service-apply  | edu.graduation.apply.ApplyMainApplication      | 8002 |
   | service-maintain | edu.graduation.maintain.MaintainMainApplication | 8003 |
   | service-lab    | edu.graduation.lab.LabMainApplication          | 8004 |
   | service-reserve| edu.graduation.reserve.ReserveMainApplication  | 8005 |
   | service-scrap  | edu.graduation.scrap.ScrapMainApplication      | 8006 |
   | service-device | edu.graduation.device.DeviceMainApplication    | 9000 |

4. **Database**: MySQL schema `gra`, see `src/main/gra.sql`. Configure in each service's `application.yml` if needed.

## Frontend (Vue)

- Path: `src/main/device-manage`
- Install: `npm install`
- Dev: `npm run serve` (default port 8080; proxy `/api` to gateway `http://localhost:8888`；需先启动网关并加 `--spring.profiles.active=dev`)
- Build: `npm run build`

## Flow

- Browser -> `http://localhost:8080` (frontend) -> requests to `/api/*` -> proxy to Gateway (默认 8888，见上) -> Gateway 路由到对应微服务 (lb://service-xxx)。
