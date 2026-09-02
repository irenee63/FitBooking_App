<?php
require_once "../db.php";

try {
    //Lee JSON enviado desde android
    $json = file_get_contents("php://input");
    $data = json_decode($json, true);

    if (!$data) {
        throw new Exception("JSON inválido");
    }

    //Obtiene la lista de asistencia
    $attendance = $data["attendance"] ?? null;

    //Consulta
    $sql = "UPDATE booking SET attendance=? WHERE id=?";
    $stmt = $conn->prepare($sql);

    //Recorre cada alumno y actualiza la asistencia
    foreach ($attendance as $item) {
        $booking_id = $item["booking_id"];
        $state = $item["state"];

        $stmt->bind_param("ii", $state, $booking_id);
        $stmt->execute();
    }

    $response = [
        "status" => "success",
        "message" => "Asistencia actualizada  correctamente"
    ];

    $stmt->close();
    
} catch (Exception $e) {
    $response = [
        "status" => "error",
        "message" => $e->getMessage()
    ];
}

header('Content-Type: application/json');
echo json_encode($response);
?>