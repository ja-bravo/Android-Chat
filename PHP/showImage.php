<?php

// Programa que le pasar una id del usuario y te muestra su imagen cargandola de un fichero txt 
// codificado en base64.
// Nota: es fichero lo crea uploadImage.php.

$idUser = $_REQUEST["idUser"];
$file = 'images/photo_'.$idUser.'.txt';

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