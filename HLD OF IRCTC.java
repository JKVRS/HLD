HLD OF IRCTC
High Throughput.
High Consistency.
// 100 crores = 1 Billion =1,00,00,00,000
// 10 lakh = 1 Million
// BIGINT - 8 B
// Int - 4 B
// Double -8B
// Char - 2B
Features/Minimum Viable Product
1. User registration.--->Saperate User Authentication Service.
2. Search Train Features.
3. Seat Availabiliy
4. Seat Booking.
5. cancle Seat.
6. Payment Gatway.
7. Notification Services.

Estimation of Scale:- 
Suppose :
No of train : 10000 train /days
Boggie : 15 bogies/train
Seat in a Boggie : 70 seat/bogie

Total seat : 70*15*10000 =1,05,00,000 = 10 Million bookable Seat/day.

we can book seat for CURRENT date or till 120 days in future.
so total bookable seat in 120 days = 120 *1,05,00,000 = 1,26,00,00,000 = 1 Billion
1 Billion also will multiply with no of station in jounery :- 1,26,00,00,000*15 = 18,90,00,00,000 = 20 Billion

For Every Seat we want to store :
Train Id , Seat Id, Class, User Id, Bookning Status, Booking Id, Payment Id
8B          8B      1B      8B      1B                8B         8B
Total Bytes to store one booked Seat - 8+8+1+8+1+8+8 =42 Bytes = 50 Bytes
Totel storage required for 20 Billion Bookable Ticket = 20 Billion * 50 Bytes =3000 GB= 3 TB

A single machine can store 3-5 TB data 
so here we can say 3 TB data can be store into a single machine no sharing is required.
Entire Booking DB can fit inside a single machine.

User DB : 0.7 Billion user has account on IRCTC
For a single user suppose we store 200 Bytes data .
totla Bytes =700000000*200=1,40,00,00,00,000 =140 GB
User databse is also not required database sharding  

Note : From a data storage point of view Sharding is not required.

** Read Heavy , Write Heavy or Read+Write Heavy System **

System 1: fetch static content like : Train List, Total Seats, Train schedule
System 1 will be read Heavy. high Availabiliy.
System 2: Getting the Availabiliy Seats
System 2 will be Eventually Consistency, It just a catch of system 3., Read Heavy,High Availabiliy
System 3 : seat Booking System
System 3 will be highly Consistency, Read+write Heavy.


Actual Design Deep Dive :-
API :
1. getTrainList(Src, Dest, date)
2. getNumberOfAvailableSeat(TrainId,src,dest,date,class)
3. bookSeats(accessToken,userId,TrainId,src,dest,date,class,noOfSeat,bearthPref,PessengerList)

Problem 1. Consistency
while booking the ticket than 
step:1 To check if the seat is avaliable
step:2 Book the Seat
step:3 Return the seat 
all three step should be follow Automacity property.
Best data base that has ACID property is SQL
and it is also will be best becuase we are using 1 machine for one Service.
choice of Data is :- SQL

Problem 2. Tatkal
Throughput is very high
In Tatkal time so many request will come up on DB , but here we are using 
only one db for booking service because of its ACID property, but single 
DB can not handle so many request at Tatkal time.
So we have to shard the database based on Train Id.we have to sahrd th DB 
because of Throughput Requirments.
