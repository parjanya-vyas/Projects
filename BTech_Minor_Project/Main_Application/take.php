<?php
extract($_POST);
$handle = fopen("log.txt", "a");
	fwrite($handle, "Name: ");
   fwrite($handle, $st_first_name);
   fwrite($handle, "\t");
   fwrite($handle, $Last_name);
   fwrite($handle, "\r\n");
   fwrite($handle, "Gender: ");
   fwrite($handle, $gender);
   fwrite($handle, "\r\n");
   fwrite($handle, "Mobile number: ");
   fwrite($handle, $number2);
   fwrite($handle, "\r\n");
   fwrite($handle, "Email ID: ");
   fwrite($handle, $mail);
   fwrite($handle, "\r\n");
fclose($handle);
header("location: more_q.html");
?>
