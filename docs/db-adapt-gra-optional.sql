-- ============================================================
-- 可选：根据 gra.sql 适配前后端后的数据库补充脚本
-- 在 MySQL 中执行（数据库名 gra）。仅当需要时执行对应段落。
-- ============================================================

USE gra;

-- ------------------------------------------------------------
-- 1) 若历史数据中 lab_equipment 有记录但 lab_equipment_asset 为空或不全，
--    可按设备类型“数量”补全实例（实例编号 设备编号-01, 02, ...）
--    执行前请先备份。已存在对应 asset 的设备类型请勿重复执行。
-- ------------------------------------------------------------
/*
-- 为每个设备类型按 count 生成缺失的 asset 记录（示例：仅当某类型下 asset 数为 0 时补全）
-- 实例号格式与后端一致：{设备编号}-{2位序号}，设备编号为空时用 EQ{设备ID}
INSERT INTO lab_equipment_asset (equipment_id, asset_code, status)
SELECT e.id, CONCAT(COALESCE(NULLIF(TRIM(e.equipment_code), ''), CONCAT('EQ', e.id)), '-', LPAD(n.n, 2, '0')), 0
FROM lab_equipment e
CROSS JOIN (
  SELECT 1 AS n UNION SELECT 2 UNION SELECT 3 UNION SELECT 4 UNION SELECT 5
  UNION SELECT 6 UNION SELECT 7 UNION SELECT 8 UNION SELECT 9 UNION SELECT 10
) n
WHERE n.n <= GREATEST(1, COALESCE(e.count, 1))
  AND NOT EXISTS (
    SELECT 1 FROM lab_equipment_asset a
    WHERE a.equipment_id = e.id
  );
*/

-- ------------------------------------------------------------
-- 2) 确认 lab_equipment_apply 表含有 apply_quantity、approval_user_id 列
--    （gra.sql 中已包含，一般无需执行）
-- ------------------------------------------------------------
-- SELECT COLUMN_NAME FROM INFORMATION_SCHEMA.COLUMNS
-- WHERE TABLE_SCHEMA = 'gra' AND TABLE_NAME = 'lab_equipment_apply'
--   AND COLUMN_NAME IN ('apply_quantity','approval_user_id');

-- ------------------------------------------------------------
-- 3) 设备类型状态与实例状态一致化（可选）
--    若希望设备类型状态随实例汇总更新，可定期或审批后执行类似逻辑：
--    将“全部在库”的类型 status 置为 0，“存在领用中”置为 1，“存在维修中”置为 2，“全部报废”置为 3。
-- ------------------------------------------------------------
/*
UPDATE lab_equipment e
SET e.status = CASE
  WHEN (SELECT COUNT(*) FROM lab_equipment_asset a WHERE a.equipment_id = e.id AND a.status = 3) = (SELECT COUNT(*) FROM lab_equipment_asset a WHERE a.equipment_id = e.id) THEN 3
  WHEN (SELECT COUNT(*) FROM lab_equipment_asset a WHERE a.equipment_id = e.id AND a.status = 2) > 0 THEN 2
  WHEN (SELECT COUNT(*) FROM lab_equipment_asset a WHERE a.equipment_id = e.id AND a.status = 1) > 0 THEN 1
  ELSE 0
END,
e.status_text = CASE
  WHEN (SELECT COUNT(*) FROM lab_equipment_asset a WHERE a.equipment_id = e.id AND a.status = 3) = (SELECT COUNT(*) FROM lab_equipment_asset a WHERE a.equipment_id = e.id) THEN '已报废'
  WHEN (SELECT COUNT(*) FROM lab_equipment_asset a WHERE a.equipment_id = e.id AND a.status = 2) > 0 THEN '故障待修'
  WHEN (SELECT COUNT(*) FROM lab_equipment_asset a WHERE a.equipment_id = e.id AND a.status = 1) > 0 THEN '领用中'
  ELSE '在库'
END;
*/

-- ------------------------------------------------------------
-- 4) 预约表增加「预约用途」字段 purpose（与领用用途含义一致）
--    若表已有该列可跳过。
-- ------------------------------------------------------------
-- ALTER TABLE lab_equipment_reserve ADD COLUMN purpose VARCHAR(500) NULL COMMENT '预约用途' AFTER status;
