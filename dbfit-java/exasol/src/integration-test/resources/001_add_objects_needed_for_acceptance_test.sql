OPEN SCHEMA user1;

CREATE TABLE users (
  name VARCHAR(50), 
  username VARCHAR(50),
  userid int identity
)
;

CREATE SCRIPT MAKEUSER() AS 
    query([[INSERT INTO USERS VALUES (:x, :y)]], {x='user1', y='fromproc'})
;
