<?php
$servername = "";
$username = "";
$password = "";
$dbname = "";
$conn = new mysqli($servername, $username, $password, $dbname);

if ($conn->connect_error) {
    die(json_encode([
        "status"  => "error",
        "message" => "Error de conexión: " . $conn->connect_error
    ]));
}
?>