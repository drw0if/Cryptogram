<?php
  if(strcasecmp($_SERVER["REQUEST_METHOD"], "POST") !== 0){
    http_response_code(404);
    exit();
  }

  if(!isset($_POST['username']) or !isset($_POST['port'])){
    http_response_code(400);
    exit();
  }

  $port = (int)$_POST['port'];
  $username = $_POST['username'];

  if($port < 1 or $port > 65535){
    http_response_code(400);
    exit();
  }

  require 'Models.php';

  $check = Queue::get(Array('username' => $username));
  if($check === null){
    http_response_code(500);
    exit();
  }

  if(count($check) > 0){
    //duplicate user
    http_response_code(401);
    exit();
  }

  if(!empty($_SERVER['HTTP_CLIENT_IP']))
    $ip = $_SERVER['HTTP_CLIENT_IP'];
  elseif(!empty($_SERVER['HTTP_X_FORWARDED_FOR']))
    $ip = $_SERVER['HTTP_X_FORWARDED_FOR'];
  else
    $ip = $_SERVER['REMOTE_ADDR'];

  $u = new Queue($username, $ip, $port);

  $check = $u->save();

  if($check === null){
      http_response_code(500);
      exit();
  }

  echo json_encode(Array("token" => $u->token));

  http_response_code(201);
  exit();
?>
