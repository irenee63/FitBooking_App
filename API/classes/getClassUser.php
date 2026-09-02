<?php
// Conexión a la base de datos
require_once "../db.php";

try {
    $users_id = $_GET['users_id'] ?? 0;
    $today = date("Y-m-d");

    if ($users_id == 0) {
        throw new Exception("ID de usuario inválido");
    }

    $sql = "SELECT c.id, c.classes_date, c.classes_time
            FROM booking b 
            INNER JOIN classes c ON b.classes_id = c.id
            WHERE b.users_id = ?
            AND c.classes_date >= '$today'
            ORDER BY classes_date ASC";

    $stmt = $conn->prepare($sql);
    $stmt->bind_param("i", $users_id);
    $stmt->execute();
    $result = $stmt->get_result();

    $classes = [];
    while ($row = $result->fetch_assoc()) {
        $classes[] = $row;
    }

    $response = [
        "status" => "success",
        "data" => $classes
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