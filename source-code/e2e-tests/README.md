# Free MES E2E Tests

本模块提供两类端到端测试：

- **后端 API E2E**：`TestNG` + Java HttpClient
- **前端 UI E2E**：`Playwright Java`
- 统一测试报告：`Allure TestNG`

## 目录

```text
src/test/java/io/metaxk/e2e
├── backend       # TestNG API E2E
├── frontend      # Playwright Java UI E2E
├── http          # 简化 API Client
└── config        # 环境变量配置

src/test/resources/suites
├── testng.xml            # 全量套件
├── testng-backend.xml    # 仅后端 API
└── testng-frontend.xml   # 仅前端 UI
```

## 运行前提

1. JDK 11+
2. Maven 3.8+
3. 后端服务已启动，默认地址：`http://127.0.0.1:48080/admin-api`
4. 前端服务已启动，默认地址：`http://127.0.0.1:3080`
5. 默认账号：`admin / admin123`，租户 ID：`1`

Playwright 会在首次执行时自动下载浏览器内核；如需预下载：

```bash
mvn -f source-code/e2e-tests/pom.xml test-compile
mvn -f source-code/e2e-tests/pom.xml dependency:resolve
```

## 运行测试

全量运行：

```bash
mvn -f source-code/e2e-tests/pom.xml test
```

只跑后端 API：

```bash
mvn -f source-code/e2e-tests/pom.xml test \
  -Dsurefire.suiteXmlFiles=src/test/resources/suites/testng-backend.xml
```

只跑前端 UI：

```bash
mvn -f source-code/e2e-tests/pom.xml test \
  -Dsurefire.suiteXmlFiles=src/test/resources/suites/testng-frontend.xml
```

生成并打开 Allure 报告：

```bash
mvn -f source-code/e2e-tests/pom.xml io.qameta.allure:allure-maven:serve
```

只生成报告：

```bash
mvn -f source-code/e2e-tests/pom.xml io.qameta.allure:allure-maven:report
```

## 环境变量

| 变量 | 默认值 | 说明 |
| --- | --- | --- |
| `E2E_API_BASE_URL` | `http://127.0.0.1:48080/admin-api` | 后端 admin-api 地址 |
| `E2E_FRONT_BASE_URL` | `http://127.0.0.1:3080` | 前端地址 |
| `E2E_USERNAME` | `admin` | 登录账号 |
| `E2E_PASSWORD` | `admin123` | 登录密码 |
| `E2E_TENANT_ID` | `1` | 租户 ID |
| `E2E_HEADLESS` | `true` | UI 测试是否无头 |
| `E2E_TRACE_ENABLED` | `false` | 是否保存 Playwright trace |

示例：

```bash
E2E_FRONT_BASE_URL=http://127.0.0.1:3080 \
E2E_HEADLESS=false \
mvn -f source-code/e2e-tests/pom.xml test
```

## 断言约定

- API 返回 HTTP 200 且业务 `code=0`
- UI 用例通过 `data-testid` 定位关键输入与登录按钮
- 不在测试日志中输出密码或 token
