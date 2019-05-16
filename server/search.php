<?php
  if(strcasecmp($_SERVER["REQUEST_METHOD"], "POST") !== 0){
    http_response_code(404);
    exit();
  }

  if(!isset($_POST['username']) or !isset($_POST['token'])){
    http_response_code(301);
    exit();
  }

  if(empty($_POST['username']) or empty($_POST['token'])){
    http_response_code(301);
    exit();
  }

  $username = $_POST['username'];
  $token = $_POST['token'];

  require 'Models.php';

  //check for valid token
  $check = Queue::get(['token' => $token]);

  //check for server error
  if($check === null){
    http_response_code(500);
    exit();
  }

  //Unauthorized if invalid token
  if(count($check) === 0){
    http_response_code(401);
    exit();
  }

  //do search stuff
  $report = Queue::get(['username' => $username]);

  //check for server error
  if($report === null){
    http_response_code(500);
    exit();
  }

  if(count($report) === 0){
    http_response_code(404);
    exit();
  }

  $ans = [];

  if(count($report) > 0)
    $ans = [$username => $report[0]->ip, 'port' => $report[0]->port];

  echo json_encode($ans);

  http_response_code(200);
  exit();
?>
