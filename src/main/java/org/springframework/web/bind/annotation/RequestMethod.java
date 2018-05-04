package org.springframework.web.bind.annotation;

public enum RequestMethod {
    GET, // 但是由于GET中URL是有长度的限制的，而GET会把所有的参数都放在URL中。 数据都明文暴露，用户可以直接看到 。用于执行查询操作
    HEAD, // 只会返回首部的信息，不会返回相应体。通常用于测试数据是否存在、当做心跳检测等等。
    POST, // POST方法把数据都存放在body里面，这样即突破了长度的限制；又保证用户无法直接看到。在使用表单时，比较常用
    PUT, // 与GET相反，用于改变某些内容。
    PATCH, // servlet 3.0提供的方法，主要用于更新部分字段。与PUT方法相比，PUT提交的相当于全部数据的更新，类似于update；而PATCH则相当于更新部分字段，如果数据不存在则新建，有点类似于neworupdate。
    DELETE, // 删除某些资源
    OPTIONS, // 询问服务器支持的方法。
    TRACE // 看看一条请求在到达服务前数据发生了什么变化。可以使用这个命令，它会在最后一站返回原始信息，这样就可以观察到中间是否修改过请求。(经常会用于跨站攻击，所以有一定的安全隐患)
}
