CREATE TABLE users (
  name VARCHAR(50), 
  username VARCHAR(50)
)
;

CREATE OR REPLACE PROCEDURE MAKEUSER()
RETURNS INTEGER
LANGUAGE NZPLSQL AS
BEGIN_PROC
   BEGIN   EXECUTE IMMEDIATE 'INSERT INTO USERS VALUES (''user1'',''fromproc'')';  RETURN 1; END;  
END_PROC;
