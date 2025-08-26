# Qa Proyecto Final README

- [ ] TODO Replace or update this README with instructions relevant to your application

To start the application in development mode, import it into your IDE and run the `Application` class. 
You can also start the application from the command line by running: 

```bash
./mvnw
```

To build the application in production mode, run:

```bash
./mvnw -Pproduction package
```

## test playwrite
```bash
npm install -D @playwright/test --legacy-peer-deps

npx playwright test
```

## Getting Started

The [Getting Started](https://vaadin.com/docs/latest/getting-started) guide will quickly familiarize you with your new
Qa Proyecto Final implementation. You'll learn how to set up your development environment, understand the project 
structure, and find resources to help you add muscles to your skeleton — transforming it into a fully-featured 
application.

## Project Plan

### Objectives

#### General Objective: 
Implement an inventory management system that meets the functional requirements, applying software quality standards and ensuring maintainability.

#### Specific Objectives:

Implement CRUD functionalities for products and suppliers.

Develop a user and role management module.

Incorporate a history of changes in product stock.

Apply different types of quality testing to ensure reliability and security.

Deliver technical and functional documentation to support the use and maintenance of the system.


The project is focused on an inventory management system with high levels of Quality Assurance.
It must implement methods that let the user add, update, view and delete products from its catalog as well as be notified when the stock is low to know what to products to restock.
Behind this system there must be users that can access specific parts of the app and are prevented from others. Some examples of posible users are Admins with access to all the system and user data and a product viewer that can see the full catalog but not interact with it.
DTOs will be used for communicating with react to keep higher levels of security and discretion.

This projects foundation is set on a Spring Boot backend with a react frontend and a rest API connecting them both.
Junit will be used for unit tests
Cucumber will be used for integration testing.
Playwright for end to end testing
Flyway for database version management
Docker for containerization.
The database will be MySQL

The key components / requierments of the program are the following.
- Management of Product (Create, update, view, delete)
- Management of Supplier (Create, update, view, delete)
- User Management (Create, update, view, delete)
- Role Management (Create, update, view, delete)
- Permit Management (View)



## Risk Assesment

1. Security issues involving API access.
  - Fix: create tests on API calls to make sure the appropriate users are able to make calls and prevent unverified ones.

2. Issues integrating Spring Boot Backend with React Front End
   - Fix: Investigate appropriate implementation of Spring API, document and create a standard API call procedure / structure. Test for stable communication

3. Conflicts from working in a team.
   - Fix: Always make sure to be working on a separete branch and double check updates on the project before pushing code to the repository.
  
4. Unneseary testing
   - Fix: Focus testing efforts on points of confliction / weakness as well as the requierments and rules the project needs to follow. Then keep going with coverage testing, but to use time more efficiently focus on the main classes, services and UI. Use flyway for ease and consistency of tests.
  
5. Unclear Requierments
   - Fix: Sit down and analize precisely what the requierments for the client really are. In this case the main mission is to provide the client with a program that lets them manage and keep track of their inventory. So every step we take needs to lead to this mission and every addition needs to somehow lead to this goal.
  
6. Bad time management.
   - Fix: Consistancy is key, working constantly in the tasks at hand will lead to better and more efficient use of time. With jira we can define the tasks we must complete and focus on one thing at a time.
  
7. Lack of knowledge in specific fields.
   - Fix: Find external help, be that from documentation, other individuals or similar help to better understand the tools we have at hand and streamline the learning and developing expirience.
  
8. version management / conflicts between vesions.
   Fix: Using the adequate tools to deal with changes in the database squema are the key to prevent future problems with future databases and posible migrations. The use of graddle is key for this project since we can know exactly what versions of our dependencies we need and work well for our projects, finally using containers to run our applications lets us be able to not just run the projects in any machine or configuration but also have a stable and always working version of the software we can be confident in.

## Requierments:

### Functional Requirements:
#### Authentification and Authorization:
- The system must have 3 default roles, user, employee and admin where user/visitor must only be able to see products. Employee must be able to create, edit and view but not delete products and view and interact with other related information. And admin must have full control over the system.
- The system must include a secure login mechanism.
- The system must verify permissions before executing any critical action.

#### Product Management:
- The system must allow authorized users to create, edit, view and delete products, specifying or specified with name, description, price, and stock, minimum stock and supplier.
- The system must store and display a history of stock changes for each product.

#### Supplier Management:
- The system must allow authorized users to create, edit, view and delete suppliers, specifying or specified with specifying name, contact, and relevant information.
- The system must allow users to view the list of registered suppliers.

#### User and Role Management
- The system must allow administrators to manage users (create, edit, delete).
- The system must allow administrators to assign roles with different access levels (administrator, employee, visitor).
- The system must restrict available functionalities according to the authenticated user’s role.

#### History and Reports:
- The system must record stock changes for each product, including date, time, and responsible user.
- The system must allow querying the history filtered by product, date, or user.

### Non-Functional Requirements:
#### Usability:
- The interface must be intuitive, responsive, and easy to use for non-technical users.
- The system must be accessible from modern browsers (Chrome, Firefox).

#### Security:
- Passwords must be stored using a secure hash.
- The system must implement role-based access control.
- System must use some kind of Authentification method (JWT)
- System must not display passwords from other users

#### Maintainability:
- The system must include unit, integration, and end-to-end tests.
- The system must allow easy incorporation of new features.
- System must implemente workflows do ease the testing and incorporation of new features and to prevent deployment issues

  
## Tecnologies Used:
- Database: My SQL
- Backend: Spring Boot
- Frontend: Vaadin
- Auditing: Hivernate Envers
- DB Migration: Flyway
- Monitoring and Observability: Prometheus, Grafana, Open Telemetry
- ContainerL Docker
- Authentification: JWT
- Unit Tests: Junit
- Integration Tests: Cucumber
- End to End Tests: Playwright
- Security Tests: Qodana, Snyk, OWASP ZAP
- Stress Test: Jmeter
- Testing Pipeline: Github Workflow
- Regresion Testing: Junit + Cucumber + Playwright Tests



## Documentation:

### Testing Guide:

There are 3 main kinds of tests to be aware of when adding or changing app features, unit, integration and end to end.
If the feature changes are related to services, then unit tests need to be adjusted.
If changes to the api are made then cucumber tests must be checked and revised
If frontend changes then revise playwright.
Adding tests to all of these is recommended, the general idea is for each service of mayor area to have its own tests, example all tests related to product are in one class, the same is for supplier and user.
Doing regression testing by running the verification/test command in graddle is very important to do before pushing to your own branch and even more important to dev.
After pushing to dev, make sure that all tests passed, even if they pass in one machine doesnt mean they will pass in github.

Some extra advise for playwright tests, using codegen is not a bad idea as a helper, but playwright seems to be very brittle when testing with a vaadin application. Its recommend to add ids to all of you vaadin components to make the playwright tests a lot easier later. Using other Forms and Pages as a template is recommend.

If running tests locally, playwright must need the program running locally as well as its testing database. Inside the tests folder you will fine a docker compose file with code for a test database, simply go to this directory and use docker compose up. If any changes are done to flyway its recommmended to run docker compose down --volumes and subsequently docker compose up and restart the application. This will restart the container with a fresh one.


## Migration and Post Deployment Plan 

After the project has been tested with unit tests, integration tests, end to end tests and converage testing ect it should be ready for release / production. The basic idea is to monitor how the users respond to the program and make any fixes if necessary or when bugs are found.


For upgrades or adding new versions of the dependencies we follow the documentation and any possible conflics between versions and make changes and addresss them in a testing enviroment. We retest with the tests that have been done in the past and add any new ones we need and fix any that have been broken or obselete. 


Any new additions to the database will be addressed with a new version of the database using flyway.


Slow rollouts to the more dedicated users as well as sustaining good comunication and feedback with them is essential before full deployment.



Task	SCRUM-25	
Document Requierments

MIGUEL JOSÉ NOBOA HURTADO	MIGUEL JOSÉ NOBOA HURTADO	Medium	Done	Done	02/Jul/25	26/Aug/25	 
Task	SCRUM-24	
Write the Project Plan

MIGUEL JOSÉ NOBOA HURTADO	MIGUEL JOSÉ NOBOA HURTADO	Medium	Done	Done	02/Jul/25	26/Aug/25	 
Task	SCRUM-23	
Test JWT tokens with users

Unassigned	MIGUEL JOSÉ NOBOA HURTADO	Medium	Done	Done	02/Jul/25	26/Aug/25	 
Task	SCRUM-21	
Test Product Frontend

MIGUEL JOSÉ NOBOA HURTADO	MIGUEL JOSÉ NOBOA HURTADO	Medium	Done	Done	02/Jul/25	26/Aug/25	 
Task	SCRUM-20	
Test Supplier Frontend

MIGUEL JOSÉ NOBOA HURTADO	MIGUEL JOSÉ NOBOA HURTADO	Medium	Done	Done	02/Jul/25	26/Aug/25	 
Task	SCRUM-15	
Create Frontend Product

Carlos Eleazar Peterson	MIGUEL JOSÉ NOBOA HURTADO	Medium	Done	Done	02/Jul/25	26/Aug/25	 
Task	SCRUM-14	
Create Frontend Supplier

Carlos Eleazar Peterson	MIGUEL JOSÉ NOBOA HURTADO	Medium	Done	Done	02/Jul/25	26/Aug/25	 
Task	SCRUM-13	
Test Service User Unit Test

MIGUEL JOSÉ NOBOA HURTADO	MIGUEL JOSÉ NOBOA HURTADO	Medium	Done	Done	02/Jul/25	26/Aug/25	 
   
