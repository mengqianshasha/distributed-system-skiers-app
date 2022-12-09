# Distributed System Skiers App

## Basics
### Microservices
* `ski-app` folder: Java Servlet <br>
* `ski-client` folder: Client sending POST requests <br>
* `ski-consumer` folder: Consumer that populate Redis database
<br/><br/>

### Populate Data in Consumer
See in file: [/ski-consumer/src/main/java/ConsumerWorker.java](https://github.com/mengqianshasha/distributed-system-skiers-app/blob/main/ski-consumer/src/main/java/ConsumerWorker.java)
<br/><br/>

### GET Requests in Servlet
##### GET `/resorts/{resortID}/seasons/{seasonID}/day/{dayID}/skiers`
See in file: [/ski-app/src/main/java/servlet/ResortServlet.java](https://github.com/mengqianshasha/distributed-system-skiers-app/blob/main/ski-app/src/main/java/servlet/ResortServlet.java)
<br/><br/>

##### GET `/skiers/{resortID}/seasons/{seasonID}/days/{dayID}/skiers/{skierID}`
See in file: [/ski-app/src/main/java/servlet/SkierServlet.java](https://github.com/mengqianshasha/distributed-system-skiers-app/blob/main/ski-app/src/main/java/servlet/SkierServlet.java)
<br/><br/>

##### GET `/skiers/{skierID}/vertical`
See in file: [/ski-app/src/main/java/servlet/SkierServlet.java](https://github.com/mengqianshasha/distributed-system-skiers-app/blob/main/ski-app/src/main/java/servlet/SkierServlet.java)
<br/><br/>

##### Redis client
See in file: [/ski-app/src/main/java/redis/RedisClient.java](https://github.com/mengqianshasha/distributed-system-skiers-app/blob/main/ski-app/src/main/java/redis/RedisClient.java)
