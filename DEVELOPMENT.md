# ZrLog Install Web 开发与扩展指南

## 1. 工程目录结构

```text
.
├── pom.xml                   # Maven 构建文件定义 (含前端一体化构建配置)
├── conf/                     # 外部装载配置文件存放区
└── src/
    └── main/
        ├── frontend/         # 前端独立工程目录 (含 React / Yarn 相关文件)
        │   ├── package.json  
        │   ├── src/          # 核心 React 页面组件代码挂载于 components/index.tsx
        │   └── tsconfig.json 
        ├── java/.../install  # 后端源代码运行逻辑
        │   ├── business/     # 业务逻辑服务层 (建表及初始核心逻辑承载)
        │   ├── exception/    # 系统异常枚举与自定义
        │   └── web/          # Web 请求控制器
        └── resources/
            ├── i18n/         # 国际化语言包映射资源 (.properties)
            └── init-table-structure.sql # 数据库初始化及首次表结构配置脚本
```

## 2. 本地开发环境与调试方法

### 2.1 后端调试启动
1. 依赖 JDK 11 开发环境。
2. 内部采用 SimpleWebServer 轻量框架，在开发套件 (IDE) 中定位到主类 `com.zrlog.install.Application` 从而运行 `main` 方法进入调试态。

### 2.2 前端独立开发调试
为支持页面实时重载（HMR），建议使用独立的前台端口服务：
```bash
cd src/main/frontend
yarn install
yarn start
```
*备注：前后端接口存在端口差异，如触发跨域控制限制，需在前后端代码间设置 Proxy 策略或调整 CORS 配置保证接口可达。*

## 3. 开发架构级应用修改与扩展

### 3.1 增加适配非默认数据库类型 (以 PostgreSQL 为例)

1. **依赖库扩充**: 引入 Maven `pom.xml` 中 PostgreSQL 对应的底层 JDBC 组件支持。
2. **后端驱动映射**: 拦截入口并扩展，在于 `ApiInstallController.java` 的 `getDbConn()` 中针对判断新的 `dbType` 参数，匹配对应的驱动全类名（如 `org.postgresql.Driver`）。如需定制自动建表支持，于 `InstallService.java -> createDatabase()` 自建对于方言 DDL 的派发与实现。
3. **前端表项拓展**: 修改向导组件 `src/main/frontend/src/components/index.tsx` 中的类别表单定义（`<Select name='dbType'>` 加入 Postgres 选项卡），并在 `getDefaultPort()` 根据条件变更默认端口反馈。

### 3.2 自定义内置博文与系统模板

1. **基础参数与表定制**: 直接修改内置的全局 SQL 构建文件库：`src/main/resources/init-table-structure.sql`。
2. **第一篇文章呈现内容模板**: 直接修改 `src/main/resources/i18n/init-blog/` 路径下的 `.md` 和 `.html` 格式文件组合，最终通过 `InstallService.java` 进行模板处理并提交数据库执行。

### 3.3 扩展收集高阶属性参数 (接入第三方组件及平台验证要求等)

由于在设计时前端支持自动扁平化表单，扩展新参数采集无需做复杂的传输逻辑重建：
1. **添加 UI 节点**: 到需要添加填写的表单步骤层（`components/index.tsx` 中 `state.current === 2` 的渲染条件处），自定义写入属于您的新 `FormItem` 容器和约束规则。
2. **接收与拦截提取**: 直接于后端的请求流向处 `ApiInstallController.java` 内部，通过语句 `getRequest().getParaToStr("新增字段名")` 从负载中抽离对应特征数据。获取到的数据放入至底层传入的 `business/vo` 及服务配置模型即可应用。

## 4. 国际化 (i18n) 注意规范

项目的多语言字段通过后端向导资源做全局流向绑定，严禁使用前端 TypeScript 强制硬编码中文字符串方案处理交互字眼。
1. 若要更增补一条语言提醒：前往 `src/main/resources/i18n/` 修改或建立相应的 Properties 条目（其中部分文件要求遵循标准 Unicode 编码录入）。
2. 在前端任意 React 页面内部中，通过定义并利用全局环境暴露对象法 `getRes()['键名']` 对映射条目的文本执行拉取展现操作。
