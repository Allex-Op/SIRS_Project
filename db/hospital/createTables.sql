CREATE TABLE IF NOT EXISTS users(
    user_id SERIAL,
    username VARCHAR(16),
    password CHAR(60),                      -- Size of bcrypt hash
    PRIMARY KEY (user_id)
);

CREATE TABLE IF NOT EXISTS token(
	id SERIAL,
	hash CHAR(64) UNIQUE,	               -- 64 hexa chars = 256 bits hash
	expiresAt BIGINT,
    PRIMARY KEY(id)
);

CREATE TABLE IF NOT EXISTS roles(
    role_id SERIAL,
    rolename VARCHAR(16),
    PRIMARY KEY(role_id)
);

CREATE TABLE IF NOT EXISTS employees(
    employee_id INT,
    role_id INT,
    PRIMARY KEY(employee_id),
    FOREIGN KEY(role_id) REFERENCES roles(role_id)
);

CREATE TABLE IF NOT EXISTS patients(
    patient_id SERIAL,
    name VARCHAR(32),
    age INT,
    diseases TEXT,
    treatment TEXT,
    PRIMARY KEY(patient_id)
);

CREATE TABLE IF NOT EXISTS labs(
    lab_id SERIAL,
    public_key TEXT,                         -- Installed manually in the system by the sysadmin
    PRIMARY KEY(lab_id)
);

CREATE TABLE IF NOT EXISTS tests(
    test_id SERIAL,
    patient_id INT,
    test_name VARCHAR(32),
    result VARCHAR(256),                    -- The test results will be the response from the lab, it can contain stuff like the data it was completed, 
                                            -- tests results like blood cells count etc, ...it will be this field that will be used to authenticate the message from the partner lab.
    test_state BOOLEAN DEFAULT FALSE,       -- The default state of a test will be FALSE, which means, awaiting results from a partner lab.
    lab_id INT,
    digital_signature TEXT,                 -- this digital signature authenticates only the 256 chars of the 'result' field, and is sent in the POST request from the lab to the hospital.
                                            -- The signature is stored as text because its received encoded in base64 (because its transported throught HTTP)
    PRIMARY KEY(test_id),
    FOREIGN KEY(lab_id) REFERENCES labs(lab_id),
    FOREIGN KEY(patient_id) REFERENCES patients(patient_id)
);
