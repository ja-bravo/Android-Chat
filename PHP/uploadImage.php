<meta charset="utf-8"> 

<?php
	// Esto es una imagen de ejemplo codificada en base64
	//$data = '{"json":"\/9j\/4AAQSkZJRgABAQAAAQABAAD\/2wBDAAEBAQEBAQEBAQEBAQEBAQEBAQEBAQEBAQEBAQEBAQEB\nAQEBAQEBAQEBAQEBAQEBAQEBAQEBAQEBAQEBAQEBAQH\/2wBDAQEBAQEBAQEBAQEBAQEBAQEBAQEB\nAQEBAQEBAQEBAQEBAQEBAQEBAQEBAQEBAQEBAQEBAQEBAQEBAQEBAQEBAQH\/wAARCAARABcDASIA\nAhEBAxEB\/8QAHwAAAQUBAQEBAQEAAAAAAAAAAAECAwQFBgcICQoL\/8QAtRAAAgEDAwIEAwUFBAQA\nAAF9AQIDAAQRBRIhMUEGE1FhByJxFDKBkaEII0KxwRVS0fAkM2JyggkKFhcYGRolJicoKSo0NTY3\nODk6Q0RFRkdISUpTVFVWV1hZWmNkZWZnaGlqc3R1dnd4eXqDhIWGh4iJipKTlJWWl5iZmqKjpKWm\np6ipqrKztLW2t7i5usLDxMXGx8jJytLT1NXW19jZ2uHi4+Tl5ufo6erx8vP09fb3+Pn6\/8QAHwEA\nAwEBAQEBAQEBAQAAAAAAAAECAwQFBgcICQoL\/8QAtREAAgECBAQDBAcFBAQAAQJ3AAECAxEEBSEx\nBhJBUQdhcRMiMoEIFEKRobHBCSMzUvAVYnLRChYkNOEl8RcYGRomJygpKjU2Nzg5OkNERUZHSElK\nU1RVVldYWVpjZGVmZ2hpanN0dXZ3eHl6goOEhYaHiImKkpOUlZaXmJmaoqOkpaanqKmqsrO0tba3\nuLm6wsPExcbHyMnK0tPU1dbX2Nna4uPk5ebn6Onq8vP09fb3+Pn6\/9oADAMBAAIRAxEAPwD816KK\nK\/gc\/wC0AKKKKACiiigAooooA\/\/Z\n"}';
	
	//Recojo el json y lo arreglo para que se quede tal y como se genero en android
	$data = $_REQUEST["json"];
	$data = str_replace ( "+" , " " , $data  );	
	$data = str_replace ( "SUMAR" , "+" , $data );
	
	$nameFile = $_REQUEST["nameFile"];
	
	// Guardo tal cual el json que me han enviado en un txt, por si luego quiero mandar el txt en vez de un jpg
	// NOTA: para visualizar este fichero en forma de imagen, puedo usar el fichero showImage.php.
	saveJSON($data , $nameFile); 

	// Ahora, en vez de guardar el json tal cual en un txt, genero un jpg fisico.
	createImage ($data , $nameFile);
	
   function saveJSON($data , $nameFile)
   { 
      $file = 'images/photo_'.$nameFile.'.txt';
      $success = file_put_contents($file, $data);
   }
   
   function createImage ($json , $nameFile)
   {
		header("Content-type: image/gif");
		$obj = json_decode($json);
		$file = 'images/photo_'.$nameFile.'.jpg';
		$success = file_put_contents($file, base64_decode( $obj->{'json'} ));
   }
?>
