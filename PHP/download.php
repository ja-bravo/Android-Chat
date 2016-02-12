<?php
	$nameFile = $_REQUEST["nameFile"];
	$file = 'images/photo_'.$nameFile.'.txt';

	if (file_exists($file)) 
	{
		$json = file_get_contents($file);

		return json_encode(json_decode($json));
	}
?>