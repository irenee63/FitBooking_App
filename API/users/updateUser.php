<?php
// Conexión a la base de datos
require_once "../db.php";

try {
 //Recibir datos
    $id = $_POST['id'] ?? 0;
    $fullname = $_POST['fullname'] ?? '';
    $email = $_POST['email'] ?? '';
    $password = $_POST['password'] ?? '';
    $birthdate = $_POST['birthdate'] ?? null;
    $totalBalance = $_POST['totalBalance'] ?? 0;
    $availBalance = $_POST['availBalance'] ?? 0;

    if ($id == 0 || empty($fullname) || empty($email)) {
        throw new Exception("Datos incompletos");
    }

    //Consulta
    $stmt = $conn->prepare("UPDATE users SET fullname=?, email=?, password=?, birthdate=?, totalBalance=?, availBalance=? WHERE id=?");
    $stmt->bind_param("ssssiii", $fullname, $email, $password, $birthdate, $totalBalance, $availBalance, $id);

    //Ejecutar consulta
    if ($stmt->execute()) {

        $response = array(
            "status" => "success",
            "message" => "Usuario actualizado correctamente"
        );

    } else {
        throw new Exception("Error al actualizar: " . $stmt->error);
    }

    $stmt->close();
    $conn->close();

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