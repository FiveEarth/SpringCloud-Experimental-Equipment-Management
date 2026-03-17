-- 预约数量字段：若表 lab_equipment_reserve 中尚无 reserve_quantity，则添加并调整唯一约束
-- 在查询中执行（按需执行）

-- 1) 添加预约数量列（若已存在会报错，可忽略）
ALTER TABLE lab_equipment_reserve
  ADD COLUMN reserve_quantity INT(10) NOT NULL DEFAULT 1 COMMENT '预约设备数量' AFTER user_name;

-- 2) 若表已有旧唯一约束 uk_reserve_conflict 且不包含 reserve_quantity，需先删除再重建（按实际约束名调整）
-- 查看当前唯一约束： SHOW INDEX FROM lab_equipment_reserve WHERE Key_name = 'uk_reserve_conflict';
-- 若唯一键为 (equipment_id, reserve_date, start_time, end_time) 则执行：
-- ALTER TABLE lab_equipment_reserve DROP INDEX uk_reserve_conflict;
-- ALTER TABLE lab_equipment_reserve ADD UNIQUE INDEX uk_reserve_conflict(equipment_id, reserve_date, start_time, end_time, reserve_quantity);

-- 若执行 1 报 “Duplicate column name 'reserve_quantity'”，说明列已存在，无需再执行。
