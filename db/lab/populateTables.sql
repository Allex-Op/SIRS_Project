-- Used 'https://bcrypt-generator.com/' for the password encryption, as the API does not offer any registration method.
-- No register method implemented, therefore all users are directly inserted into the database.
INSERT INTO users(username, password) VALUES
    ('mrscientist', '$2y$12$PVDdLEPhaJRIqnh8j5wOcu3Y/ocCSqPvwUh6pF0AE.4a90jJjtDt6');   -- scientist
