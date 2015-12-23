<?php

// Programa que le pasas el nombre de una imagen y te muestra su imagen cargandola de un fichero txt 
// codificado en base64.
// Nota: es fichero lo crea uploadImage.php.

$nameFile = $_REQUEST["nameFile"];
$file = 'images/photo_'.$nameFile.'.txt';

if (file_exists($file)) 
{
	header("Content-type: image/gif");
	$json = file_get_contents($file);
	$obj = json_decode($json);
	echo base64_decode( $obj->{'json'} );
}
else
{
	echo 'No se cargo nada mierda';
}

?>