Car Rental System

Requirments:
User can serch the car based on the location and time period.
All car should displaay that are avaliable in that perticular location.
User can book 2 car at a time.
All the car info should be display.
Booked car should not be visiable to anyone.
User can cancle their booking before pick time.
User can update profile.
Price should be dipaly based on per hour.
Owner can add and remove his can on plateform.


Entity:
User, Car

Class User{
   userid,userName,userAddress,userMobile,userEmail,userDrivingLicenceNumber,userPassword,userType,List<Car>
}
Note :- User and Car -> 1:m 
Class Car{
    carId,CarNumber,CarDescripion,CarStatus, CarType,city
}
Class Booking{
   BookingId
   Car
   User
   Payment
   CarResntStartTime
   CarRentEndTime
   CarPrice
   
}
Class Payment{
    PaymentId
    PaymentLink
    PaymentType
    PaymentStatus
    amount
}


    
    /signup
    /login
    /getAllCars
    /serachCar
    /getCarDetails
    /bookCar   /cancleCar
    /Payment
    /viewBookingDetails

Class MianClass{
    
    

}


/**
 * HLD OF ZOOM CAR
 */
Features
1. User registration  ---->Authentication Service
2. Search the car  ----->Booking service
3. Book the car
4. Cancle the car
5. Payment      ----->Payment Service

Estimation Of Scale:

1. For User Service 
Suppose no of user is - 1000000 - 1 Million
Store the User Data 
User ID, Name, Address, Licence Number, email, PhoneNumber, password
4        20*2, 100*2,    10*2,         20*2,   8,           10*2
total = 4+40+200+20+40+8+20 = 332 Bytes

total bytes for all the user = 1000000*332=332000000 bytes = .0001 Billion * 332 bytes =.0332 GB
suppose One machine can store the 3-5 TB data
so to store the user data we dont need data sharding

Basically when user register on the portal than he could see data will be write in databse so user service will be write heavy.
We can use SQL/MogoDB because the data is stracture,and if some image column are their in user's' info than we can use MogoDb.

2. Booking Service =

No of avialbe car on zoom plateform per location :- 1000
No of location where zoom car is availabe : 30
totel no of availabe cars = 1000*30 =30000

one car can be book for future 6 month =30*6=180 days 
one car can be avilabe minimun 8 hr = 180*8= 1440
toatl avilbility of a car = 30000*1440= 4,32,00,000 hr/car  = 43100000%3600 sec/car  =0.00432 billion

data storage 
bookId,UserId,CarId,PaymentId,Address,startDate,EndDate
4      4       4     4        20*2     8          8 
total bytes = 4+4+4+4+40+8+8=32+40= 100
 total data store per hr =100 byte * 0.00432 billion=0.432 Gb = 0.00432 TB

 No sharding is required for the databse 

 *** Read Heavy , Write Heavy or Read+Write Heavy System **

 For booking service to book the car when we serach car it will read the data from databse
 but before book the car it will show all the available cars 
 to search the avialble car : Read Heavy + High Avilabiity 
 to book the car : Eventually Consistent, wirte Heavy

******* DB Chocie for Booking service *******
book the car should be follow Automicity 
The data is stractured 
we can choose SQL.
                        
******* Actual Design Deep Dive *************
signUpUser(RequestBody User user)
logiIn(username,password)
SearchCar(location,startDate,endDate)
BookCar(userId,CarId,startDate,EndDate,paymentId)

****** Spike *********************************
In this dsign there less peak days but still we can consider some peak days
since the data storage is very less for booking the car so if sike happens than we dont need to shard the data.
because one machine can handle 3-5 TB data.
 




