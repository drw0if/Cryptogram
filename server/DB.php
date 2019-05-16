<?php

  class DB{
    private static $instance = null;

    private $dbUser = "";
    private $dbPasswd = "";
    private $dbName = "";
    private $dbHost = "";
    private $connection = null;

    public static function getInstance(){
      if (static::$instance === null) {
        static::$instance = new DB();
      }

      return static::$instance;
    }

    public function exec(string $query, array $args){
      static::getInstance();

      if($this->connection === null){
        return null;
      }

      try{
        $stmt = $this->connection->prepare($query);
        $stmt->execute($args);
        return $stmt->fetchAll(PDO::FETCH_ASSOC);
      }
      catch(PDOException $e){
        return null;
      }
    }

    private function __construct(){
      try{
        $this->connection = new PDO("pgsql:host=$this->dbHost;dbname=$this->dbName;port=5432",$this->dbUser, $this->dbPasswd);
        $this->connection->setAttribute(PDO::ATTR_ERRMODE, PDO::ERRMODE_EXCEPTION);
      }
      catch(PDOException $e){
      }
    }

    public function __destruct(){
      $this->connection = null;
      static::$instance = null;
    }

    private function __clone(){
    }
  }
?>
