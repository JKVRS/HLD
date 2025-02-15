HLD Of Trading Application ( Zerodha, Upstox)

Requirment :
1. User can signup and signup
2. User can link their bank account
3. User can edit their profie
4. Create and manage trading account
5. Link bank account for fund transer
6. Real time stock price and market index
7. Historical data for stock
8. Candelstick chart and graph
9. Top Gainer , losser and top active stock
10. Order book for stock and complete it
11. Modify and cacnle Order
12. View portfolio
13. Calculate profit loss for each stock and Calculate overall gain and loss
14. Trade history and ladgure report
15. Order execution , or notify for any IPO

---------------------------------------------------------------------------------------------------------------------------------------------------------------------

For this design i will focus on key Features like User managmenet, Account management, Order management, Notification service, Real Time market, Portfolio management
along with i will focus on scalbility, Relibilaty , DataBase, sharding and peek time etc.

So as i can see in the Requirments this applicaiton will be a microservice base application so that all services in the application will work idependalty.
for example if one microservice become failed then whole application wont be impacted

Microservice:
User service
Account service
Order Management service
Portfolio Management service
Real Market service
Notification service

FOr tradding application we can use any language java,pyhon or GoLang . But i want to prefer 2 language that can we very helpful for the application 

Conclusion: 
Language: Java/ GoLang/ PL-SQL
DB: SQL, MongoDB, TimescaleDB, S3 databse
Caching : Redis
Cloud: AWS
MonitoringTool : Splunk, Dynatrace, DataDog

----------------------------------------------------------------------------------------------------------------------------------------------------------------------

=> Estimation : Suppose there is 10 million user for 10 years(1000000*10 = 1,00,00,000)

1. User Data:  (64 bit Data we are using)  for 10 years
UserName    UserMobile   UserEmail       UserPAN      UserDOB
  20B         8B           30B             10B           8B    = ~ 80B data/user
Total  = 80B * 10000000 *10 = 80byte * (100000000/100 crore) = 80 byte *0.1 Billion = 8 GB (Excluding index, Metadata, Encryption, Additional fields)
Assume 30-40% overhead data would be there then total size of data is = (80*.3) ~ 10-12 GB data

2. Account Data:
AccountNumber      UserName    AccountType     IFSCCode 
10B                 50B          20B            10B  = ~ 100 byte
suppose a user linked 5 bank account 
total data = 500 byte/user
10 million user = 1,00,,00,000 * 500 byte = 0.1 Billion * 500 byte = ~50 GB (Excluding Index, Metadata, Encryption)
Assume 10-20 % overhead data would be there then = ~ 60GB data 


3. Order Management Data: 
StockID   Userid     stockName    StockCurrentPrice   StockQuantity     StockBuyingPrice    StockSellingPrice   StockBuyingDate     StockSellingDate    TotalProfitLoss      TotalProfitLossPercent     StockMaxHigh    StockMinLow     TradingTractionId   TradingType
  4B       20B         20B            8B                   4B             8B                   8B                 8B                  8B                    8B                     8B                      8B             8B               30B                 2B

Total = ~ 132 byte = ~ 150 byte
suppose ek user ek din m 10 request tradning k liye hit krta h 
we have 10 million user 
requesr for trading in one day = 1000000 *10 = 10000000 request/day
suppose we have are storing data for 10 years = 1,00,00,000 *365 *10 = 36,50,00,00,000 request in 10 years = 36.5 billion = ~ 40 billion 
suppose 150 byte takes to order one stock (buy/sell) = total byte required = 40 billion * 150 byte = 6000 GB = 6 TB data (Excluding indeing, Metadata, Encryption, Additional fields)

We need shard Data here because single table can not handle this much data, Single data can handle only 1-2TB Data 
Data Sharding :
As we have 6-10 TB data so number of shard Requirments will be Minimum 6 (1 TB/Shard)
-> Suppose Sharding Base on UserId : When we Shard the data base on the userId means i will use some hashfunction with UserId base on that my data will store in the perticuler shard
                                     Pros: Queries specify to a user (Trading history) are fast because one user's' data  will sotre one specific shard.
                                           Easy to scale Horiantal adding more shard
                                     cons: Suppose one user trades more frequently , it could lead uneven shard size.

