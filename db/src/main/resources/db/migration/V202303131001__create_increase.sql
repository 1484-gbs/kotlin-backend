CREATE TABLE `increase` (
  `increase_id` bigint NOT NULL AUTO_INCREMENT,
  `increase_type` ENUM('HEALTH', 'WELFARE') NOT NULL,
  `salary` INT NOT NULL,
  `tax` DECIMAL(7,2) NOT NULL,
  `half_tax` DECIMAL(7,2) NOT NULL,
  PRIMARY KEY (`increase_id`),
  UNIQUE KEY `increase_UN` (`increase_type`, `salary`,`tax`)
);