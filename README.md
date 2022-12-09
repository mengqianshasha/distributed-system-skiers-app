# Distributed System Skiers App

## Basics
### Microservices
Java Servlet: `ski-app` folder <br>
Client sending POST requests: `ski-client` folder <br>
Consumer that populate Redis database: `ski-consumer` folder
<br/><br/>

### Populate Data in Consumer
See in file: `/ski-consumer/src/main/java/ConsumerWorker.java`
<br/><br/>

### GET Requests in Servlet
##### GET `/resorts/{resortID}/seasons/{seasonID}/day/{dayID}/skiers`
See in file: `ski-app/src/main/java/servlet/ResortServlet.java`
<br/><br/>

##### GET `/skiers/{resortID}/seasons/{seasonID}/days/{dayID}/skiers/{skierID}`
See in file: `/ski-app/src/main/java/servlet/SkierServlet.java`
<br/><br/>

##### GET `/skiers/{skierID}/vertical`
See in file: `/ski-app/src/main/java/servlet/SkierServlet.java`
<br/><br/>

##### Redis client
See in file: `/ski-app/src/main/java/redis/RedisClient.java`
