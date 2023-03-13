CREATE TABLE `employee_salary` (
  `employee_salary_id` bigint NOT NULL AUTO_INCREMENT,
  `employee_id` bigint NOT NULL,
  `year` Int NOT NULL,
  `month` Int NOT NULL,
  `salary_of_month` INT NOT NULL,
  `health_insurance` INT NOT NULL,
  `employment_insurance` INT NOT NULL,
  `welfare_pension` INT NOT NULL,
  `income_tax` INT NOT NULL,
  `salary_paid` INT NOT NULL,
  PRIMARY KEY (`employee_salary_id`),
  UNIQUE KEY `employee_salary_UN` (`employee_id`,`year`,`month`)
);