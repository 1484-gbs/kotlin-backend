CREATE TABLE `employee_skill` (
  `employee_skill_id` bigint NOT NULL AUTO_INCREMENT,
  `employee_id` bigint NOT NULL,
  `skill_id` bigint NOT NULL,
  PRIMARY KEY (`employee_skill_id`),
  UNIQUE KEY `employee_skill_UN` (`employee_id`,`skill_id`)
);