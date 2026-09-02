<?php
// Conexión a la base de datos
require_once "../db.php";

try {
    $user = $_GET['user'];
    $birth = $_GET['birth'];

    $sql = "SELECT id FROM users
            WHERE email = ? AND birthdate = ?";

    $stmt = $conn->prepare($sql);
    $stmt->bind_param("ss", $user, $birth);
    $stmt->execute();
    $result = $stmt->get_result();

    if ($result ->num_rows==0) {
        echo json_encode(["status" => "error", "message" => "Usuario o fecha incorrectos"]);
        exit;
    }

    //GENERAR CONTRASEÑA ALEATORIA
    $chars = "abcdefghijklmnopqrstuvwxyz1234567890";
    $newPass = substr(str_shuffle($chars), 0, 6);

    $sql = "UPDATE users SET password =? WHERE email =?";
    $update = $conn->prepare($sql);
    $update->bind_param("ss", $newPass, $user);
    $update->execute();

    $response = [
        "status" => "success",
        "new_password" => $newPass
    ];
    
} catch (Exception $e) {
    // Manejar cualquier error
    $response = array(
        "status" => "error",
        "message" => $e->getMessage()
    );
}

header('Content-Type: application/json');
echo json_encode($response);
?>