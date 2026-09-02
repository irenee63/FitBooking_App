<?php
// Conexión a la base de datos
require_once "../db.php";

try {
    //Recibir id
    $id = $_POST['id'] ?? 0;

    //Consulta
    $stmt = $conn->prepare("DELETE FROM users WHERE id=?");
    $stmt->bind_param("i", $id);

    //Ejecutar consulta
    if ($stmt->execute()) {

        $response = array(
            "status" => "success",
            "message" => "Usuario eliminado correctamente"
        );

    } else {
        throw new Exception("Error al eliminar usuario: " . $stmt->error);
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