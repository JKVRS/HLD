******** Design HostStar/Netflix/Youtube/Amazon Prime ***********

Two Type of Streaming - Recorded Stream 
                      - Live Stream

Three Major Component - Injection, Discovery, Streaming

Ingestion of content : 
Ingestion means writing something.
Suppose a user want to upload the file 
frist user will upload a RAW VIDEO FILE.
Video- Bunch of image one after another. (frame/sec)
Suppose a video has 30 frame per sec.
How long movie is - 2 hr
total frame per sec in video = 30*2*60*60=7200*30=216000 frame/video  in one video.
since a single frame has different different resolution.
like - 144p,240p,360p,720p,1080p,2k,4k
1080p=1920(Width)*1080(Hight) resolution.
Every Image store Pixcel that take some byte.
suppose single pixcel take 1 Byte and if it is coloured than it will take 3 Byte= 1 Byte (RED)+1 Byte (Green)+1 Bytw(Voilate)
storge required for one frame : 1920 * 1080 *3 Byte.
Total Storage Required for a Video =216000 *1920*1080*3 Byte = 1296 * 10^9= 1296 GB= 1.2 TB
If user store RAW VIDEO than it will takes some TB storage.

After upload of RAW VIDEO Content there will be some process of Injection in HOTSTAR black Box :-
1. Store RWA File as File in BLOB Store.
2. PipeLine in HOTSTAR : Distributed Async Queuing Mechanisam.
   First queue will be copyright check.
         If the video is bad or copyright than stop the further process
         If the video is good than we might want to populate upload id iin some other queue so that next step can place.
   Second Queue will be Resolution Generation.
         We can convert RAW Video into multiple Pixcels like: 144p,240p,360p
         It will generate copy of video in different different Resolution.
    Third Queue will be Codec.
          It is nothing but compression and decompression of video.change the video in the form of like: .mp4,.avi,
          Algoritham :
                   Suppose a video has 30 Frame per second.
                   Duirng the straming a lot of frame doesnot change so the codec queue will add delta changes into the previous frame.
                   that compress the video from TB to some GB.  
    Fourth Queue will be chunkifiner :
          HostStar will make 2 min logical chunks.than that video will be chunkify for 2 min logical chunks that is been seted up by hotstar.
    update Elastic serach cluster.  
    Analytics DB update.
    Populate Recommandation system.
    Populate at CDNs.
    (All of these step are going to be part if ingection PipeLine).

Discovery:- 
SearchService(): Elastic Search
LandingPageServie(location,catageory,)
RecommandationService()
AdvertiseMentService()

Streaming:-
Client : User 
Flow : Client -------->Discovery Service (landing page, search, scrolling page) after this user will click one movie and a new page will open.
First Request from the user is getMetaData(accessToken,contentId).
Second Request getVideoMetaData(accessToken,contentId)
      Response is
         { VideoTime in videoChannel,Video Running Bar,Thumbnail,when hobber muouse at bar its load perview images }
 HostStar load first few chunks of the video to start the video as the time passes than more request sends to load the next chunks of the video.
Third Request getVideoChunks(accessToken,contentId,chunkId) 
suppose a video has 3 hr and logical chunks is 4 min than total chunks is required : 180/4=45 chunks
ChunkNo.  ChunkId Timer 
1.        107      0-4 min
2.        108      4-8 min
3.        109      8-12 min
this request go to HostStar server, HostStart will provide us a CDN link.
so our reuquest will go to nearset location DNS server and start to play video.
we need to hit this request preamptive menner so that user can watch the content seamlessly.
-> User can hit the request for the chronologically next 4-5 chunks.
-> User shold also fetch most probable chunks based on previosly data science analysis. another API will call getPopularChunks(accessToken,contentId)
-> Client is going to sotre those popular video chunks as client side cache.
suppose i have 2 min chunks.
time taken to call the CDN and get response form CDN is = 20 sec.
as my first 20 sec video run than our code will be like this after 20 sec permetivley it request again to load the next chunks.

Adeptive Bitrate Streaming (ABS):
HostStar keep monitoring the BandWidth and Device Type of the user so if user has good BandWidth HostStar give response with good resolution but if BandWidth is low than resolution will be low.
that's' why something our video quality is low and after some time it will be batter.

Summerization that allow Streaming:
Chunking
Premtive Chunk loading
Browser/Clinet Side Chace.
CDN
MetaData of populer contenst chould be chache in Global/App server Chache.
Encoding optimization to reduce RAW video.
ABS 

**** Live Streaming:**** 

Cricket Ground ----> Set Of Cameras ----> RTMP (Real Time Messageing Protocol)---> HotStar server

In Real Time streaming the chunks size should be Minimum second like 10 sec or 15 sec.
in this moment while creeating the 15 sec chunks created a few job are scheduled.
-> we should created multiple resolution of 15 sec chunks.
-> crate two codec per resolution.
-> to populate meta data about that chunks DB+CACHE.
-> upload the chunk in CDN and update the meeta data aslo.

Client Side :
getMetaData()
getVideoMetaData() : This API will change with getChunkMetaData(accessToken,CurrentTimeStamp) -> as i make this api call i will get last all the chunks.
Suppose i am hiting the api call at 3:40:00 than in response , here we are supposing 20 sec is already delay for live streaming.
 i will get CH1 3:39:20 - 3:39:40 
            CH2 3:39:00 - 3:39:20 
User will have to continuously call this API.

Scale of Live Streaming :
Injection
Discovery
Streaming : MeataData Fetch , ChunkVideo Fetch

-> Injection Scaling : No changes in Injection as scalling.
-> Discoverry scalling: Yes, Discovery Service need to scaling.
     Layouts gets changes based on scale .
-> Streaming Scaling :  Meta : Need to scale up DB,Cache,App Servver.
                        Video Chunk : CDNs need to scale.

Why live Lecture on scaler and live match on hotstar is different :
First Point on Scaler live stream it doesnot allow to watch tha past content.
Second Point is traffic is very less compare to HostStar.
Third Point after live lecture to willl change to recorded that takes some time not immideatly it changes.

