-- employee.`position` definition

CREATE TABLE `position` (
  `position_id` bigint NOT NULL AUTO_INCREMENT,
  `position_name` varchar(100) COLLATE utf8mb4_unicode_ci NOT NULL,
  `display_order` int NOT NULL,
  PRIMARY KEY (`position_id`)
);