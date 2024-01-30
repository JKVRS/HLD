MicroServices 
Monolith :
A single piece of code/service deployed as one depolyable.
when we deploy the code in convert java file to .jar file or .war file. than it will changes into binary artifact all these is called executable.
one service do all the tings
we need to authenticate our service.
Federation of DataBase : Suppose we have differet DB that is decouple that is called fedration 

MicroServices:
Every small small task has different service that is called microservice.
Every service is depolyable indepently.
Example: 
Build HotStar in Monolith:
Build HotStar in MicroServices: UserAuthService,UserService, DataInjection, DiscoveryService, StreaamingService

SOA : (Services Oriented Architecture):
Autonomy + Reuseability: same service can be use for different -different product

Protocols for Communication B/w Service :
1. REST APIs :(Request Response Module) :(Sync Communication) :(Both the service should be avilavable at same time)
Example of how two services might communicate using a REST API:
Service A (Client) wants to get information from Service B (Server).
Service A (Client) sends an HTTP GET request to Service B (Server).
The request is in the form of a URL, like http://serviceB.com/users/123.
Service B (Server) receives the request, processes it, and returns the appropriate response (usually in JSON or XML format)

2. Remote Procedure Call : (Sync Communication)
Google Remote Procedure Call : 

3. Event Driven Architecture Communication : 
Ex: Swiggy 
Order Service, PaymentSerice, Resturant Service, Delivery Service, Notification Service
There is a queue suppose one order take place as the order placed than one oerder event take placed into the queue.
than Notification Service and PaymentSerice will interect witht he order queue to read the event.
Now there will be two queue payment succes and fail as the payment success than the new event will generate and put into the payment queue.
like wise two Services are talking to each other via an trigger event.
Note : In this mode of communication , services dont use Sync/request-response models.rather they communicate using events sent via different relevant msg queue and that mdg is read by all those services that are interseted that event.
       All service are completely deCoupled.
Problems: Problem with this is "CHAOS".(how to keep track of the overall job is done, the managment of the event is chaosing).
          Job should be done completely suppose if one service is failed than upstreaming service should know about this.(This things need to manage in EDA by Distributed Transaction).
          Distributed Logging and monitoring are required.

-> Manage Chaos :
SAGA Pattern :
2 phase Commit :

Design Pattern of microservice:
1. Strangler Fig Pattern :
Incrementally migrate a legacy system by gradually replacing specific pieces of functionality with new applications and services.
Suppose initially we have one Monolith than beside that we have a small microservice as per requirment.
Pull out lowest hanging seprabale componenet into an independent service.
Example: 

2. 2PC (2 Phase Commit ):
One way of manageing distributed transaction in case of microservice.
Example: we have Order Service,Payment Service, Resturent Service, Logistic Service
We will set up a cordinator node that will manage end to end process of one transaction.
suppose order service generate order no 100 than that perticular node will interect will all service and manage the communication.
this node will get three posiable from others services done/failed/pending.
phase 1 - Asking of question from other services about the status of generated order.
-if we get pending response from some services that cordinator will ask again about the status for those services.
when we get Done response from all service than cordinator will go into next pahse and let all the service know that they can go ahead and commit the message.
-if we get failed response from at least one service or time out happend and at least one service was still showing pending than in phase 2 cordinator will say that commit with the faild and rollback again the transaction.
Problems : cordinator itself becomes strained and we need to do desgin cordinator service in well manner.(Scalable , failSafe cordinator woll required).
           Commit are dependent on the slowest step to complete.

3. SAGA Pattern :
This Pattern solves for distributed trnsaction using "Compensatory Transaction".
The SAGA pattern is a design pattern used in microservices architecture to manage and maintain data consistency across multiple services.
It’s a sequence of smaller transactions, where each transaction is executed by a single service, and the state changes are broadcasted to other services involved in the Saga. 
The name “SAGA” comes from the concept of a long story with many parts, just like a distributed transaction1.

Here are some key features of the SAGA Pattern:
Coordinated transactions: The SAGA pattern provides a way to coordinate transactions that involve multiple services or processes.
Compensation and rollback: The SAGA pattern includes a mechanism for compensating or rolling back the transaction if one of the steps fails.
Distributed transactions: The SAGA pattern supports distributed transactions that span multiple services or processes.
Asynchronous processing: The SAGA pattern can support asynchronous processing, which allows for greater concurrency and performance.
Error handling: The SAGA pattern provides a standardized way to handle errors that occur during the transaction.
Scalability: The SAGA pattern can scale to handle large and complex transactions that involve multiple services or processes.

Code :

@Service
public class OrderService {
    @Autowired
    private SagaOrchestrator sagaOrchestrator;

    public void createOrder(Order order) {
        sagaOrchestrator.create(order);
    }
}

@Service
public class PaymentService {
    public void processPayment(Order order) {
        // Implementation goes here
    }
}

@Service
public class InventoryService {
    public void updateInventory(Order order) {
        // Implementation goes here
    }
}

@Service
public class DeliveryService {
    public void deliverOrder(Order order) {
        // Implementation goes here
    }
}

@Service
public class SagaOrchestrator {
    @Autowired
    private PaymentService paymentService;
    @Autowired
    private InventoryService inventoryService;
    @Autowired
    private DeliveryService deliveryService;

    public void create(Order order) {
        try {
            paymentService.processPayment(order);
            inventoryService.updateInventory(order);
            deliveryService.deliverOrder(order);
        } catch (Exception e) {
            // Rollback or compensate the transaction
        }
    }
}
SAGA has two type pattern : 1. Choreographed SAGA : the Choreographed SAGA Pattern uses event-driven architecture
                            2. Orchestrator SAGA : Add a centerlized Orchestrator 
Code of Choreographed SAGA :

@Service
public class OrderService {
    @Autowired
    private ApplicationEventPublisher publisher;

    public void createOrder(Order order) {
        // Save order to database
        // ...
        publisher.publishEvent(new OrderCreatedEvent(order));
    }
}

@Service
public class PaymentService {
    @EventListener
    public void onOrderCreatedEvent(OrderCreatedEvent event) {
        // Process payment
        // ...
    }
}

@Service
public class InventoryService {
    @EventListener
    public void onPaymentProcessedEvent(PaymentProcessedEvent event) {
        // Update inventory
        // ...
    }
}

@Service
public class DeliveryService {
    @EventListener
    public void onInventoryUpdatedEvent(InventoryUpdatedEvent event) {
        // Deliver order
        // ...
    }
}
