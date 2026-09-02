<?php
// Conexión a la base de datos
require_once "../db.php";

try {
    //Recibir id
    $classes_id = $_GET['classes_id'] ?? 0;

    //1. Obtener los usuarios en clase
    $sql = "SELECT users_id FROM booking WHERE classes_id = ?";
    $stmt = $conn->prepare($sql);
    $stmt->bind_param("i", $classes_id);
    $stmt->execute();
    $result = $stmt->get_result();
    //guarda el resultado en un array
    $users = [];
    while ($row = $result->fetch_assoc()) {
        $users[] = $row['users_id'];
    }

    //2. Devuelve el saldo a cada usuario
        foreach ($users as $u_id) {
        $sql = "UPDATE users SET availBalance = availBalance + 1 WHERE id = ?";
        $stmt = $conn->prepare($sql);
        $stmt->bind_param("i", $u_id);
        $stmt->execute();
    }

    //3.Elimina la clase
    $stmt = $conn->prepare("DELETE FROM classes WHERE id=?");
    $stmt->bind_param("i", $classes_id);
    $stmt->execute();


    if ($stmt->affected_rows===0) {
        throw new Exception("No se encontró la clase");
    } 

    $response = [            
        "status" => "success",
        "message" => "Clase eliminada correctamente"];

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