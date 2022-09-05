INSERT INTO users(username, password, enabled)
VALUES ('user', '{noop}user123', true),
       ('admin', '{noop}admin123', true)
;

INSERT INTO authorities(username, authority)
VALUES ('user', 'ROLE_USER'),
       ('admin', 'ROLE_ADMIN')
;