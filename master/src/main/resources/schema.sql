DROP DATABASE IF EXISTS `air_conditioner_system`;
CREATE DATABASE `air_conditioner_system`;
USE `air_conditioner_system`;

-- 顾客
DROP TABLE IF EXISTS user;
CREATE TABLE user
(
    id       bigint PRIMARY KEY NOT NULL,
    name     VARCHAR(32) UNIQUE NOT NULL,
    password VARCHAR(6)         NOT NULL DEFAULT '123456' -- 顾客的初始密码为 123456, 长度为 6 位数的数字, 和银行密码类似
);

-- 记录 room_id 和 user_id 之间的映射关系的表
DROP TABLE IF EXISTS room;
CREATE TABLE room
(
    room_id bigint PRIMARY KEY NOT NULL,
    user_id bigint,
    inuse   boolean            NOT NULL DEFAULT FALSE
);

-- 中央空调记录的每次温控请求
DROP TABLE IF EXISTS request;
CREATE TABLE request
(
    id                bigint PRIMARY KEY NOT NULL,
    room_id           bigint             NOT NULL,
    start_time        DATETIME           NOT NULL,
    stop_time         DATETIME           NOT NULL,
    start_temperature double             NOT NULL,
    stop_temperature  double             NOT NULL,
    fan_speed         VARCHAR(6)         NOT NULL DEFAULT 'MEDIUM', -- 风速等级
    total_fee         decimal(10, 2)     NOT NULL
);