CREATE TABLE test (
  some_column INTEGER NOT NULL
);

SELECT *
FROM test
JOIN (SELECT some_column AS other_column FROM test) USING (some_column);
