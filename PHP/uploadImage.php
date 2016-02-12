<?php
	$data = $_REQUEST["json"];
	$data = str_replace ( "+" , " " , $data  );	
	$data = str_replace ( "SUMAR" , "+" , $data );
	
	$nameFile = $_REQUEST["nameFile"];

	saveJSON($data , $nameFile); 

	createImage ($data , $nameFile);
	
   function saveJSON($data , $nameFile)
   { 
      $file = 'images/photo_'.$nameFile.'.txt';
      $success = file_put_contents($file, $data);
   }
   
   function createImage($json , $nameFile)
   {
		header("Content-type: image/gif");
		$obj = json_decode($json);
		$file = 'images/photo_'.$nameFile.'.jpg';
		$success = file_put_contents($file, base64_decode( $obj->{'json'} ));
   }
?>
