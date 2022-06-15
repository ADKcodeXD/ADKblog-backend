# 数据库文件

- 本后端采用了mysql8.0作为数据库

- 数据库中有bl\_开头和admin\_的表 

- bl\_开头的表是用于前台交互的，admin\_开头的表是用于博客后台的权限认证以及管理的。
- 后台暂时未整理(其实有点残废

## 如何运行

安装好数据库后，输入命令 

```bash
mysql -u username -p password

source [路径名]\adkblog.sql
```

建议安装宝塔进行数据库的管理

## 数据库字段格式

本数据库字符集格式为utf8mb4

排序规则为utf8mb4_general_ci





