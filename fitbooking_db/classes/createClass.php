<?php
// Conexión a la base de datos
require_once "../db.php";

try {
    //Datos recibidos
    $start_date = $_POST['start_date'] ?? null;
    $end_date = $_POST['end_date'] ?? null;
    $time = $_POST['time'] ?? '';
    $capacity = $_POST['capacity'] ?? 0;
    $days = $_POST['days'] ?? [];

    //Datos obligatorios
    if (!$start_date || empty($time) || $capacity <= 0) {
        throw new Exception("Datos incompletos");
    }

    //Si no seleccionamos end_date ni días de la semana, clase única
    if (!$end_date || empty($days)){
        $stmt = $conn->prepare("INSERT INTO classes (classes_date, classes_time, capacity) VALUES (?, ?, ?)");
        $stmt->bind_param("ssi", $start_date, $time, $capacity);
        $stmt->execute();

        echo json_encode([
            "status"  => "success",
            "message" => "Clase única creada",
            "created" => 1
        ]);
        exit;
    } 

    //Clases recurrentes
    else {
        $created = 0;  //Contador clases creadas

        // Hoy (para no crear clases en fechas pasadas)
        $today = new DateTime('today');

        // Crear objetos fecha
        $start = new DateTime($start_date);
        $end   = new DateTime($end_date);

        // Incluir el último día
        $end->modify('+1 day');

        // Intervalo de 1 día
        $interval = new DateInterval('P1D');

        // Crear rango de fechas
        $period = new DatePeriod($start, $interval, $end);

        foreach ($period as $dateObj) {

            // Saltar fechas pasadas
            if ($dateObj < $today) {
                continue;
            }

            $date    = $dateObj->format("Y-m-d");
            $dayName = strtolower($dateObj->format("D")); // mon, tue, wed, thu...

            // Solo si el día está seleccionado
            if (in_array($dayName, $days)) {

                $stmt = $conn->prepare("
                    INSERT INTO classes (classes_date, classes_time, capacity)
                    VALUES (?, ?, ?)
                ");
                $stmt->bind_param("ssi", $date, $time, $capacity);

                try {
                    $stmt->execute();
                    $created++;
                } catch (mysqli_sql_exception $e) {
                    if ($e->getCode() != 1062) {
                        throw $e;
                    }
                    // si es duplicado (error 1062), lo ignoramos y seguimos
                }
            }
        }

        echo json_encode([
            "status"  => "success",
            "message" => "Clases recurrentes creadas",
            "created" => $created
        ]);
        exit;
    }

} catch (Exception $e) {
    // Manejar cualquier error
    $response = [
        "status" => "error",
        "message" => $e->getMessage(),
        "error_code" => $conn->errno
    ];
}

header('Content-Type: application/json');
echo json_encode($response);
?>