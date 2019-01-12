CREATE TABLE greeting (
  id INT IDENTITY(1,1) PRIMARY KEY,
  message VARCHAR(255),
  author_name VARCHAR(255),
  created DATETIME
);
