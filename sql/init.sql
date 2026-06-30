-- ============================================================
-- 数据库创建脚本
-- 项目：DuanFengHeng 菜品管理系统
-- 数据库：MySQL 8.0
-- ============================================================

-- 创建数据库
CREATE DATABASE IF NOT EXISTS db_reggie DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci;

USE db_reggie;

-- ============================================================
-- 1. 员工表
-- ============================================================
DROP TABLE IF EXISTS employee;
CREATE TABLE employee (
    id          BIGINT       NOT NULL PRIMARY KEY COMMENT '主键ID',
    username    VARCHAR(32)  NOT NULL COMMENT '用户名',
    name        VARCHAR(32)  NOT NULL COMMENT '姓名',
    password    VARCHAR(64)  NOT NULL COMMENT '密码',
    phone       VARCHAR(11)  DEFAULT NULL COMMENT '手机号',
    sex         VARCHAR(2)   DEFAULT NULL COMMENT '性别',
    id_number   VARCHAR(18)  NOT NULL COMMENT '身份证号码',
    status      INT          NOT NULL DEFAULT 1 COMMENT '状态 1:启用 0:禁用',
    create_time DATETIME     DEFAULT NULL COMMENT '创建时间',
    update_time DATETIME     DEFAULT NULL COMMENT '更新时间',
    create_user BIGINT       DEFAULT NULL COMMENT '创建人',
    update_user BIGINT       DEFAULT NULL COMMENT '修改人'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='员工信息表';

-- 插入默认管理员账号（密码为加密后的值，此处为示例）
INSERT INTO employee (id, username, name, password, phone, sex, id_number, status, create_time, update_time, create_user, update_user)
VALUES (1, 'admin', '管理员', 'e10adc3949ba59abbe56e057f20f883e', '13800138000', '男', '110101199001011234', 1, NOW(), NOW(), 1, 1);

-- ============================================================
-- 2. 分类表
-- ============================================================
DROP TABLE IF EXISTS category;
CREATE TABLE category (
    id          BIGINT       NOT NULL AUTO_INCREMENT PRIMARY KEY COMMENT '主键ID',
    type        INT          NOT NULL COMMENT '类型 1:菜品分类 2:套餐分类',
    name        VARCHAR(32)  NOT NULL COMMENT '分类名称',
    sort        INT          NOT NULL DEFAULT 0 COMMENT '排序',
    create_time DATETIME     DEFAULT NULL COMMENT '创建时间',
    update_time DATETIME     DEFAULT NULL COMMENT '更新时间',
    create_user BIGINT       DEFAULT NULL COMMENT '创建人',
    update_user BIGINT       DEFAULT NULL COMMENT '修改人'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='菜品及套餐分类表';

-- ============================================================
-- 3. 菜品表
-- ============================================================
DROP TABLE IF EXISTS dish;
CREATE TABLE dish (
    id          BIGINT         NOT NULL AUTO_INCREMENT PRIMARY KEY COMMENT '主键ID',
    name        VARCHAR(32)    NOT NULL COMMENT '菜品名称',
    category_id BIGINT         NOT NULL COMMENT '菜品分类ID',
    price       DECIMAL(10,2)  NOT NULL COMMENT '菜品价格',
    code        VARCHAR(64)    NOT NULL DEFAULT '' COMMENT '商品码',
    image       VARCHAR(255)   DEFAULT NULL COMMENT '图片路径',
    description VARCHAR(255)   DEFAULT NULL COMMENT '描述信息',
    status      INT            NOT NULL DEFAULT 1 COMMENT '售卖状态 0:停售 1:起售',
    sort        INT            NOT NULL DEFAULT 0 COMMENT '排序',
    create_time DATETIME       DEFAULT NULL COMMENT '创建时间',
    update_time DATETIME       DEFAULT NULL COMMENT '更新时间',
    create_user BIGINT         DEFAULT NULL COMMENT '创建人',
    update_user BIGINT         DEFAULT NULL COMMENT '修改人',
    is_deleted  INT            NOT NULL DEFAULT 0 COMMENT '逻辑删除 0:未删除 1:已删除'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='菜品信息表';

-- ============================================================
-- 4. 菜品口味表
-- ============================================================
DROP TABLE IF EXISTS dish_flavor;
CREATE TABLE dish_flavor (
    id          BIGINT       NOT NULL AUTO_INCREMENT PRIMARY KEY COMMENT '主键ID',
    dish_id     BIGINT       NOT NULL COMMENT '菜品ID',
    name        VARCHAR(32)  NOT NULL COMMENT '口味名称',
    value       VARCHAR(255) DEFAULT NULL COMMENT '口味数据（JSON格式）',
    create_time DATETIME     DEFAULT NULL COMMENT '创建时间',
    update_time DATETIME     DEFAULT NULL COMMENT '更新时间',
    create_user BIGINT       DEFAULT NULL COMMENT '创建人',
    update_user BIGINT       DEFAULT NULL COMMENT '修改人',
    is_deleted  INT          NOT NULL DEFAULT 0 COMMENT '逻辑删除 0:未删除 1:已删除'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='菜品口味表';

-- ============================================================
-- 5. 套餐表
-- ============================================================
DROP TABLE IF EXISTS setmeal;
CREATE TABLE setmeal (
    id          BIGINT         NOT NULL AUTO_INCREMENT PRIMARY KEY COMMENT '主键ID',
    category_id BIGINT         NOT NULL COMMENT '套餐分类ID',
    name        VARCHAR(32)    NOT NULL COMMENT '套餐名称',
    price       DECIMAL(10,2)  NOT NULL COMMENT '价格（单位：元）',
    status      INT            NOT NULL DEFAULT 1 COMMENT '售卖状态 0:停售 1:起售',
    code        VARCHAR(64)    DEFAULT NULL COMMENT '商品码',
    description VARCHAR(255)   DEFAULT NULL COMMENT '描述信息',
    image       VARCHAR(255)   DEFAULT NULL COMMENT '图片路径',
    create_time DATETIME       DEFAULT NULL COMMENT '创建时间',
    update_time DATETIME       DEFAULT NULL COMMENT '更新时间',
    create_user BIGINT         DEFAULT NULL COMMENT '创建人',
    update_user BIGINT         DEFAULT NULL COMMENT '修改人'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='套餐信息表';

-- ============================================================
-- 6. 套餐-菜品关联表
-- ============================================================
DROP TABLE IF EXISTS setmeal_dish;
CREATE TABLE setmeal_dish (
    id          BIGINT         NOT NULL AUTO_INCREMENT PRIMARY KEY COMMENT '主键ID',
    setmeal_id  BIGINT         NOT NULL COMMENT '套餐ID',
    dish_id     BIGINT         NOT NULL COMMENT '菜品ID',
    name        VARCHAR(32)    DEFAULT NULL COMMENT '菜品名称（冗余字段）',
    price       DECIMAL(10,2)  DEFAULT NULL COMMENT '菜品单价',
    copies      INT            DEFAULT NULL COMMENT '份数',
    sort        INT            NOT NULL DEFAULT 0 COMMENT '排序',
    create_time DATETIME       DEFAULT NULL COMMENT '创建时间',
    update_time DATETIME       DEFAULT NULL COMMENT '更新时间',
    create_user BIGINT         DEFAULT NULL COMMENT '创建人',
    update_user BIGINT         DEFAULT NULL COMMENT '修改人',
    is_deleted  INT            NOT NULL DEFAULT 0 COMMENT '逻辑删除 0:未删除 1:已删除'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='套餐菜品关联表';

-- ============================================================
-- 7. 订单表
-- ============================================================
DROP TABLE IF EXISTS orders;
CREATE TABLE orders (
    id             BIGINT         NOT NULL AUTO_INCREMENT PRIMARY KEY COMMENT '主键ID',
    number         VARCHAR(64)    NOT NULL COMMENT '订单号',
    status         INT            NOT NULL DEFAULT 1 COMMENT '订单状态 1:待付款 2:待派送 3:已派送 4:已完成 5:已取消',
    user_id        BIGINT         NOT NULL COMMENT '下单用户ID',
    address_book_id BIGINT        NOT NULL COMMENT '地址ID',
    order_time     DATETIME       NOT NULL COMMENT '下单时间',
    checkout_time  DATETIME       DEFAULT NULL COMMENT '结账时间',
    pay_method     INT            NOT NULL DEFAULT 1 COMMENT '支付方式 1:微信 2:支付宝',
    amount         DECIMAL(10,2)  NOT NULL COMMENT '实收金额',
    remark         VARCHAR(255)   DEFAULT NULL COMMENT '备注',
    user_name      VARCHAR(32)    DEFAULT NULL COMMENT '用户名',
    phone          VARCHAR(11)    DEFAULT NULL COMMENT '手机号',
    address        VARCHAR(255)   DEFAULT NULL COMMENT '地址',
    consignee      VARCHAR(32)    DEFAULT NULL COMMENT '收货人'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='订单信息表';

-- ============================================================
-- 8. 订单明细表
-- ============================================================
DROP TABLE IF EXISTS order_detail;
CREATE TABLE order_detail (
    id          BIGINT         NOT NULL AUTO_INCREMENT PRIMARY KEY COMMENT '主键ID',
    name        VARCHAR(32)    DEFAULT NULL COMMENT '名称',
    order_id    BIGINT         NOT NULL COMMENT '订单ID',
    dish_id     BIGINT         DEFAULT NULL COMMENT '菜品ID',
    setmeal_id  BIGINT         DEFAULT NULL COMMENT '套餐ID',
    dish_flavor VARCHAR(64)    DEFAULT NULL COMMENT '口味',
    number      INT            NOT NULL DEFAULT 1 COMMENT '数量',
    amount      DECIMAL(10,2)  NOT NULL COMMENT '金额',
    image       VARCHAR(255)   DEFAULT NULL COMMENT '图片路径'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='订单明细表';
