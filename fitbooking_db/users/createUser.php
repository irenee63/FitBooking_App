<?php
// Conexión a la base de datos
require_once "../db.php";

try {
    //Recibir datos
    $rol = $_POST['rol'] ?? 'user';
    $email = $_POST['email'] ?? '';
    $password = $_POST['password'] ?? '';
    $fullname = $_POST['fullname'] ?? '';
    $birthdate = $_POST['birthdate'] ?? null;
    $totalBalance = $_POST['totalBalance'] ?? 0;
    $availBalance = $_POST['availBalance'] ?? 0;

    //Datos obligatorios
    if (empty($email) || empty($password) || empty($fullname)) {
        throw new Exception("Datos incompletos");
    }

    //Consulta insertar usuario
    $stmt = $conn->prepare("INSERT INTO users (rol, email, password, fullname, birthdate, totalBalance, availBalance) 
                            VALUES (?, ?, ?, ?, ?, ?, ?)");
    $stmt->bind_param("sssssii", $rol, $email, $password, $fullname, $birthdate, $totalBalance, $availBalance);

    try {
        $stmt->execute();
    } catch (mysqli_sql_exception $e) {
        if ($e->getCode() == 1062) {
            echo json_encode([
                "status"  => "error",
                "message" => "Ya existe un usuario con ese email"
            ]);
            exit;
        }
        throw $e;
    }

    //otros errores
    if ($stmt->errno !== 0) {
        echo json_encode([
        "status"  => "error",
        "message" => "Error al insertar: " . $stmt->error
        ]);
        exit;
    }

    //Usuario creado correctamente
    $newId = $conn->insert_id;

    $response = array(
        "status" => "success",
        "message" => "Usuario creado correctamente",
        "user_id" => $newId
    );
            
} catch (Exception $e) {

    $response = array(
        "status" => "error",
        "message" => $e->getMessage()
    );

}

header('Content-Type: application/json');
echo json_encode($response);
?>