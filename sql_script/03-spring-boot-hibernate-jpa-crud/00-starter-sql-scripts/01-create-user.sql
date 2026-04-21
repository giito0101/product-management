-- Drop user first if they exist
-- Now create user with prop privileges
DROP USER if exists 'springdemo'@'%' ;

CREATE USER 'springdemo'@'%' IDENTIFIED BY 'springdemo';

GRANT ALL PRIVILEGES ON * . * TO 'springdemo'@'%';