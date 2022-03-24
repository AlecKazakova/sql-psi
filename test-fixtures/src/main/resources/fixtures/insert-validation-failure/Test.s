CREATE TABLE test (
  column_1 INTEGER NOT NULL,
  column_2 INTEGER NOT NULL
);

INSERT INTO test
VALUES (1, 2, 3);

INSERT INTO test (column_1, column_2, column_3)
VALUES (1, 2, 3);

INSERT INTO test
SELECT * FROM (VALUES (1, 2, 3));

WITH temp_table AS (
  SELECT * FROM (VALUES (1, 2, 3))
)
INSERT INTO test
SELECT * FROM temp_table;

INSERT INTO test
SELECT * FROM temp_table;