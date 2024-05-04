DeropBox HLD

Requirements:
Functional Requirments: Upload, Download, share Doc
Non Functional Requirements: Scaleable, HighAvaiable, low latency(when i do some chang in file that change should reflect immeditely), Optimize data Transefer by minimize bandwidth, ACID

Estimation of Scale:
No of Users : 1 Billion
storege/ user is given = 20 GB
total space requied = 20 Billion GB

In Drop Box we have three layers:
Storage layers (where our file will store in Distributed Storage, Amazon s3, HDFS)
Medata Data Storage (Infformation of a file)
Synchronizer layer 

Storage Layer:
Suppose we have uplaod 1GB file 
Now if we use HDFS 2.0 the default chunk size is 128 MB
total chunks required is = 1 GB/ 128 MB = 1024 / 128 = ~ 8 chunks
The metadata table we maintain to store chunk information is known as the "NameNode server". It keeps mapping that chunks are present on which machine(data node) for a certain file. Say, 
for File 1, chunk 1 is present on machine 3. 
In HDFS, there will be only one name node server, and it will be replicated.

total files are stored in Google Derive Globally suppose 10 Billion
total storate = (10 Billion * 1 GB) /128  =  10,000,000,000 * 1024 MB/ 128 MB= ~80,00,00,00,000 chunks
Now i want those chunks meta for 10 years than and one erery chunks has around 100 byte
toal storage for chunks will be= 800 Billion * 10 * 100 byte = 8000 Billion * 100 Byte = 800000 TB 
we need to Shard the data in NameNode Server. or if i talk about the database we will use the MYSQL database to store the metadata of the chunks because 
this databsae is highly consistent.

Sharding key will be fileId.
we know that to make the distributed system reliable, we never store
data on a single machine; we replicate it. Here also, a chunk cannot be stored on a single
machine to make the system reliable. It needs to be saved on multiple machines. We will keep
chunks on different data nodes and replicate them on other data nodes so that even if a
machine goes down, we do not lose a particular chunk.

Suppose there is a client who wants to upload a large file. The client requests the app
server and starts sending the stream of data. The app server on the other side has a
client (HDFS client) running on it.
● HDFS also has a NamNode server to store metadata and data nodes to keep the actual
data.
● The app server will call the name node server to get the default chunk size, NameNode
server will respond to it ( say, the default chunk size is 128 MB).
● Now, the app server knows that it needs to make chunks of 128 MB. As soon as the app
server collects 128 MB of data (equal to the chunk size) from the data stream, it sends
the data to a data node after storing metadata about the chunk. Metadata about the
chunk is stored in the name node server. For example, for a given file F1, nth chunk - Cn
is stored in 3rd data node - D3.
● The client keeps on sending a stream of data, and again when the data received by the
app server becomes equal to chunk size 128 MB (or the app server receives the end of
the file), metadata about the chunk is stored in the name node server first and then
chunk it send to the data node.

Briefly, the app server keeps receiving data; as soon as it reaches the threshold, it asks the
name node server, 'where to persist it?', then it stores the data on the hard disk on a particular
data node received from the name node server

Download the File:
Similar to upload, the client requests the app server to download a file.
● Suppose the app server receives a request for downloading file F1. It will ask the name
node server about the related information of the file, how many chunks are present, and
from which data nodes to get those chunks.
● The name node server returns the metadata, say for File 1, goto data node 2 for chunk
1, to data node 3 for chunk 2, and so on. The application server will go to the particular
data nodes and will fetch the data.
● As soon as the app server receives the first chunk, it sends the data to the client in a
data stream. It is similar to what happened during the upload. Next, we receive the
subsequent chunks and do the same.

Update the file:
when we update somthing into the file first those changes will goto the HDFS and find the chuks that in which chunks we are updating the text.
as the chenge happend we update the metadata of the chuks immidately. Now if a another user want to see the changes than new update chuks will be notify to the another user.
those changes put in messageQueuse that will be Async like (Kafka, RabitQueue)



