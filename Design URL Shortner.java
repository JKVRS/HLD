Design URL Shortener :

Requirments:
We have one URL when we hit that URl Automtic that URL will be changed in the Shortener URL 
Use Case: Suppose when i am psot a URl in the LinkedIn or any other palterform than that palterform automatic short that URL and when we click that Shortener URL 
          we land on the actual page.
          Expiry date should be thre for shortner the URL.

Estimation of the Scale:

Suppose the ratio of the read and write = 100 : 1
Epiry of a URL is 10 year
1 million new URL is coming in a month for shortner
Now one URL take 500 bye after short
storege calculation :
1000000 * 12 * 100 * 500 byte=120,00,00,000 * 500 byte = 0.12 Billion * 500 Byte = 60.00= ~ 60GB data 

Cache : Suppose TTL of the cache is 1 day 
50 Query/sec for write the operation 
the query in a day is = 50 * 3600 * 24 = ~ 50,00,000 query/day 
data storege for one day = 0.0005 Billion * 500 Byte = .25 GB
So we need ~ 1 GB RAM for cache

Desin Goals:

APIs:
shortURL(String URL, long UserId, String apiKey, Date expiry)
deleteURL(String apiKey, String OrginialURL, String shortURL)
redirectionAPI()

DB Choose :
MongoDB : URL table , User table

Algoritham : 
main URL become Short : use the map store the shorter and actual URl in map but this approch is not Scaleable.
                        so for this i will use MD5 hash that will provide us the 128bit unique string but suppose i dont want 128 bit 
                        i want more less character like 8-10 character but suppose if i pick up the first 5-8 character of the MD5 has generated URL than it might be possiablity 
                        that first 5-8 character will be same so its a Collisions.

avoid Collisions :
we have a-z, A-Z , 0-9 total 62 character and suppose i want 5 length of the shorten URL
total combination is 62 ^n (n= length of shorten URL)
total URL in 10 years : 1000000 * 12* 10 = 120000000

62^n>120000000
take the log 
log(62^n)>log(120000000)
n ~ 4.5 =5

so we use base62 to generate the shortend URL
Base2 input = Number -> Base62 -> AZaz12...
for each long URL we will generate the random number and with the help of base62 we'll' the this input to base62, but there is possiblity that same random can be generate.
to resoolve to generate the random we will use Zookeeper.
Zookeeper is a Distributed Mangmenet server  






