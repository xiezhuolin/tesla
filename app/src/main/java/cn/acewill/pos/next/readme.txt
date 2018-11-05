目录说明
presenter: 接收界面的请求，然后进行真正的业务逻辑，比如调用dao或者service进行数据库或者网络操作
dao: 操作本地SQLite
model: 业务对象
service: 通过HTTP操作网络
factory: 一些需要全局初始化的类
ui: 所有的view相关的东西，比如activity， fragment等