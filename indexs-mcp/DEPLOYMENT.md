# 远程MCP服务部署指南

## 前置条件

1. 已安装 Docker 和 Docker Compose
2. 拥有一个可访问的云服务器
3. 已配置好反向代理（可选，推荐使用 Nginx 或 Traefik）

## 部署方式

### 方式一：直接使用 Docker 运行

1. **克隆项目到服务器**
   ```bash
   git clone https://github.com/mlin4020/indexs-mcp.git
   cd indexs-mcp
   ```

2. **修改配置文件**
   编辑 `config.json` 文件，将 `api_base_url` 改为实际的指标平台接口地址：
   ```json
   {
     "api_base_url": "https://your-metrics-api.com",
     "timeout": 30,
     "headers": {
       "Content-Type": "application/json"
     }
   }
   ```

3. **构建并运行 Docker 容器**
   ```bash
   docker build -t indexs-mcp .
   docker run -d --name indexs-mcp -p 3000:3000 --restart unless-stopped indexs-mcp
   ```

### 方式二：使用 Docker Compose

1. **创建 docker-compose.yml 文件**
   ```yaml
   version: '3.8'
   services:
     indexs-mcp:
       build: .
       container_name: indexs-mcp
       restart: unless-stopped
       ports:
         - "3000:3000"
       environment:
         - NODE_ENV=production
       volumes:
         - ./config.json:/app/config.json
         - ./tools:/app/tools
   ```

2. **启动服务**
   ```bash
   docker-compose up -d
   ```

## 反向代理配置

### 使用 Nginx

在 Nginx 配置文件中添加以下内容：

```nginx
server {
    listen 80;
    server_name your-mcp-domain.com;

    location / {
        proxy_pass http://localhost:3000;
        proxy_http_version 1.1;
        proxy_set_header Upgrade $http_upgrade;
        proxy_set_header Connection 'upgrade';
        proxy_set_header Host $host;
        proxy_cache_bypass $http_upgrade;
    }
}
```

### 使用 HTTPS（推荐）

使用 Certbot 获取免费的 SSL 证书：

```bash
sudo apt-get update
sudo apt-get install certbot python3-certbot-nginx
sudo certbot --nginx -d your-mcp-domain.com
```

## 验证部署

1. **检查服务状态**
   ```bash
   docker logs indexs-mcp
   ```

2. **测试工具列表接口**
   ```bash
   curl http://your-mcp-domain.com/mcp/tools
   ```

3. **测试工具调用**
   ```bash
   curl -X POST http://your-mcp-domain.com/mcp/get_metrics \
     -H "Content-Type: application/json" \
     -d '{"metric_id": "test", "start_time": "2026-04-01T00:00:00Z", "end_time": "2026-04-14T00:00:00Z"}'
   ```

## 常见问题

### 服务无法启动
- 检查端口 3000 是否被占用：`netstat -tulpn | grep 3000`
- 查看容器日志：`docker logs indexs-mcp`

### 无法连接到指标平台
- 检查 `config.json` 中的 `api_base_url` 是否正确
- 确保服务器可以访问指标平台的网络

### Agent 无法调用 MCP 服务
- 确保反向代理配置正确
- 检查防火墙设置，确保端口可以访问

## 更新服务

```bash
cd indexs-mcp
git pull
docker-compose down
docker-compose up -d --build
```
