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
  `salary_of_month` INT NOT NULL,
  `login_id` VARCHAR(256) NOT NULL,
  `password` VARCHAR(256) NOT NULL,
  `created_by` VARCHAR(256) NOT NULL,
  `created_at` datetime(3) NOT NULL,
  `updated_by` VARCHAR(256),
  `updated_at` datetime(3),
  PRIMARY KEY (`employee_id`),
  UNIQUE KEY(`login_id`)
);