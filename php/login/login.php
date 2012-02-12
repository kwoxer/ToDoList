<?php
$hostname = 'localhost';
$username = 'root';
$pws = 'test';

try {
    $name = $_POST['name'];
    $pw = $_POST['pw'];

    $dbh = new PDO("mysql:host=$hostname;dbname=toDoList", $username, $pws);

    $stmt = $dbh->prepare("SELECT * FROM account WHERE name = '$name'");
    $stmt->execute();
	
	
    $result = $stmt->fetchAll();
   
	
    foreach($result as $row)
    	{
        	echo ($pw == $row['password'] ? '1' : '0');
    	}

    }
        catch(PDOException $e)
    {
    echo $e->getMessage();
    }
?>