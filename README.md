# 项目名称
深圳奥琦玮开发的下一代POS，项目代号tesla

# 使用到的第三方库
Retrofit,OKHttp - 处理HTTP请求

RxJava -- 处理事件的聚合和过滤等等

ButterKnife -- View 主键，资源等绑定

DBFlow -- 读写SQLite

Robolectric -- 单元测试

# 夜神模拟器启动
<夜神模拟器bin目录>/nox_adb.exe connect 127.0.0.1:62001

# 如何访问安卓上的HTTP服务器？
在使用`adb forward tcp:本地端口 tcp:安卓设备的端口` 来转发,然后在本机的浏览器里访问`http://localhost:本地端口`即可 (目前只能只支持Genymotion模拟器)。



