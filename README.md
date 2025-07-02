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

## Getting Started

The [Getting Started](https://vaadin.com/docs/latest/getting-started) guide will quickly familiarize you with your new
Qa Proyecto Final implementation. You'll learn how to set up your development environment, understand the project 
structure, and find resources to help you add muscles to your skeleton — transforming it into a fully-featured 
application.

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

## Migration and Post Deployment Plan 

After the project has been tested with unit tests, integration tests, end to end tests and converage testing ect it should be ready for release / production. The basic idea is to monitor how the users respond to the program and make any fixes if necessary or when bugs are found.

For upgrades or adding new versions of the dependencies we follow the documentation and any possible conflics between versions and make changes and addresss them in a testing enviroment. We retest with the tests that have been done in the past and add any new ones we need and fix any that have been broken or obselete. 

Any new additions to the database will be addressed with a new version of the database using flyway.

Slow rollouts to the more dedicated users as well as sustaining good comunication and feedback with them is essential before full deployment.
   
