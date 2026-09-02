<?php
// Conexión a la base de datos
require_once "../db.php";

try {
    //Recibir datos
    $users_id = $_GET['users_id'] ?? '';
    $classes_id = $_GET['classes_id'] ?? '';

    //Datos obligatorios
    if (empty($users_id) || empty($classes_id)) {
        throw new Exception("Datos incompletos");
    }

    //Comprobar si ya existe una reserva para ese usuario y en esa clase
    $stmt = $conn->prepare("SELECT id FROM booking WHERE users_id = ? AND classes_id = ?");
    $stmt->bind_param("ii", $users_id, $classes_id);
    $stmt->execute();
    $result = $stmt->get_result();

    if ($row = $result->fetch_assoc()) {
        $response = [
            "status" => "exists",
            "booking_id" => $row['id']
        ];
    } else {
        $response = [
            "status" => "not_found"
        ];
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