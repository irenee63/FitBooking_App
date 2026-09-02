<?php
require_once "db.php";

// recibimos el usuario y la contraseña enviados por el formulario
$email = $_POST['email'] ?? '';
$password = $_POST['password'] ?? '';

// consulta si el usuario insertado está en la base de datos
$stmt = $conn->prepare("SELECT id, password, rol FROM users WHERE email = ?");
$stmt->bind_param("s", $email);
$stmt->execute();

$result = $stmt->get_result();

if ($result->num_rows == 0) {
    echo "USER_NF";
} else {
    $row = $result->fetch_assoc();

    // Verificar contraseña
    if ($password === $row['password']) {
        //Devuelve ROL e ID
        echo "LOGIN_OK_" . $row['rol'] . "#" . $row['id'];
    } else {
        echo "PASSWORD_INCORRECT";
    }
}

$stmt->close();
$conn->close();
?>