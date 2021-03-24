## Spring Boot 集成 Shiro 权限框架演示

### SQL 脚本

本示例使用 `Mysql` 数据库，需提前建表，脚本如下：

```sql
create table sys_user
(
    id       int auto_increment primary key,
    username varchar(50)  null comment '用户名',
    password varchar(50)  null comment '密码',
    perms    varchar(500) null comment '权限',
    role     varchar(500) null comment '角色'
) comment '用户';

INSERT INTO demo.sys_user (username, password, perms, role) VALUES ('admin', '1111', 'admin', 'admin');
INSERT INTO demo.sys_user (username, password, perms, role) VALUES ('zs', '1111', null, null);
INSERT INTO demo.sys_user (username, password, perms, role) VALUES ('ls', '1111', 'admin', null);
INSERT INTO demo.sys_user (username, password, perms, role) VALUES ('ww', '1111', null, 'admin');
```