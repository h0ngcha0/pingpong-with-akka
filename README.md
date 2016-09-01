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

``` bash
curl -XPOST -H"Content-Type: application/json" -d'{"color": "red"}' localhost:9000/basic/ping
```
