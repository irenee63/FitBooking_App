<?php
// Conexión a la base de datos
require_once "../db.php";

try {
    //Recibe el id
    $id = $_GET['id'] ?? 0;

    //Buscar usuario por id
    $stmt = $conn->prepare ("SELECT * FROM users where id=?");
    $stmt->bind_param("i", $id);
    $stmt->execute();
  
    $result = $stmt->get_result();

    if ($result->num_rows > 0) {

        $user = $result->fetch_assoc();

        $response = array(
            "status" => "success",
            "data" => $user
        );

    } else {
        $response = array(
            "status" => "error",
            "message" => "Usuario no encontrado"
        );
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