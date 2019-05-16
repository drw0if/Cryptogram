<?php
  require "DB.php";

  class Queue{
    /*Static variables*/
    private static $db = null;

    /*Static methods*/
    private static function getDB(){ static::$db = DB::getInstance(); }

    private static function generateToken(){ return bin2hex(random_bytes(32)); }

    public static function get(array $args = []){
      static::getDB();

      if(!isset($args['username'])) $args['username'] = '%';
      if(!isset($args['token'])) $args['token'] = '%';

      $query = 'SELECT * FROM queue
        WHERE username LIKE :username
        AND token LIKE :token';

      $ans = static::$db->exec($query, $args);

      if($ans === null)
        return null;
      $record = [];
      foreach($ans as $r)
        array_push($record, new Queue($r['username'], $r['ip'], $r['port'], $r['token']));
      return $record;
    }

    /*Proper methods*/
    public function __construct($username, $ip, $port, $token = NULL){
      $this->username = $username;
      $this->ip = $ip;
      $this->port = $port;
      $this->token = ($token === NULL) ? Queue::generateToken() : $token;
    }

    /*Insert this instance into the database*/
    public function save(){
      static::getDB();

      $query = 'INSERT INTO queue(username, ip, port, token) VALUES(?, ?, ?, ?)';
      $params = [$this->username,
                  $this->ip,
                  $this->port,
                  $this->token];

      return static::$db->exec($query, $params);
    }

    //remove this instance from the database
    public function remove(){
      static::getDB();

      $query = "DELETE FROM queue WHERE username = ? AND ip = ? AND port = ? AND token = ?";
      $params = [$this->username,
                  $this->ip,
                  $this->port,
                  $this->token];

      return static::$db->exec($query, $params);
    }
  }
?>
