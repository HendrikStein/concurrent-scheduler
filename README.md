Concurrent Scheduler
====================
This is my playground to schedule tasks within a scalable backend cluster. 
My challange is to not to use the Quartz Framework but retain control over the synchronize process.
My approach uses the distributed data grid Hazelcast to synchronize the scheduler over multiple VMs.

***Work is still in progress...***

VM Parameter
------------
- -Dnode.name=NODE_1 {The node Name}
- -Dschedule.amouttasks=5 {The amount of scheduled tasks}
- -Dschedule.timeunit=MINUTE {Periodical Time Unit. Possible values DAYS, HOURS, MINUTES, SECONDS}
- -Dschedule.period=5 {The period}
- -Donetime.amounttasks=5 {The amount of one time tasks}


Note
====
This is an eclipse project, so it is easy to import.
There are 2 launcher to start different nodes. The Shutdown hook doesn't work if you terminate the java process with eclipse.


Third Party Libraries
====================

- Hazelcast, Open Source in Memory Data Grid (http://www.hazelcast.org/)
- Joda Time (http://www.joda.org/joda-time/)
- Commons Lang (http://commons.apache.org/proper/commons-lang/)
- Log4J (http://logging.apache.org/log4j/1.2/)
