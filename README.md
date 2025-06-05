# toint-jdy4j

toint-jdy4j 是一个开源、免费、轻量级的简道云 Java SDK, 助力开发者快速集成简道云.

## 快速开始

```
// 客户端配置
final JdyClientConfig jdyClientConfig = new JdyClientConfig(key);
// 初始化客户端
final JdyClient jdyClient = new JdyClientImpl(jdyClientConfig);
// 新增数据
jdyClient.saveData();
// 修改数据
jdyClient.updateData();
// 查询数据
jdyClient.getData();
// 删除数据
jdyClient.deleteData();
......
// 其他方法请查看 JdyClient
......
```

## Maven

- [Maven 中央仓库](https://central.sonatype.com/artifact/cn.toint/toint-jdy4j-core)

```xml

<dependency>
    <groupId>cn.toint</groupId>
    <artifactId>toint-jdy4j-core</artifactId>
    <version>${version}</version>
</dependency>
```

## 提供bug反馈或建议

提交问题反馈请说明正在使用的 JDK 版本, `toint-jdy4j` 版本和相关依赖库版本.

- [GitHub  issue](https://github.com/toint-admin/toint-jdy4j/issues)

# 沟通说明

1. 提交地 `issue` 或 `PR` 未回复并开启状态表示还未处理, 请耐心等待.
2. 为了保证新 `issue` 及时被发现和处理, 我们会关闭一些描述不足的 `issue`, 此时你补充说明重新打开即可.
3. PR 被关闭, 表示被拒绝或需要修改地地方较多, 重新提交即可.