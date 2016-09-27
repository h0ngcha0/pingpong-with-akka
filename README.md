PingPong with Akka
==================

Present some of the core concepts in Akka while playing Ping Pong!

# Topics to be covered
    - Concurrency
    - Fault tolerance (supervision strategy)
    - Akka remote
    - Akka cluster
    - Akka persistence

# Generate docker image

``` bash
sbt docker:publishLocal
```

# Run

``` bash
docker-compose up
```

under the project root directory


# Example

:port = 9000

:header = <<
CONTENT-TYPE: application/json
#

### Send a pingpongball
POST http://localhost::port/ping
:header

{"type": "pingpongball"}

### Get number of balls seen
GET http://localhost::port/ping
:header

### Send a basketball (resume)
POST http://localhost::port/ping
:header

{"type": "basketball"}

### Send a fireball (restart)
POST http://localhost::port/ping
:header

{"type": "fireball"}

### Send a bullet (stop)
POST http://localhost::port/ping
:header

{"type": "bullet"}
