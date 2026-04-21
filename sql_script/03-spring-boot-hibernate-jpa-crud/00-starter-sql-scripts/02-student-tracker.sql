CREATE DATABASE  IF NOT EXISTS `student_data`;
USE `student_data`;

--
-- Table structure for table `learner`
--

DROP TABLE IF EXISTS `learner`;

CREATE TABLE `learner` (
  `id` int NOT NULL AUTO_INCREMENT,
  `given_name`varchar(45) DEFAULT NULL,
  `family_name` varchar(45) DEFAULT NULL,
  `mail_address` varchar(45) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=latin1;

