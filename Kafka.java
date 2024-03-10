Apache Kafka:
It is like Communication system that helps different parts of a computher system to exchange data by publishing and subscribing to topic.
        Publish      
  sender   ->   Kafka   ->   receiver (Receiber get that msg when he subscribe the event).
(Producer)                    (subscriber/comsumer)
Example:
OLA Driver Location Update.
Zomato live food Tracking.
Notification Service.

Features:
High Throguput.
Fault Tolerance (Replication)
Durable
Scalable

Architecture:
1.Producer :(To produce the message)
2.
------------------------------------------
           Kafka Ecosystem    
 ----------------------------------|
 |   Kafka Cluster                 |
 |    -Broker-1                    |
 |      Topic-1                    |
 |        Partition-1              |
 |    -Broker-2                    |
 |                                 |
 |                                 |
 |                                 |
 |                                 |
 ----------------------------------
                  |
             Zookeeper

Data will store in Partition related to that topic. 
Ex: we have two topic login and deliverOrder so login related data will store in login Partition will the offset value like :0,1,2,
    and deliverOrder related data will store in another Partition.
Zooker handle the broker's' state.

3.Consumer :(Consume the message)

Insatallation :
Download kafka zip file 
Extract file
Insatall Zookeeper
start Zookeeper.
start Kafka Server

Use Case:
-To process payments and financial transactions in real-time, such as in stock exchanges, banks, and insurances.
-To track and monitor cars, trucks, fleets, and shipments in real-time, such as in logistics and the automotive industry.
-To continuously capture and analyze sensor data from IoT devices or other equipment, such as in factories and wind parks.
-To collect and immediately react to customer interactions and orders, such as in retail, the hotel and travel industry, and mobile applications.
-To monitor patients in hospital care and predict changes in condition to ensure timely treatment in emergencies.
-To connect, store, and make available data produced by different divisions of a company.
-To serve as the foundation for data platforms, event-driven architectures, and microservices

command to run the Zookeeper:
C:\kafka\k>bin\windows\zookeeper-server-start.bat config\zookeeper.properties

command to run kafka:
kafka server 9092 port.

Kafka with Console:
1. create new topic with kafka-topic
2. Produce message with kafka-Console-Producer
3. Consume the message with kafka-Console-Consumer

Commands:
1. Create a topic to store events.
- >bin\windows\kafka-topics.bat --create --topic user-topic-- bootstrap-server localhost:9092

2. write some event into topic:/Produce the Topic
- >bin\windows\kafka-console-producer.bat --topic user-topic --bootstrap-server localhost:9092

3. Read the Event:
-  bin\windows\kafka-console-consumer.bat --topic user-topic --from-bigning --bootstrap-server localhost:9092
 we can create multiple consumer.