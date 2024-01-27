Design a unique Id Generator

Requirments:
Id should be unique
Id should be Incremental in nature.
we should not use centerlized id generator because it cause some point of failure when one machine will fail.
so we should use distributed Id generator.


Idea 1: If i use TimeStamp as unique Id generator than there might be an issue that at same time two request will came.
        at that time two same id will generate.
Idea 2: In sql server autoIncrement Unique Id will generate.
        but sql works for single machine, if our problem is to generate Id in single machine than SQL server is fine but if we are using different different machine than it wont work.
Idea 3: we can use UUID, but UUID also in soemtime not in incremental nature.
Idea 4: Actula solution : the best idea is that we can use TimeStamp+ServerId with some edge case.
        This is called "Twitter SnowFlakes Algo".

=> Twitter SnowFlakes Algo:
We are going to be generate 64 Bit Ids.Its take less memory than UUID becaue UUID generate 128 Bit.
These 64 bit deserve for different purpose.
->1st bit is sign bit.it not even use.
->Next 41 Bit will use to store the Timestamp.This Time is Basically Epoch Time.This time calculate in nenoSec.
  the cutoff date for this is : 4th/11/2010
  if 41 bit we can store : 2^41-1 = 2199023255551 milisecond.
  means this algo can store = 2199023255551/(1000*60*60*24*365)=  2199023255551/31,53,60,00,000 =70 year
  means this algo will store the timeStamp from 2010 to 2080 year.becaue this algo suggests in 2010 for the twitter but if we want built our own algo with the same than the cut off date will be current date.
  
-> NeXT 5 bit deserve for data center Id. = 2^5= 32 DataCenter
-> Next 5 Bit for Mechaine Id.= 2^5= 32 Machine/data center.
-> Last 12 bit for sequesce of Number:
we can cutomize these bit distribution base on our configrutation.
This Algo  allows for 1024 machine to generate non conflicting+incremental Ids parallely.
-> Last 12 bit is for Sequence Id: 2^12= 4096 this sequece handle the same id generator.
  Ex : if so many request is came up at same milisecond on same machine than it will be handle by last 12 bit by introduceing the counter in same mili second.
  we have a counter clock that reset in some amount of milisecond so when at the same time more than one request comes id will generate sequencelly.
  becase we are handleing 4096 request in 1 milisecond in last 12 bit after that it again reset to 0.
-> First bit can we use to delete the Id ,suppose if we wnt to delete a id than simply we can set first bit as 1 or 0 .   
EDGE case:
TSA always provides unique key under the asumption that not more than 4096 request will come to the same machine in same milisecond.

Code :
public class Snowflake {
    private long sequence = 0L;
    private final long workerId;
    private final long twepoch = 1288834974657L;
    private long lastTimestamp = -1L;

    public Snowflake(long workerId) {
        this.workerId = workerId;
    }

    public synchronized long nextId() {
        long timestamp = System.currentTimeMillis();

        if (timestamp < lastTimestamp) {
            throw new RuntimeException("Clock moved backwards. Refusing to generate id for " + (lastTimestamp - timestamp) + " milliseconds");
        }

        if (lastTimestamp == timestamp) {
            sequence = (sequence + 1) & 0xFFF; // 12 bits
            if (sequence == 0) {
                timestamp = tilNextMillis(lastTimestamp);
            }
        } else {
            sequence = 0L;
        }

        lastTimestamp = timestamp;

        return ((timestamp - twepoch) << 22) | (workerId << 12) | sequence;
    }

    private long tilNextMillis(long lastTimestamp) {
        long timestamp = System.currentTimeMillis();
        while (timestamp <= lastTimestamp) {
            timestamp = System.currentTimeMillis();
        }
        return timestamp;
    }
}
