<?php
require_once "db.php";

// Solo ejecutar si es día 28
if (date("d") != "28") {
    die("Hoy no es día 28");
}

// Reiniciar saldo disponible = saldo total
$sql = "UPDATE users SET availBalance = totalBalance";

if ($conn->query($sql)) {
    echo "Saldos reiniciados correctamente";
} else {
    echo "Error: " . $conn->error;
}
?>