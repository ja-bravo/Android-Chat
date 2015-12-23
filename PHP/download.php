<?php

// Programa que le pasas el nombre una imagen y te devuelve su imagen cargandola de un fichero txt 
// codificado en base64.
// Nota: es fichero lo crea uploadImage.php.

$nameFile = $_REQUEST["nameFile"];
$file = 'images/photo_'.$nameFile.'.txt';

if (file_exists($file)) 
{
	$json = file_get_contents($file);

	// Nota: tengo descodificar el $json que viene en el fichero, para volver a codificarlo para poder devolverlo
	// Si lo envias tan cual lo sacas del fichero es como si se volviera a codificar y luego da error en android
	return json_encode(json_decode($json));
}
else
{
	echo 'No se cargo nada mierda';
}

?>