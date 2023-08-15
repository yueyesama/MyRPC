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
  1. 传统的 BIO 与线程池网络传输性能较低



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
- 缺点
  1. 使用 Java 自带序列化方式（Java 序列化写入不仅是完整的类名，也包含整个类的定义，包含所有被引用的类），不够通用，不够高效



## Version4

- 自定义传输格式与编解码方式，支持 Java 原生序列化与 json 序列化

- 自定义序列化器

  - 创建 Serializer 接口
    - 提供 serialize 和 deserialize 方法，前者把对象转成字节数组，后者把字节数组转成对象（deserialize 方法需要额外提供 <u>对象类型</u> 参数）
    - 提供 getType 和 getSerializerByCode 方法，方便在编码时获取序列化类型，在解码时找到对应的序列化器执行反序列化
  - 定义原生序列化器 ObjectSerializer 实现 Serializer 接口
    - 通过 ByteArrayOutputStream 和 ObjectOutputStream 实现 序列化
    - 通过 ByteArrayInputStream 和 ObjectInputStream 实现 反序列化
  - 定义 json 序列化器 JsonSerializer 实现 Serializer 接口
    - 通过 JSONObject.toJSONBytes() 实现序列化
    - 通过 JSON.parseObject() 实现反序列化，需要提供对应的 .class 对象

- 自定义编解码器

  - 格式

  | 消息类型 2Byte | 序列化方式 2Byte | 消息长度 4Byte | 序列化字节数组 ？byte |
  - MyEncode 类继承 MessageToByteEncoder 实现 encode 方法，定义编码逻辑
  - MyDecode 类继承 ByteToMessageDecoder 实现 decode 方法，定义解码逻辑

  - NettyInitializer 中替换成自定义的编解码器

- 缺点

  1. 服务端与客户端通信的 host 与 port 在代码里写死了，扩展性不强



## Version5

- 引入 Zookeeper 作为注册中心，在注册中心注册自己的服务与对应的地址，而客户端调用服务时，去注册中心根据服务名找到对应的服务端地址
- 定义服务注册接口 ServiceRegister 提供两大基础功能

  - 注册（服务端）：保存服务与地址
  - 查询（客户端）：根据服务名查找地址
- 定义服务注册方法 ZkServiceRegister 实现 ServiceRegister，通过 Curator 提供的 Zookeeper 客户端来连接 Zookeeper

  - 注：Zookeeper 可以看作一个树形目录文件系统，是一个分布式协调应用
  - **创建会话：**使用 Curator 创建客户端，指定 Zookeeper 地址、重试策略、超时时间、命名空间（用于不同应用间的相互隔离）等
  - **服务注册：**
    - /serviceName 创建成持久化节点，服务提供者下线时，不删服务名，只删地址
    - /serviceName/serverAddress 创建成临时节点，服务器下线就删除节点
  - **服务发现：**
    - 根据服务名路径 /serviceName 返回地址 serverAddress（这里默认用第一个）
- 服务端、客户端新增注册和发现功能

  - **服务端：**重用服务暴露类，传入host，port，服务名，顺便做服务注册
  - **客户端：**启动时需要先初始化注册中心，建立连接，从注册中心获得host，port
- 缺点
  1. 根据服务名查询地址时，我们返回的总是第一个IP，导致这个提供者压力巨大，而其它提供者调用不到