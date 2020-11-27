INSERT INTO roles(rolename) VALUES
    ('doctor'),
    ('nurse'),
    ('janitor');

-- Used 'https://bcrypt-generator.com/' for the password encryption, as the API does not offer any registration method.
-- No register method implemented, therefore all users are directly inserted into the database.
INSERT INTO employees(username, password, role_id) VALUES
    ('mrdoctor', '$2a$10$wWvxgnTa95bZdQqoek2eNOSVr5XM5ZgapzC.ds2UhASp3YGxcJo4C', 1), -- password: doctor
    ('msnurse', '$2a$10$nCpZUx0s9XleyPuUVkMKxeqboSgD8uf3s7JNbu/f2lENnC25j8vRG', 2),  -- password: nurse
    ('mrjanitor', '$2a$10$bgrf87WsbjjVajKnOYBUMevEDCDoot8mR80wOdC7b4Q0MZaBh6tyi', 3); -- password: janitor

INSERT INTO patients(name, age, diseases, treatment) VALUES
    ('joão', 15, 'HIV,PNEUMONIA', 'AN APPLE A DAY'),
    ('maria', 45, 'COVID-19', '500g of Dishwasher');

-- Talvez ter uma store em vez de guardar os certificados assim
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

INSERT INTO tests(patient_id, result, lab_id, digital_signature) VALUES 
    (1, 'COVID-19: POSITIVE', 1, 'invalidsignature'); -- All info besides result & digital_signature is inserted by the Hospital API.

