# ADKblog-backend-client
ADK blog 网站的后端实现源码
基于Springboot 

利用redis作为中间缓存

## 环境

- java JDK1.8

- Springboot 2.5.3
- mysql 8.0
- redis 6.0.8

## 架构以及目录结构

- adkblog

  - common   //共用文件夹 存放注解之类的东西

  - config  //配置bean
  - controller  //控制层 接口转发层
  - dao/mapper  //持久层 存放mapper
    - dos   //中间数据

  - handler //拦截器
  - pojo  //实体类
  - service  //业务层 业务逻辑的具体实现接口
    - serviceImpl //接口实现

  - utils //工具类
  - vo  //页面显示类

