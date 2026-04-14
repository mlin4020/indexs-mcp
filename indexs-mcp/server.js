const express = require('express');
const fs = require('fs');
const path = require('path');
const axios = require('axios');

const app = express();
app.use(express.json());

// 读取配置
const configPath = path.join(__dirname, 'config.json');
const config = JSON.parse(fs.readFileSync(configPath, 'utf8'));

// 读取工具定义
const toolsPath = path.join(__dirname, 'tools');
const tools = {};

fs.readdirSync(toolsPath).forEach(file => {
  if (file.endsWith('.json')) {
    const toolPath = path.join(toolsPath, file);
    const tool = JSON.parse(fs.readFileSync(toolPath, 'utf8'));
    tools[tool.name] = tool;
  }
});

// 处理工具调用
app.post('/mcp/:toolName', async (req, res) => {
  const { toolName } = req.params;
  const tool = tools[toolName];
  
  if (!tool) {
    return res.status(404).json({ error: 'Tool not found' });
  }
  
  try {
    // 构建完整的API地址
    const apiUrl = config.api_base_url + tool.api_endpoint;
    
    // 发送请求到指标平台
    const response = await axios({
      url: apiUrl,
      method: tool.method,
      data: req.body,
      headers: config.headers,
      timeout: config.timeout * 1000
    });
    
    res.json(response.data);
  } catch (error) {
    res.status(500).json({ error: error.message });
  }
});

// 提供工具列表
app.get('/mcp/tools', (req, res) => {
  res.json(Object.values(tools));
});

// 启动服务器
const PORT = process.env.PORT || 3000;
app.listen(PORT, () => {
  console.log(`MCP server running on port ${PORT}`);
  console.log(`Tools available: ${Object.keys(tools).join(', ')}`);
});
