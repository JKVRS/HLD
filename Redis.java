REDIS

Redis: we can use this Redis for different purporse like: Cache, DataBase, Message Broker.
It is most use cloud dataBase or also know as Amazon Elastic Cache.
Redis is In-Memory data store.(in the Primery Memory (RAM))
It will store data in key- value formate.
Redis by default support different-2 Data stracture.

PRO and CONS with the DB:
PROS: Fast Access and Fast Updates.
CONS: Primary Memory is not durable.(when system switch off memory get lost).
      Storage is low to store the data.
      Redis is not useful if we want to sotre large file like video , But if data modify very quickely than Redis is very good option.

Architracture of Redis:
Two Componenet : Redis Client , Redis Server.
both the Componenet can sit in same box or in different box but communication will happen b/w client and servre via Redis Protocol.

Redis Durability :
All the computation/live storge will happen in Redis server becuase this server is In-Memory but to make sure that data will be persist or not Redis has 
persist Mechanisam.
1. RDB :(Redis Data Backup):Redis give to take a snapshot of the data before the systam shut down. and that snapshot save is the disk where we allow it.
   DownSize : There is a chance to loss the data if RDB time is more.
   RDB Time : to take the snapshot of the data.
   RDB boot up is lower.

2. AOF:(Append Only File): Every Operation that is happing in the Redis the log of the Operation will append in the Disk so that while rebooting the system we can recreate the data.
      (Just Store all the command so that we can recreate the data).
    DownSize of AOF: while recreating the data there is again write opration so it will increase Latency.
                     AOF Boot up time is higher.

3. RDB+AOF : Rdis can use RDB and AOF simultinuously that will increase boot timing and is protect to loss of data.

Rewrite of AOF file:
There is no possibality to loss the data.

Master- Slave in Redis:
Redis allow us to create the repilca of its own so parent one is called as Master Redis server and another server calls as slave Reddis server.
whenever write happend we will copy write in all the slave in the sync/unsync.(By Defalut Redis works on ASync manner).
Advantage:
If parent is down than one slave should be up that will take palce of Master Redis.
Reads become faster.
Sync in Redis:
Master and slave are connecting with each ohter with the hlep network so master will keep sending a stream of command to slaves.
in these commands we will have only update commands.but there will some delta persent between master and slave.
but the problem is partition.(communication faild betweeen master and slaves ).
so slave will keep sending the ACIC to Master that we are alive.if any of slave doesnt send ACIC to  master than master will not send any write command to that perticular slaves.
when that dead slaves comes back up than :
That slave send the request to the master that request will call as partial resync.

Noob Problem in Redis:
Suppose we have master and slve and at some point of time master goes down and Devoops team has auto restart the the master on master and after some time our master will restart than
all the data might be lost in master and now slave would know that our master is start back than slave will send the request to sync the data at that time master will give few entries or no data to the slaves
than our all the salves will also lost data at that time.
Solution: Eitehr ensure that Persistance On or Stop Auto Restart of Master redis.

Redis Data Stracture:
Redis sotre the data in key- value formate, its not a plain key -value pair it has data starcute in K-V.
in Redis the Key always in String we called as Binary Safe String.(Means String will be convert in Binary foramte so decoding issue will not there).
Value : can have multiple data type: Stirng, LinkedList of String,Set of String for Unique Element,
In Redis must be only 512MB not more than that because its binary safe. if key are long the fetching the data form Redis will become slow.

Summary:
Redis - DB, Cache, Message Broker
Redis- Client and server
Redis Persistance:- AOF ,RDB
Redis Replication- Async , sync wait.
Noob Problem
Redis Data starcute
