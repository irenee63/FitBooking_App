<?php
// Conexión a la base de datos
require_once "../db.php";

try {
    //Recibir datos
    $users_id = $_POST['users_id'] ?? '';
    $classes_id = $_POST['classes_id'] ?? '';
    $booking_date = date("Y-m-d H:i:s");

    //Datos obligatorios
    if (empty($users_id) || empty($classes_id) || empty($booking_date)) {
        throw new Exception("Datos incompletos");
    }

    //Comprobar saldo disponible de usuario
    $sql = "SELECT availBalance FROM users WHERE id = ?";
    $stmt = $conn->prepare($sql);
    $stmt->bind_param("i", $users_id);
    $stmt->execute();
    $result = $stmt->get_result();
    $user = $result->fetch_assoc();

    if (!$user) {
        throw new Exception("Usuario no encontrado");
    }

    if ($user['availBalance'] <= 0) {
        throw new Exception("Saldo insuficiente");
    }
    
    //Comprobar si ya existe una reserva para ese usuario y en esa clase
    $stmt = $conn->prepare("SELECT id FROM booking WHERE users_id = ? AND classes_id = ?");
    $stmt->bind_param("ii", $users_id, $classes_id);
    $stmt->execute();
    $result = $stmt->get_result();

    if ($result->num_rows > 0) {
        throw new Exception("Ya tienes una reserva para esta clase");
    }

    //Comprobar si aún hay plazas disponibles en la clase
    $stmt = $conn->prepare("SELECT capacity FROM classes WHERE id = ?");
    $stmt->bind_param("i", $classes_id);
    $stmt->execute();
    $result = $stmt->get_result();
    $class = $result->fetch_assoc();

    if (!$class) throw new Exception("Clase no encontrada");

    $capacity = $class['capacity'];

    //Cuenta las reservas
    $stmt = $conn->prepare("SELECT COUNT(*) AS reserved FROM booking WHERE classes_id = ?");
    $stmt->bind_param("i", $classes_id);
    $stmt->execute();
    $result = $stmt->get_result();
    $row = $result->fetch_assoc();

    $reserved = $row['reserved'];

    if ($reserved >= $capacity) {
        throw new Exception("No quedan plazas disponibles");
    }

    //Insertar reserva
    $stmt = $conn->prepare("INSERT INTO booking (users_id, classes_id, booking_date) 
                            VALUES (?, ?, ?)");
    $stmt->bind_param("iis", $users_id, $classes_id, $booking_date);

    if (!$stmt->execute()) {
        throw new Exception("Error al insertar: " . $stmt->error);
    } 

    //Restar saldo del usuario
    $stmt = $conn->prepare("UPDATE users SET availBalance = availBalance - 1 WHERE id = ?");
    $stmt->bind_param("i", $users_id);
    $stmt->execute();

    $newId = $conn->insert_id;

    $response = [
        "status" => "success",
        "message" => "Reserva creada correctamente",
        "booking_id" => $newId
    ];

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