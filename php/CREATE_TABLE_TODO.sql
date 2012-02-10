CREATE TABLE `toDoList`.`todo` (
`_id` INT NOT NULL AUTO_INCREMENT PRIMARY KEY ,
`date` TEXT NOT NULL ,
`category` TEXT NOT NULL ,
`done` INT NOT NULL ,
`summary` TEXT NOT NULL ,
`description` TEXT NOT NULL
) ENGINE = InnoDB;