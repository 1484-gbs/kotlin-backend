-- employee.`employee` definition

CREATE TABLE `employee` (
  `employee_id` bigint NOT NULL AUTO_INCREMENT,
  `first_name` varchar(20) NOT NULL,
  `last_name` varchar(20) NOT NULL,
  `first_name_kana` varchar(20) NOT NULL,
  `last_name_kana` varchar(20) NOT NULL,
  `birthday` date NOT NULL,
  `gender` enum('MALE', 'FEMALE') NOT NULL,
  `tel` varchar(15),
  `position_id` bigint NOT NULL,
  PRIMARY KEY (`employee_id`)
);