-> Supose Sharding Base on StockID : When we shard the data base on Stockid then hashfunction with StockId
                                     Pros: Queries specific to a stock (All trades for stoc X) will be fast
                                           It is good for analytic queries where stock aggergration is required.
                                    cons:   Queries involving multiple shards cross shard queries will be there. 

-> Suppose Shariding Base on (UserId+StockId): When we Shard the data base on (UserId+StockId) , hashfunction with (UserId+Shardid)
                                               Pros: Reduce HostSpot caused by single dominanat user or stock
                                               cons : Increased complexity in shard managmenet and queries will be very complex.

Best Approch to Shard Data :
The Prmary key for the sharding should be userId becase a trading applicaiton is User-Centric becaue most queries are likey to focus on a specific User's' trade hostory.

4. Portfolio Data: 
This is mostly read service not write service. we will fetch the data for a perticuler user in the portfolio.

5. Real Market Data :
Here also there is no write operation, because we need to get the all readl market data form  the NSE/BSE database 

6. Notification Data:
 Some auditing data we neeed to write in our database.

------------------------------------------------------------------------------------------------------------------------------------------------------------------------

=> Find System is Read heavy or Write heavy: 

As we identified that there is six microservice we are using for now so each service will run saperate server and container.

User service :  In this service use can sigup and signin on the application and update the profile so in this case this service write heavy not so much
                but write operation will be there.

Account service : In this service also user can add bank details on the application so in this case also this service write more 

Order Management service :  This service is critical one, User can sell/buy stock. As request for the trading is so high so this system will be write heavy

Portfolio Management service : This service is read heavy, for each user we need to fetch the data to show all the stocks in his portfolio

Real Market service : This servcie also read heave, because in NSE/BSE so many companies are listed, and we need to show them for each users


Note: Service that are read heavy, in those we will use data-raplication so that read can done very efficient. Master-Slave. In read heavy system we reads the 
      data form Slave for effiency.

-----------------------------------------------------------------------------------------------------------------------------------------------------------------------

=> Caching :

User service : As User service is responsible for singup and signin so when user try to login on the application for Authentication the user with the userID we will 
             use Redis cache/In memeory cache so the time to autheticate the user will Reduce
             Calculation : TTL 1 day
             Write Operation : 50 write/sec
             writes in day : 50 * 3600 *24 = 4320000
             User data = 80 byte/user
             total data = 80 * 4320000 = 345 MB
             cache required = ~ 500 MB RAM

Account service : Verfiy the account details, if the account fetched from the Redis then it will be fast. Redis / Memcached we can use
            Calculation : TTL 1 day
            write Operation : 20 write/sec
            write in day = 20 * 3600*24  = 172800 
            Account data = 100 byte
            total data = 100 * 1728000 = 172800000 = 172 MB
            cache requierd ~ 200 MB RAM

Order Service : As this service is hig write heavy service as stocks for buy and sell.
            Calculation : TTL 1 day
            write operation: 200 write/day
            write in day = 200 * 3600 * 24 = 172,80,000 
            order data = 150 byte
            total data = 150 * 172,80,000 = 2,59,20,00,000 = ~ 2600 MB
            cache required ~ 3/4 GB RAM.

Portfolio Service: As this service is Read heavy service because we will fetch the data and show to the user.
            Calculation : TTL 1 day
            Read operation : 300 read/sec
            read in day : 300 * 3600 * 24 = 25920000
            suppose 1 kB / portfolio  = 25920000 * 1= 25 GB
            cachae required ~ 25 GB  RAM 

---------------------------------------------------------------------------------------------------------------------------------------------------------------------
=> Data Base Selection :

User service (PostgreSQL / MySQL) : As in user servcie the user data is unfirom so we can use SQL database. And SQL database has good acid properties. 
               Trade-off : Limited scalbility for write heavy operation without repliaction or sharding.

