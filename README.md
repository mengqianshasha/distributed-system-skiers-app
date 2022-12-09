# Distributed System Skiers App

## Basics
### Microservices
Java Servlet: `ski-app` folder
Client sending POST requests: `ski-client` folder
Consumer that populate Redis database: `ski-consumer` folder

### Populate Data in Consumer
See in file: `/ski-consumer/src/main/java/ConsumerWorker.java`

### GET Requests in Servlet
##### GET `/resorts/{resortID}/seasons/{seasonID}/day/{dayID}/skiers`
See in file: `ski-app/src/main/java/servlet/ResortServlet.java`
\

##### GET `/skiers/{resortID}/seasons/{seasonID}/days/{dayID}/skiers/{skierID}`
See in file: `/ski-app/src/main/java/servlet/SkierServlet.java`
\

##### GET `/skiers/{skierID}/vertical`
See in file: `/ski-app/src/main/java/servlet/SkierServlet.java`
