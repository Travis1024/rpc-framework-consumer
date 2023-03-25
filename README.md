# rpc-framework-consumer

### 模块说明

| 模块             | 说明                                                         |
| ---------------- | ------------------------------------------------------------ |
| Common-API       | 服务消费者和服务提供者的公共API**（消费者和提供者共同依赖）** |
| RPC-core         | RPC核心模块，定义序列化、异常、编码、pojo、枚举类**（消费者和提供者共同依赖）** |
| RPC-client-start | RPC客户端starter，封装客户端发起的请求过程（动态代理、网络通信） |
| Consumer         | 消费者启动类，controller                                     |

<img src="https://travisnotes.oss-cn-shanghai.aliyuncs.com/mdpic/202303251503511.png" alt="rpc_code_framework" style="zoom:60%;" />
