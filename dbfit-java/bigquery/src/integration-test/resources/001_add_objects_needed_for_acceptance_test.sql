CREATE TABLE users (
  name VARCHAR(50), 
  username VARCHAR(50),
  userid int primary key generated by default as identity
)
;

CREATE PROCEDURE MAKEUSER()
LANGUAGE SQLSCRIPT 
SQL SECURITY INVOKER AS
BEGIN
   INSERT INTO USERS VALUES ('user1','fromproc');
END;
