# StructuredTaskScope
Example how to use StructuredTaskScope
based on [this Repo](https://github.com/typed-rocks/structured-taskscopes/tree/main).

I have also added JMeter tests to show the performance gain compared to synchronous processing.


#Previous design: 
Thread-per-Request => One Request is handled by its own thread. e.g. Java EE-Server
One Thread costs 20MB and 1ms to launch. 
Blocking => bad for I/O

Virtual Thread: 
about 1000 times cheaper
no nead to write asynchronous code

if virtualThreads are blocking, it gets detached from platform thread 
> Virtual Thread is in java heap memory
> after Blocking, it can attach to a second platform thread
