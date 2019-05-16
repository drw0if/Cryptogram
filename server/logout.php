<?php

  if(strcasecmp($_SERVER["REQUEST_METHOD"], "POST") !== 0){
    http_response_code(404);
    exit();
  }

  if(!isset($_POST['username']) or !isset($_POST['token'])){
    http_response_code(400);
    exit();
  }

  $username = $_POST['username'];
  $token = $_POST['token'];

  require 'Models.php';
  $report = Queue::get(Array('username' => $username, 'token' => $token));

  if($report === null){
    http_response_code(500);
    exit();
  }

  if(count($report) === 0){
    http_response_code(401);
    exit();
  }

  $report[0]->remove();

  http_response_code(204);
  exit();
?>
