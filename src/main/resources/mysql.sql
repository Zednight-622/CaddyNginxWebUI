DROP schema IF EXISTS `nginx`;
create schema nginx;
use nginx;
# admin 用户表
DROP TABLE IF EXISTS `admin`;
CREATE TABLE `admin` (
	`id` BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY, 
    `name` varchar(30) NOT NULL ,
    `pass` varchar(50),
    `key` varchar(30),
    `auth` TEXT,
    `create_time` datetime,
    `update_time` datetime
)ENGINE=Innodb CHARSET=utf8mb4;

# 基础配置表
DROP TABLE IF EXISTS `basic`;
CREATE TABLE `basic` (
	`id` INT NOT NULL AUTO_INCREMENT PRIMARY KEY ,
    `name` varchar(30),
    `value` TEXT, 
    `seq` int, 
    `create_time` datetime, 
    `update_time` datetime
)engine=innodb charset=utf8mb4;

# 环境变量表
DROP TABLE IF EXISTS `setting`;
create table setting
(
    `id`          BIGINT not null AUTO_INCREMENT primary key ,
    `key`         varchar(30) UNIQUE,
    `value`       TEXT,
    `create_time` datetime,
    `update_time` datetime
)engine=innodb charset=utf8mb4;

# http模块表
DROP TABLE IF EXISTS `http`;
create table http
(
    id          BIGINT not null AUTO_INCREMENT primary key,
    name        varchar(30) UNIQUE,
    value       TEXT,
    unit        TEXT,
    seq         int,
    create_time datetime,
    update_time datetime
)engine=innodb charset=utf8mb4;

# upstream模块表
DROP TABLE IF EXISTS `upstream`;
create table upstream
(
    id          BIGINT not null AUTO_INCREMENT primary key,
    name        varchar(30) UNIQUE,
    tactics     varchar(30),
    proxy_type  int,
    monitor     int,
    create_time datetime,
    update_time datetime
)engine=innodb charset=utf8mb4;

# uptream_server 负载集群表
DROP TABLE IF EXISTS `upstream_server`;
create table upstream_server 
(
    id             BIGINT not null AUTO_INCREMENT primary key,
    upstream_id    bigint,
    server         TEXT,
    port           int,
    weight         int,
    fail_timeout   int,
    max_fails      int,
    status         TEXT,
    monitor_status int,
    create_time    datetime,
    update_time    datetime
)engine=innodb charset=utf8mb4;

# Location模块表
DROP TABLE IF EXISTS `location`;
create table location
(
    id             BIGINT not null AUTO_INCREMENT primary key,
    server_id           bigint,
    path                TEXT,
    type                int,
    location_param_json TEXT,
    value               TEXT,
    upstream_id         bigint,
    upstream_path       TEXT,
    root_path           TEXT,
    root_page           TEXT,
    root_type           TEXT,
    header              int,
	create_time    datetime,
    update_time    datetime
)engine=innodb charset=utf8mb4;

# Server模块表
DROP TABLE IF EXISTS `server`;
create table `server`
(
    id                BIGINT not null AUTO_INCREMENT primary key,
    server_name       TEXT,
    listen            TEXT,
    def               int,
    rewrite           int,
    rewrite_listen    TEXT,
    `ssl`            int,
    http2             int,
    pem               TEXT,
    `key`               TEXT,
    proxy_type        int,
    proxy_upstream_id TEXT,
    pem_str           TEXT,
    key_str           TEXT,
    `enable`            boolean,
    descr             TEXT,
    protocols         TEXT,
    password_id       TEXT,
    create_time       datetime,
    update_time       datetime
)engine=innodb charset=utf8mb4;


