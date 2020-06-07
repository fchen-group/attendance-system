# Introduction
Attendance systems are likely to increase students' academic performance, which are reported in existing researches. Traditional attendance systems either rely on calling a name roll or on swiping some hardware to check attendance. These approaches are time consuming or hard/expensive to maintain the system. The wide adoption of smart phones open a new angel to design a more friendly attendance system.

How to leverage *existing*, *wide-spread* computing infrastructure (e.g. a mobile phone, a mobile app, network connections) to design a user-friendly attendance system?
What are students' attitude on such a system?

To solve the above problem, this project designed and implemented a mobile phone based lightweight attendance system. The proposed system reports attendance by scanning a QR code. We have deployed the system and used the proposed system in two classes throughout one semester for first year college students.

Our experience showed that attendance systems using mobile technology are much faster than traditional attendance taking. One attendance checking is able to finish in 1 minute with same accuracy and cheating resistance. Students also showed positive feedbacks on the proposed system. The positive feedbacks seem to due to two reasons from the survey. One is that the proposed system are more efficient than traditional systems. Another is that the proposed system is also easy to use.

# Implementaion Details
The implemeation contains three modules.
* `SignServer` module  
This the backend module. It is implemented using JavaEE; specifically, spring, springmvc, mybatis, websocket are used to construct the server.
* `web` module    
This is one frontend module. It uses 'vue' to construct web pages for instructors to setup, manage the system.
* `mini-program` module 
This is another frontend module. It uses Wechat mini-program techinique to implement a web based mobile pages for students to take attendance.

# Survey Data
We have conducted a survey on students' attitude after one-semester use of the proposed system.

The survey form is [here](/survey%20form.pdf).

The survery result is presented in a [sheet](/survey%20results%20sheet.xlsx) and a [graph](/survey%20results%20in%20graph.pdf).
