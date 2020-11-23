CREATE TABLE IF NOT EXISTS roles(
    role_id SERIAL,
    rolename VARCHAR(16),
    PRIMARY KEY(role_id)
);

CREATE TABLE IF NOT EXISTS employees(
    employee_id SERIAL,
    username VARCHAR(16),
    password TEXT,                      -- Size of bcrypt hash
    role_id INT,
    PRIMARY KEY(employee_id),
    FOREIGN KEY(role_id) REFERENCES roles(role_id)
);

CREATE TABLE IF NOT EXISTS sessions(
	id SERIAL,
	token TEXT UNIQUE,	               -- 64 hexa chars = 256 bits hash
    employee_id INT,
    PRIMARY KEY(id),
    FOREIGN KEY(employee_id) REFERENCES employees
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
    result VARCHAR(256),                    -- The test results will be the response from the lab, it can contain stuff like the data it was completed, 
                                            -- tests results like blood cells count etc, ...it will be this field that will be used to authenticate the message from the partner lab.
    lab_id INT,
    digital_signature TEXT,                 -- this digital signature authenticates only the 256 chars of the 'result' field, and is sent in the POST request from the lab to the hospital.
                                            -- The signature is stored as text because its received encoded in base64 (because its transported throught HTTP)
    PRIMARY KEY(test_id),
    FOREIGN KEY(lab_id) REFERENCES labs(lab_id),
    FOREIGN KEY(patient_id) REFERENCES patients(patient_id)
);

