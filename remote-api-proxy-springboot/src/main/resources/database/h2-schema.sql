CREATE TABLE IF NOT EXISTS "project" (
    "id" BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '主键',
    "name" VARCHAR(200) NOT NULL DEFAULT '' COMMENT '项目名',
    "create_time" TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    "update_time" TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '更新时间',
    "deleted"
);