<?php
// Conexión a la base de datos
require_once "../db.php";

try {
    $date = $_GET['date']; // YYYY-MM-DD

    //Calcular reserved
    $sql = "SELECT c.id, c.classes_date, c.classes_time, c.capacity, 
            (SELECT COUNT(*) FROM booking b WHERE b.classes_id = c.id) AS reserved
            FROM classes c
            WHERE classes_date = ? 
            ORDER BY classes_time ASC";

    $stmt = $conn->prepare($sql);
    $stmt->bind_param("s", $date);
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