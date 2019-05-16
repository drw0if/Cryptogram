<?php
  require "DB.php";

  $ddl = "CREATE TABLE queue (".
      "username character varying(100) NOT NULL," .
      "ip inet NOT NULL," .
      "port integer NOT NULL," .
      "token character(64) NOT NULL" .
      ");";

  $db = DB::getInstance();

  $db->exec($ddl, []);
?>

