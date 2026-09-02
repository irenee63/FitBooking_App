<?php
// Conexión a la base de datos
require_once "../db.php";

try {
    //Recibir los datos
    $users_id = $_GET['users_id'] ?? '';
    $classes_id = $_GET['classes_id'] ?? '';

    //Consulta
    $stmt = $conn->prepare("DELETE FROM booking WHERE users_id = ? AND classes_id = ?");
    $stmt->bind_param("ii", $users_id, $classes_id);

    //Ejecutar consulta
    if ($stmt->execute()) {

        $response = array(
            "status" => "success",
            "message" => "Reserva eliminada correctamente"
        );

    } else {
        throw new Exception("Error al eliminar reserva: " . $stmt->error);
    }

    //Devuelve el saldo al usuario
    $stmt = $conn->prepare("UPDATE users SET availBalance = availBalance + 1 WHERE id = ?");
    $stmt->bind_param("i", $users_id);
    $stmt->execute();
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