# 数据库初始化

-- 创建库
create database if not exists task_db;

-- 切换库
use task_db;

-- 用户表
create table if not exists user
(
    id           bigint auto_increment comment 'id' primary key,
    userAccount  varchar(256)                           not null comment '账号',
    userPassword varchar(512)                           not null comment '密码',
    userRole     varchar(256) default 'user'            not null comment '用户角色：user/admin/ban',
    createTime   datetime     default CURRENT_TIMESTAMP not null comment '创建时间',
    updateTime   datetime     default CURRENT_TIMESTAMP not null on update CURRENT_TIMESTAMP comment '更新时间',
    isDelete     tinyint      default 0                 not null comment '是否删除'
) comment '用户' collate = utf8mb4_unicode_ci;

-- 任务表
create table if not exists task
(
    id          bigint auto_increment comment 'id' primary key,
    userId      bigint                                 not null comment '用户id',
    taskStatus  varchar(256) default 'todo'            not null comment '任务状态：todo/running/done/failed',
    createTime  datetime     default CURRENT_TIMESTAMP not null comment '创建时间',
    updateTime  datetime     default CURRENT_TIMESTAMP not null on update CURRENT_TIMESTAMP comment '更新时间',
    isDelete    tinyint      default 0                 not null comment '是否删除',
    index idx_userId (userId)
) comment '任务' collate = utf8mb4_unicode_ci;