# Car Reservation API 
Welcome to application Car API for renting car

## Requirement

The goal of the application is to reserve cars for upcoming rides.
As a user of the system, you can add cars where each car is represented by its make, model and unique identifier following the pattern “C<number>”.
Besides, adding a new car, a user can also update one, remove one, or see all the cars (no pagination needed).
The last step in the flow is the ability to reserve a car for a ride at a certain time and duration. The reservation can be taken up to 24 hours ahead and the duration can take up to 2 hours. The system should find an available car, store the reservation if possible and give back a response with the details. The user can also see all the upcoming reservations by calling another dedicated endpoint.
The communication is only via API in the JSON format and the data can be stored just in the memory. The application doesn’t need to implement a concept of users (data is shared).
The solution should clearly represent your architectural knowledge and follow the best practices overall including loose coupling and automated testing.

Technical Requirements:
* Java-> used Java 17
* Spring Boot -> used Spring boot 3.0.6 default from Spring Initializr
* Automated tests -> used JUnit5 and Integration tests
* REST API + documentation -> Used Rest API to exposes endpoints and documentation is here in README file
* Docker -> app is dockerized (there is latter explanation how to start form docker app)

### Guides for Docker

In the root of the project execute commands:

* `docker build -t reservation .`
  This will create docker image form application
* `docker run -p 8080:8080 reservation`
  This will start docker container on port 8080 

### Guides for DB

The additional logic with an embedded H2 database is added. There are scripts `schema.sql` and `data.sql` which will be executed at the start of the application.
With this addition, we have already data to show, and there is no hardcoded logic in the memory of the application.

The access to db:
* url `http://localhost:8080/h2-console/`
* JDBC URL: `jdbc:h2:mem:testdb`
* user: `sa`
* password: there is no password (leave it empty)


### Endpoints Documentation

We have two different parts of the application Car API and reservation API:

#### CAR API

* [http://localhost:8080/api/cars](http://localhost:8080/api/cars) with the GET method we get all cars from DB
* [http://localhost:8080/api/cars](http://localhost:8080/api/cars)  with POST method we can create new car 
Here we have logic that if customer use invalid id (something different form C + 8 digits), 
system will recognize and generate new car id instead of one that customer use, and will return new car object with generated id
* [http://localhost:8080/api/cars](http://localhost:8080/api/cars) with the PUT method instead carId we need to use real carId like C12345678, 
if system don't find car in DB it will return 404 (not found status), if finds it will update. There are several validations.
* [http://localhost:8080/api/cars/carId](http://localhost:8080/api/cars/carId) with the DELETE method  is same sa for update, system deletes car

#### Reservation API

* [http://localhost:8080/api/reservations](http://localhost:8080/api/reservations) with the GET method we get all reservations from DB
* [http://localhost:8080/api/reservations](http://localhost:8080/api/reservations) with POST method system will try to reserve a slot for renting a car,
  in case there is a problem, a message will direct the customer to documentation, to check

For POST and PUT methods in the Car API system expect a body with JSON format like this
example:
` {
"id": "C12345678",
"producer": "Zastava",
"model": "128"
}`

POST in Car API method will not update existing data in DB, only will create new, ones if the customer uses an invalid id,
the system will generate a new one for him/her, if the customer use id of the existing car, the system will again generate
new id and save the car in DB, and return data of the new car

If values (model and producer are empty), Exception will be thrown, system will return BAD_REQUEST (400) with appropriate message 

DELETE in CAR API if we want to delete valid cars, all reservations that

I didn't validate the names of the producer or model (it is possible to add a car with producer `!l56933` and model `!#$%^`),
didn't want to go into so much in detail, especially because this is an open endpoint without any security

For the POST method in the Reservation API system expect a body  with JSON format like this
example:
`{
"start": "2023-05-14T18:00:00",
"duration": "120"
}`

I use validation at this time like duration is valued in minutes, and it can't be equal to 120 (the requirement is less than 2h for reservation),
and more and equal than 30, from a business perspective, we as the company do not find it meaningful to rent a car for less

The system expects an int value for the duration, so `25.5` will not be excepted (serialization will fail), `abc` as well...

Start attribute for Time using this format: 2023-05-14T18:00:00

C<number> validation is changeable it depends on the form 3 property in the `application.property` file:
* `car.id.pattern` This is EL and at the moment is set to accept the C letter and 8 digits after
* `cad.id.prefix.letter`= This is the prefix letter which will be used, it is set to C, but it can be different (ex: B, D...)
* `cad.id.number.of.digits`= This is the number of digits, at the moment it is 8 

### Postman colection

In resources, there is a folder postman where you can find exported Postman collection , for easier testing (with prepared data)
you can import that file and just execute calls (and play with it)
file: `resources/Car API.postman_collection.json`

