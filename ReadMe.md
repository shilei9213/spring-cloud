##端口规划

###Eureka Server：
---
spring:
  profiles: Standalone
  application:
    name: eureka-server-standalone
server:
  port: 8761
---
spring:
  profiles: Peer1
  application:
    name: eureka-server
server:
  port: 50001
---
spring:
  profiles: Peer2
  application:
    name: eureka-server
server:
  port: 50002
---
spring:
  profiles: Peer3
  application:
    name: eureka-server
server:
  port: 50003

###服务提供者：
---
server:
  port: 10001
spring:
  profiles: Instance1
  application:
    name: microservice-time

---
server:
  port: 10002
spring:
  profiles: Instance2
  application:
    name: microservice-time


###服务消费者:
server:
  port: 20001
spring:
  application:
    name: webfront

###Api 网关：
server:
  port: 60001
spring:
  application:
    name: api-gateway-zuul