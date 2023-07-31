## Version0 最简单版本

- 客户端建立 Socket 连接，传输 id 给服务端，得到返回的 User 对象
- 服务端以 BIO 的方式监听 Socket，如有数据，返回对应的 User 对象给客户端
- 缺点
  1. 只支持返回一个 User（只支持一个 Service 中的一个方法），不支持其他类型的返回值
  2. 客户端只能传个 id 到服务端，服务类型单一



## Version1

- 完善通用消息格式（request, response）使用 Java 自带的序列化方法
  - request 中包含需要调用的 **Service 接口名，方法名，参数，参数类型**
  - response 中**除了传输对象 Object 之外，引入状态码和状态信息表示服务调用成功还是失败**
- 增加服务类型，在 UserService 接口中新增一个服务方法
- 客户端根据不同的 Service 进行动态代理
  - 客户端创建代理对象，每一次代理对象调用方法，做以下增强（通过反射构建 request 对象、sendRequest 发送至客户端）
  - 客户端可以调用不同的服务方法
- 服务端读取客户端传过来的 request，通过反射调用对应的方法，得到的数据封装进 response 返回给客户端
- 缺点
  1. 服务端只支持 UserService，不支持多个服务的注册
  2. 服务端 BIO 方式，性能较低
  3. 服务端监听、处理在一个线程的代码块中执行，需要松耦合



## Version2

- 添加一个新的服务接口 BlogService
- 添加服务暴露类 ServiceProvider，其主要属性是一个 map，记录**接口名与服务端实现类的对应关系**，可以从 request 中获取接口名，从 ServiceProvider 中找到对应的实现类
- 重构服务端代码，功能松耦合
  - 将 RPCServer 抽象成接口，开放封闭原则
  - 提供 BIO 和 使用线程池 两种 RPCServer 的实现类，负责监听客户端的请求
  - 新增 工作任务类 WorkThread，负责解析从客户端得到的 request 请求，执行服务方法，返回给客户端
- 客户端中添加 BlogService 方法的测试用例
- 缺点
  - 传统的 BIO 与线程池网络传输性能较低



## Version3

- 引入高性能网络框架 Netty 实现网络通信
- 服务端：定义 Netty 服务器，实现 RPCServer 接口
  - 建立 bossGroup 和 workGroup 两个事件循环组，分别负责建立连接和处理具体请求
  - 自定义通道初始化器，添加各种处理器
    - 定义消息格式，解决粘包问题
    - 序列化处理
    - 定义服务端处理器（继承入站处理器类），回调方法中实现**读取 request 返回 response**<u>（反射调用服务方法，封装 response）</u>
  - bind() 绑定端口、sync() 同步阻塞至连接成功为止
- 客户端：定义 Netty 客户端，实现 RPCClient 接口
  - 建立 workGroup 事件循环组，客户端不用处理连接事件，只用创建一个
  - 自定义通道初始化器，添加各种处理器
    - 前两项和服务端一致
    - 定义服务端处理器（继承入站处理器类），回调方法中接收 response，使用 AttributeKey 保存其属性
  - connect() 建立连接、sync() 同步阻塞至连接成功为止
  - 建立连接成功后，获取对应的通道，**写入 request**<u>（request 的创建和 netty 客户端的创建都是基于动态代理）</u>
  - **获取 response**