Account service (PostgreSQL / MySQL) : As Account service also has some unfirom data or some non-uniform data. so we can use SQL + S3 database. In S3 data base we can upload some document.
               Bank account linking requires strong consistency and transactional support to avoid duplication or corruption.
               Trade-off : Scaling write-heavy operations may require partitioning.
                           NoSQL could provide better scalability but lacks the relational querying capabilities needed for structured account data.


Order Service (PostgreSQL / MySQL) : Here user can sell and buy the stock data. Requires tracking order status, stock details, execution time, and trade history. 
               Requires ACID compliance for transactional integrity (e.g., ensuring orders execute without duplication).Relational structure simplifies querying for trade history and order execution details.
               Sharding (e.g., by user ID) addresses scalability for high-frequency trades.
               Trade-off : Horizontal scaling can be complex compared to NoSQL.
                           A hybrid approach (e.g., using Redis for caching frequently accessed data) may be needed to reduce latency.

Portfolio Service (MongoDB / DynamoDB) : Tracks user portfolio details, including current holdings, profit/loss, and overall performance. 
                 Portfolio data is read-heavy and denormalized for faster access.NoSQL databases provide scalability and low-latency reads for frequent portfolio queries.
                 Flexible schema supports dynamic portfolio updates.
                 Trade-off : Complex queries (e.g., aggregation) require careful indexing or additional processing layers.
                             Eventual consistency may be a concern for some real-time updates.

Real Market Service (InfluxDB / TimescaleDB) : Fetches and stores real-time stock prices, market indices, top gainers/losers, and historical data. 
                      Optimized for time-series data, such as stock price updates and historical trends.Efficiently handles high-frequency data ingestion (e.g., price updates every second).
                      Provides built-in aggregation and downsampling for historical analysis.

-------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------

=> Desing Deep Dive : 

1. User Service
This service handles user management (signup, login, profile updates).

Key APIs:

POST /user/signup
Signup (username, password, email, phone, dob, pan)

Output:
Success or failure response with user ID.

Login

POST: /user/ login
Login (username, password)

Output:
Access token, refresh token.

PUT: 
/user/{userId}
Update Profile(userId)
Body: email, phone, address.
Output:
Success or failure.

2. Account Service
Handles bank account linking and fund transfer.

Key APIs:

POST /account/link
Link Bank Account (userId, accountNumber, ifscCode, accountType (saving/current))
Output:
Success or failure with account ID.

GET /account/{userId}
Get Linked Accounts(userId)
Output:
List of linked bank accounts.

POST /account/transfer
Initiate Fund Transfer(fromAccount, toAccount, amount, purpose)
Output:
Success or failure with transaction ID.

3. Order Management Service
Handles placing, modifying, canceling, and executing stock orders.

Key APIs:
POST /order/place
Place Order(userId, stockId, quantity, price, orderType (buy/sell), orderMode (limit/market).)
Output:
Order ID and status.

DELETE /order/{orderId}
Cancel Order(orderId)
Output:
Success or failure.


PUT /order/{orderId}
Modify Order(orderId)
Body: quantity, price.
Output:
Success or failure.

GET /order/{orderId}
Order Status(orderId)
Output:
Order details and status.

4. Portfolio Management Service
Handles viewing and calculating portfolio performance.

Key APIs:
GET /portfolio/{userId}
View Portfolio(userId)
List of stocks, quantities, and current values.

GET /portfolio/{userId}/profitLoss
Calculate Profit/Loss(userId)

Detailed profit/loss per stock and overall gain/loss.

5. Real Market Service
Handles fetching real-time stock prices, charts, and market data.

Key APIs:
GET /market/price/{stockId}
Get Stock Price(stockId)
Output:
Real-time stock price.

GET /market/history/{stockId}
Get Historical Data(stockId, range (1d/1w/1m), interval (1min/5min/hourly))
Output:
Historical price data.

GET /market/top
Top Gainers/Losers(gainers/losers/active)
Output:
List of top stocks.

GET /market/chart/{stockId}
Candlestick Chart(stockId, range, interval.)
Output:
Data for chart rendering.

----------------------------------------------------------------------------------------------------------------------------------------------------------------------






















