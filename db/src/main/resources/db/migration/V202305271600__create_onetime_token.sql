-- employee.`position` definition

CREATE TABLE `one_time_token` (
  `employee_id` bigint NOT NULL,
  `one_time_token` varchar(256) NOT NULL,
  `otp_req_id` varchar(36) NOT NULL,
  `expired_at` datetime(3) NOT NULL,
  PRIMARY KEY (`employee_id`)
);