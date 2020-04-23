SpringBoot代码生成脚手架
==============
版本修订

| 版本  | 修订人| 主要修订功能点 |
| :---- | :---- |  :--- |
| 0.0.1-SNAPSHOT | homer | 脚手架工程初建设，生成后的项目继承于base |

## 一、基本介绍
> 本工程是一个maven-archetype项目，主要作用是用来快速搭建spring-boot项目的脚手架

### 1.1、工程结构
```text
project-name -- 工程名
  client -- 对外暴露的api
  common -- 公共依赖层
  server -- 服务核心层
```
### 1.2、功能支持
**未完成**
- [ ] 集成mybatis plus持久化框架
- [ ] 集成HikariCP支持mysql的DataSource

**已完成**
- [x] 编写异常处理、swagger使用等示例
- [x] 添加日志logback-spring文件配置，统一日志输出格式
- [x] 集成base框架

## 二、使用说明
```text
第一步：克隆代码到本地；
git clone git@github.com:DomoXian/homer-maven-archetype.git

第二步：maven打包和安装；
mvn clean package
mvn install

第三步：生成项目;
mvn archetype:generate \
-DgroupId=输入groupId \
-DartifactId=输入artifactId \
-Dpackage=输入代码的包名 \
-Dversion=0.0.1-SNAPSHOT \
-DarchetypeGroupId=com.homer \
-DarchetypeArtifactId=homer-maven-archetype \
-DarchetypeVersion=0.0.1-SNAPSHOT
```
