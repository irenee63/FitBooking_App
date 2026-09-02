<?php
// Conexión a la base de datos
require_once "../db.php";

try {
    $id = $_GET['id'];

    //Calcular reserved y seleccionar la clase por id
    $sql = "SELECT c.id, c.classes_date, c.classes_time, c.capacity, 
            (SELECT COUNT(*) FROM booking b WHERE b.classes_id = c.id) AS reserved
            FROM classes c
            WHERE c.id = ?";

    $stmt = $conn->prepare($sql);
    $stmt->bind_param("i", $id);
    $stmt->execute();
    $result = $stmt->get_result();

    if ($row = $result->fetch_assoc()) {
        $response = [
            "status" => "success",
            "data" => $row
        ];
    } else {
        $response = [
            "status" => "error",
            "message" => "Clase no encontrada"
        ];
    }
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