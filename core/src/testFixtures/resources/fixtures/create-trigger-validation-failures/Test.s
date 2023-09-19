CREATE TABLE test (
  _id INTEGER NOT NULL
);

CREATE TRIGGER trigger_1
BEFORE INSERT ON test
BEGIN
  UPDATE test
  SET _id = old._id;
END;

-- valid.
CREATE TRIGGER trigger_2
BEFORE UPDATE ON test
BEGIN
  UPDATE test
  SET _id = old._id;
  UPDATE test
  SET _id = new._id;
END;

CREATE TRIGGER trigger_3
INSTEAD OF DELETE ON test
BEGIN
  UPDATE test
  SET _id = new._id;
END;

CREATE TRIGGER trigger_4
AFTER DELETE ON test
BEGIN
  WITH temp_table AS (
    VALUES (1, 2, 3)
  )
  SELECT *
  FROM temp_table, old;
END;

-- valid.
CREATE TRIGGER trigger_5
INSTEAD OF INSERT ON test
BEGIN
  INSERT INTO test (_id)
  VALUES (new._id);
END;