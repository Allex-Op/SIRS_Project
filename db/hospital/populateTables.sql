-- Used 'https://bcrypt-generator.com/' for the password encryption, as the API does not offer any registration method.
-- No register method implemented, therefore all users are directly inserted into the database.
INSERT INTO users(username, password) VALUES
    ('mrdoctor', '$2y$12$PPLKxZji2odOx/UHoUNnz.m4sRI0VsCfwqqwREz5rpRMFvdL104E.'),   -- password: doctor
    ('msnurse', '$2y$12$0QtlCeUu4ZHuOMcpjd3tAOzPs.AzFSA4ka.h01xnfcBcqfzSC5HLm'),    -- password: nurse
    ('mrjanitor', '$2y$12$.K3hjrANxX/Hp4ziaK9vJOu13H6hDH1pqpSSJwvlgUwClhL05dnd.');  -- password: janitor

INSERT INTO roles(rolename) VALUES
    ('doctor'),
    ('nurse'),
    ('janitor');

INSERT INTO employees(employee_id, role_id) VALUES
    (1, 1),
    (2, 2),
    (3, 3); -- e.g. janitor was the 3rd user to be created (id:3) and janitor was 3rd role to be created pair(user_id, role_id)

INSERT INTO patients(name, age) VALUES
    ('joão', 15),
    ('maria', 45);

INSERT INTO labs(public_key) VALUES
    ('-----BEGIN PUBLIC KEY-----
MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAxZjOcdjMUcO0wMnBpAcG
7ws6ZeDFDx0VW8XP3S+rNIFo0PoEOe0QZBFT0+FqBzuDNihsL2bHVNm3gqDVBElm
T/RhYOjI5Iv4FK4NCcIRyOe6cEBVk+Wh/IJBw2rJPZOduLG0VBA15r+F1kqY5pRN
lzTfmtc30dz5KImn2F/fTHfCBQMejVY1iGpKFeeRC0qqawG06EnRnIpf/Eb5aQa8
c+o/r2AQOk3RI02NyTBvRTZPCF5MohSiOFjSgbJStfflPKEtsEzu1TDbwbRUL+Mm
/Yh7uBK+s/EY1okko9793hVio8nVuKKzZlWxk6nks+0ra948lUzmq0D5VUJsmzBm
eQIDAQAB
-----END PUBLIC KEY-----
');

-- INSERT INTO tests(patient_id, test_name, result, test_state, lab_id, digital_signature) VALUES (); -- All info besides result & digital_signature is inserted by the Hospital API.

