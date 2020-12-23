DROP schema IF EXISTS `cngui`;
create schema `cngui`;
use `cngui`;
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

# 基础配置表
DROP TABLE IF EXISTS `nginx_basic`;
CREATE TABLE `basic` (
	`id` INT NOT NULL AUTO_INCREMENT PRIMARY KEY ,
    `name` varchar(30),
    `value` TEXT, 
    `seq` int, 
    `create_time` datetime, 
    `update_time` datetime
)engine=innodb charset=utf8mb4;

# http模块表
DROP TABLE IF EXISTS `nginx_http`;
create table nginx_http
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
DROP TABLE IF EXISTS `nginx_upstream`;
create table nginx_upstream
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
DROP TABLE IF EXISTS `nginx_upstream_server`;
create table nginx_upstream_server 
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
DROP TABLE IF EXISTS `nginx_location`;
create table nginx_location
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
DROP TABLE IF EXISTS `nginx_server`;
create table `nginx_server`
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


# caddyGlobal模块表
DROP TABLE IF EXISTS `caddy_global`;
create table `caddy_global`
(
    id                BIGINT not null AUTO_INCREMENT primary key,
    `key`			  TEXT,
    `value` 		  TEXT,
    seq				  int,
    create_time       datetime,
    update_time       datetime
)engine=innodb charset=utf8mb4;

# caddySnippet模块表
DROP TABLE IF EXISTS `caddy_snippet`;
create table `caddy_snippet`
(
    id                BIGINT not null AUTO_INCREMENT primary key,
    `name`            TEXT,
    seq				  int,
    create_time       datetime,
    update_time       datetime
)engine=innodb charset=utf8mb4;

# caddySnippet中键值表
DROP TABLE IF EXISTS `caddy_snippet_token`;
create table `caddy_snippet_token`
(
    id                BIGINT not null AUTO_INCREMENT primary key,
    `key`            TEXT,
    `value`            TEXT,
    snippet_id        bigint,
    seq				  int,
    create_time       datetime,
    update_time       datetime
)engine=innodb charset=utf8mb4;

# caddySite模块表
DROP TABLE IF EXISTS `caddy_site`;
create table `caddy_site`
(
    id                BIGINT not null AUTO_INCREMENT primary key,
    address			  TEXT,
    `matcher_token`   Text,
    reverse_proxy_id  BIGINT,
    `file_server`     Text,
    `import`		  Text,
    seq				  int,
    create_time       datetime,
    update_time       datetime
)engine=innodb charset=utf8mb4;

# caddySite中的键值模块表
DROP TABLE IF EXISTS `caddy_site_token`;
create table `caddy_site_token`
(
    id                BIGINT not null AUTO_INCREMENT primary key,
    `key`            TEXT,
    `value`            TEXT,
    site_id        bigint,
    seq				  int,
    create_time       datetime,
    update_time       datetime
)engine=innodb charset=utf8mb4;

# caddySite模块表
DROP TABLE IF EXISTS `caddy_reverse_proxy`;
create table `caddy_reverse_proxy`
(
    id                BIGINT not null AUTO_INCREMENT primary key,
    address			  TEXT,
    `matcher_token`   Text,
    reverse_id        bigint,
    lb_policy         Text,
    create_time       datetime,
    update_time       datetime
)engine=innodb charset=utf8mb4;