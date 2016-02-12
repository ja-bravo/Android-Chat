<?php
	$nameFile = $_REQUEST["nameFile"];
	$file = 'images/photo_'.$nameFile.'.txt';

	if (file_exists($file)) 
	{
		header("Content-type: image/gif");
		$json = file_get_contents($file);
		$obj = json_decode($json);
		echo base64_decode( $obj->{'json'} );
	}
?>