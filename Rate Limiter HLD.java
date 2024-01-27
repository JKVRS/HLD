---Rate Limiter HLD --

Rate-limiting basically motivates developers to pay for increased API usage beyond specified limits. 
The most important use case of rate limiters, in my opinion, is to prevent your app from the so-called . 
These attacks can have severe impact on your app, making it vulnerable to: DoS/DDoS attacks

Help prevent overload by limiting the amount of traffic that can access a website or application within a given time frame.
Example: Suppose if we are using any SaaS product than that product have some risriction that x request can be hit in per sec/per hour/per day/per Month.
Basically these type of useCase can be handled by RateLimiter Service.
Rate Limit can be based on : Bsed on IP, Based on accountId.

If we exceed the request limit than we get 429-Too Many Request Error.
This RateLimiter can be done on client side, GateWay Side, App Side.

-> Enforce RateLimits:
1. Throgthning: Allow within the limit beyond limit proivde 429 response.
2. Logging: Logging +Analytics.
3. Blocking : 

RateLimiter Algo:

1. Token Bucket Algo :
Whatever Rate limiting is creating on any level than we will create a bucket with some capacity and we will store tokens with some rate.
like : 10 Token/sec. but beyond the capacity bucket wont add any token .
Code :  Suppose we have different bucket for different user.

public class TokenBucket {
    private long lastRequestTime = System.nanoTime(); // Last Request time for a perticular user
    private double availableTokens;  // avaliable token at that perticular time
    private final double maxBucketSize=200; // capacity of the bucket
    private final double tokensPerSecond=10; // Asume 10 Token Per second is coming

    public TokenBucket(int maxBucketSize) {
        this.maxBucketSize = maxBucketSize;
    }

    public synchronized boolean allowRequest(int NoOfRequest) {
        refill();  // call refill method
       // if available Token are grater and equal No of Request that are coming per second and if token is availalbe in the bucket 
       // than we will allow that perticular request otherwise we dont allow. 
        if (availableTokens >= NoOfRequest) {
            availableTokens -= NoOfRequest; 
            return true;
        }

        return false;
    }
    // it will verify the avilable token how many tokens are avilaable in the bucket
    private void refill() {
        long currentTime = System.nanoTime();
        double timeDelta = (currentTime - lastRequestTime) / 1e9; // calculate the timing gap of lastRequestTime and CurrentRequestTime Convert nanoseconds to seconds
        availableTokens = Math.min(maxBucketSize, availableTokens + (timeDelta * tokensPerSecond));
        lastRequestTime = currentTime;
    }
}
Note: we can consider this as good feature and bad feature as well based on our requiremnt.
downSize : Based on the capacity, can allow a burst of the request to go through.

2. Leaky Bucket:
In this algo we have an bucket where some token are inserting as per sec/min/hour.
and we have a hole in this bucket from where tokens are getting leaked out with some rate.
token if left unused will also get leaked out/wasted.
it allows for more smother handling of burst.

public class LeakyBucket {
    private long capacity;
    private long lastRequestTime;
    private int fillRate=10; // 10 token per second.
    private int leakRate=2; // 2 token per second.
    private int avilaableToken;

    public LeakyBucket(long capacity) {
        this.capacity = capacity;
        this.tokens = 0;
        this.lastRequestTime = System.currentTimeMillis();
    }

    public synchronized boolean allowRequest(int NoOfRequestPerSecond) {
        long now = System.currentTimeMillis();
        long elapsedTime = now - lastRequestTime;
        elapsedTime=elapsedTime/1000;
        lastRequestTime = now;
        availableToken = Math.min(maxBucketSize, availableTokens + (elapsedTime * fillRate)-(elapsedTime*leakRate));

        if (availableToken >= NoOfRequestPerSecond) {
            available-=NoOfRequestPerSecond;
            return true;
        } else {
            return false;
        }
    }
}


3.Fixed Window Counter :
In this we divide the timeLine into fixed Window set.
if Windows are one minute each than 
first Window : 10:00 - 10:01
second Window: 10:01 - 10:02
third Window : 10:02 - 10:03 ...
Suppose In a Window we allows 100 request, now suppose in minute we recieved 80 request than the 20 token remain in that minute.
as next window come up from 10:01 - 10:02 again token will be reset 100 and previous token will be thrown to dustbin.
DownSize : Suppose 100 request comes in last sec of the one window and again 100 request comes in first second of other window that there will be 
           200 request within 2 sec that is high traffic.

4. Sliding Window Counter :(Sliding window algo becomes as approximate algo)
Here the window will be slide in every second.
The Sliding Window algorithm works by keeping track of the timestamp of each request in a rolling time window. 
The server maintains a count of requests for each client and the timestamp of their oldest request. 
When a new request comes in, the server checks the count of requests in the sliding window

Code :
public class SlidingWindowRateLimiter {
    private final int maxRequests;
    private final long windowSizeInMillis;
    private final ConcurrentHashMap<String, Deque<Long>> timestamps = new ConcurrentHashMap<>();

    public SlidingWindowRateLimiter(int maxRequests, long windowSizeInMillis) {
        this.maxRequests = maxRequests;
        this.windowSizeInMillis = windowSizeInMillis;
    }

    public boolean isAllowed(String clientId) {
        // Get the timestamp deque for the client, creating a new one if it doesn't exist
        Deque<Long> clientTimestamps = timestamps.computeIfAbsent(clientId, k -> new ConcurrentLinkedDeque<>());

        // Get the current timestamp in milliseconds
        long currentTimeMillis = System.currentTimeMillis();

        // Remove timestamps older than the sliding window
        while (!clientTimestamps.isEmpty() && currentTimeMillis - clientTimestamps.peekFirst() > windowSizeInMillis) {
            clientTimestamps.pollFirst();
        }

        // Check if the number of requests in the sliding window exceeds the maximum allowed
        if (clientTimestamps.size() < maxRequests) {
            clientTimestamps.addLast(currentTimeMillis);
            return true; // Request is allowed
        }

        return false; // Request is not allowed
    }
}



