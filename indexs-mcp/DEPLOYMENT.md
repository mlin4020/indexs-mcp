# 远程MCP服务部署指南（Java版本）

## 前置条件

1. 已安装 Docker 和 Docker Compose
2. 拥有一个可访问的云服务器
3. 已配置好反向代理（可选，推荐使用 Nginx 或 Traefik）

## 项目结构

```
indexs-mcp/
├── pom.xml                              # Maven配置文件
├── Dockerfile                           # Docker构建文件
├── docker-compose.yml                   # Docker Compose配置
├── config.json                          # 配置文件（可选，用于外部配置）
├── tools/                               # 工具定义目录
│   └── get_metrics.json                # 示例工具定义
└── src/main/
    ├── java/com/indexs/mcp/
    │   ├── McpServerApplication.java   # 主应用类
    │   ├── config/                      # 配置类
    │   ├── controller/                  # 控制器
    │   ├── model/                       # 数据模型
    │   └── service/                     # 业务逻辑
    └── resources/
        └── application.yml              # Spring Boot配置
```

## 部署方式

### 方式一：使用 Maven 本地构建和运行

1. **克隆项目到服务器**
   ```bash
   git clone https://github.com/mlin4020/indexs-mcp.git
   cd indexs-mcp
   ```

2. **修改配置文件**
   编辑 `src/main/resources/application.yml` 文件，将 `mcp.server.api-base-url` 改为实际的指标平台接口地址：
   ```yaml
   mcp:
     server:
       api-base-url: https://your-metrics-api.com
       timeout: 30
       headers:
         Content-Type: application/json
   ```

3. **构建项目**
   ```bash
   mvn clean package -DskipTests
   ```

4. **运行应用**
   ```bash
   java -jar target/indexs-mcp-1.0.0.jar
   ```

### 方式二：使用 Docker 运行

1. **克隆项目到服务器**
   ```bash
   git clone https://github.com/mlin4020/indexs-mcp.git
   cd indexs-mcp
   ```

2. **修改配置文件**
   编辑 `src/main/resources/application.yml` 文件，将 `api-base-url` 改为实际的指标平台接口地址

3. **构建并运行 Docker 容器**
   ```bash
   docker build -t indexs-mcp .
   docker run -d --name indexs-mcp -p 3000:3000 --restart unless-stopped indexs-mcp
   ```

### 方式三：使用 Docker Compose（推荐）

1. **克隆项目到服务器**
   ```bash
   git clone https://github.com/mlin4020/indexs-mcp.git
   cd indexs-mcp
   ```

2. **修改配置文件**
   编辑 `src/main/resources/application.yml` 文件，将 `api-base-url` 改为实际的指标平台接口地址

3. **启动服务**
   ```bash
   docker-compose up -d --build
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
   # 使用 Docker 方式
   docker logs indexs-mcp
   
   # 使用本地方式
   # 查看应用日志
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

## 开发指南

### 添加新工具

1. 在 `tools/` 目录中创建新的工具定义 JSON 文件
2. 工具定义格式：
   ```json
   {
     "name": "tool_name",
     "description": "工具描述",
     "api_endpoint": "/api/endpoint",
     "method": "POST",
     "parameters": {
       "type": "object",
       "properties": {
         "param1": {
           "type": "string",
           "description": "参数描述"
         }
       },
       "required": ["param1"]
     }
   }
   ```

### 本地开发

1. 确保已安装 JDK 17+ 和 Maven 3.6+
2. 克隆项目到本地
3. 修改 `application.yml` 配置
4. 运行 `McpServerApplication` 主类

## 常见问题

### 服务无法启动
- 检查端口 3000 是否被占用：`netstat -tulpn | grep 3000`
- 查看容器日志：`docker logs indexs-mcp`
- 确保 JDK 版本为 17 或更高

### 无法连接到指标平台
- 检查 `application.yml` 中的 `api-base-url` 是否正确
- 确保服务器可以访问指标平台的网络
- 检查防火墙和代理设置

### Agent 无法调用 MCP 服务
- 确保反向代理配置正确
- 检查防火墙设置，确保端口可以访问
- 验证工具定义 JSON 文件格式正确

### Maven 构建失败
- 确保 Maven 版本兼容
- 检查网络连接，确保可以下载依赖
- 尝试清理 Maven 缓存：`mvn clean`

## 更新服务

```bash
cd indexs-mcp
git pull
docker-compose down
docker-compose up -d --build
```

