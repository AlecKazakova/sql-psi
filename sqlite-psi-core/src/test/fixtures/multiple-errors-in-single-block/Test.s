CREATE TABLE test (
  _id INTEGER NOT NULL PRIMARY KEY
);

SELECT column1, column2
FROM test
JOIN test2
JOIN (
  SELECT _id
  FROM test
  WHERE column3 = 'What is up homies'
)
ORDER BY cheetos;