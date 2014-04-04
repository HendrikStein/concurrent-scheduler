Concurrent Scheduler
====================
This is my playground to schedule tasks within a scalable backend cluster. 

This work is still in progress...

VM Parameter
------------
- -Dnode.name=NODE_1 {The node Name}
- -Dschedule.amouttasks=5 {The amount of scheduled tasks}
- -Dschedule.timeunit=MINUTE {Periodical Time Unit. Possible values DAYS, HOURS, MINUTES, SECONDS}
- -Dschedule.period=5 {The period}
- -Donetime.amounttasks=5 {The amount of one time tasks}





Third Party Libraries
====================

- Hazelcast, Open Source in Memory Data Grid (http://www.hazelcast.org/)
- Joda Time (http://www.joda.org/joda-time/)
- Commons Lang (http://commons.apache.org/proper/commons-lang/)
- Log4J (http://logging.apache.org/log4j/1.2/)
