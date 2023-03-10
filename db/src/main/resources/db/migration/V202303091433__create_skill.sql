CREATE TABLE `skill` (
  `skill_id` bigint NOT NULL AUTO_INCREMENT,
  `skill_name` varchar(100) COLLATE utf8mb4_unicode_ci NOT NULL,
  `skill_type` enum('LANGUAGE','FW','DB','INFRA') COLLATE utf8mb4_unicode_ci NOT NULL,
  `display_order` int NOT NULL,
  PRIMARY KEY (`skill_id`)
);