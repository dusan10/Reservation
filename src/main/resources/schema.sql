CREATE TABLE car
(
    id        VARCHAR(255),
    producer  VARCHAR(255),
    model     VARCHAR(255),
    PRIMARY KEY (id)
);

CREATE TABLE reservation
(
    id                VARCHAR(36) NOT NULL,
    car_id            VARCHAR(255) NOT NULL,
    reservation_start DATETIME NOT NULL,
    reservation_end   DATETIME NOT NULL,
    FOREIGN KEY (car_id) REFERENCES car (id)
);
