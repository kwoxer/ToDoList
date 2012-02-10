<?php
$hostname = 'localhost';
$username = 'root';
$password = '';

try {
    $name = $_GET['name'];
    $pw = $_GET['password'];

    $dbh = new PDO("mysql:host=$hostname;dbname=toDoList", $username, $pw);

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