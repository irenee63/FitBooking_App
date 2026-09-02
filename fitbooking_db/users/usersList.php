<?php
// Conexión a la base de datos
require_once "../db.php";

try {
    // Consulta para obtener usuarios
    $sql = "SELECT id, fullname FROM users";
    $result = $conn->query($sql);

    // Comprobar si hay resultados
    if ($result->num_rows > 0) {
        //Array para guardar los usuarios
        $users = array();
        while ($row = $result->fetch_assoc()) {
            $users[] = $row;
        }
        
        //Devolver usuarios
        $response = array(
            "status" => "success",
            "data" => $users
        );
    } else {
        // Si no hay usuarios, devolvemos array vacío
        $response = array(
            "status" => "success",
            "data" => array()
        );
    }
    // Cerrar la conexión
    $conn->close();

} catch (Exception $e) {
    // Manejar cualquier error
    $response = array(
        "status" => "error",
        "message" => $e->getMessage()
    );
}

//Enviar la respuesta en un json
header('Content-Type: application/json');
echo json_encode($response);
?>