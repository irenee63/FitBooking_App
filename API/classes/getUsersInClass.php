<?php
require_once "../db.php";

try {
    $classes_id = $_GET['classes_id'] ?? 0;

    if ($classes_id == 0) {
        throw new Exception("ID de clase inválido");
    }

    $sql = "SELECT 
            b.id AS booking_id,
            u.id AS user_id, 
            u.fullname, b.attendance 
            FROM booking b 
            INNER JOIN users u ON b.users_id = u.id
            WHERE b.classes_id = ?";

    $stmt = $conn->prepare($sql);
    $stmt->bind_param("i", $classes_id);
    $stmt->execute();
    $result = $stmt->get_result();

    $users = [];
    while ($row = $result->fetch_assoc()) {
        $users[] = $row;
    }

    $response = [
        "status" => "success",
        "data" => $users
    ];

} catch (Exception $e) {
    $response = [
        "status" => "error",
        "message" => $e->getMessage()
    ];
}

header('Content-Type: application/json');
echo json_encode($response);
?>