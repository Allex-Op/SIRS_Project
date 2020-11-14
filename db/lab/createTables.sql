-- The labs has no use for the other tables (patients, roles...) as there will be only 1 role the "scientist" or whatever
-- person works in labs.
CREATE TABLE IF NOT EXISTS users(
    user_id GENERATED ALWAYS AS IDENTITY,
    username VARCHAR(16),
    password CHAR(60),                      -- Size of bcrypt hash
    PRIMARY KEY (user_id)
);

CREATE TABLE token(
	id INT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
	hash CHAR(64) UNIQUE,	               -- 64 hexa chars = 256 bits hash
	expiresAt BIGINT
);

-- There is also no tests table as the Lab API will automatically answer with mock data
-- everytime there is a request from the hospital to process some tests
