CREATE TABLE `toDoList`.`hat`(
`_id` INT NOT NULL , 
`_cid` INT NOT NULL , 
FOREIGN KEY (`_id`) REFERENCES todo (_id) ON DELETE CASCADE ON UPDATE CASCADE, 
PRIMARY KEY (`_id`, `_cid`) 
)ENGINE = InnoDB