HOST METRICS MONITOR:

Given 100,000 servers and the requirement to query each host and store the response in an in-memory data structure and keep doing this every 2 second to update the metrics values of each host.
MULTITHREADING:
 To achieve this multithreading is needed since the requests need to send parallelly to the 100,000 different server endpoints. For instance, if the input is a list of all the 100,000 hosts
•	1 thread can send 0.1*2 = 20 HTTP GET requests (each request takes 100ms = 0.1s) hosts within the 2 second interval. 
•	Hence, we would need to have a maximum of 100,000 / 20 = 5,000 threads for all the servers.
Since there would be an overhead in time for the TCP connection as well for each HTTP GET request a batch of threads can be assigned to work on certain set of host servers as the requests are made every 2 seconds.
I have made use of a Java utility called the ExecutorService which allows to pass tasks to be executed by a thread asynchronously. The executor service creates and maintains reusable pool of threads for executing the submitted tasks. ScheduledExecutorService is used to run the threads in a fixed time rate.
STORAGE:
For each GET request that is made the response is stored to an in-memory data structure, a Hashmap assuming the size required would to size a constant 100,000 host name keys which have JSON strings as values. Each JSON string can be assumed to be having about 5 metrics, each of which being a string, an integer or a map of values. If there is a constraint in storing the map in a global hashmap, in-memory database like Redis can be leveraged to store the data. Another approach would be to divide the map based on the hosts and store on different nodes and devise a mechanism to retrieve the metrics by the host specified by connecting to the right node.   
 
IMPROVEMENTS:
•	Can have a cluster. Each machine can maintain a map (hostname -> Metrics} for a few hosts. Based on the hostname in the query we can go to the specific node which has the map for that host. 



