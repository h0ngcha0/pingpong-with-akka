:port = 9000
:port2 = 32776

:header = <<
CONTENT-TYPE: application/json
#

# Basic
POST http://localhost::port/basic/ping
:header

{"pingpongball": 0}

# Get balls seen by basic
GET http://localhost::port/basic/ping

# Supervised restart
POST http://localhost::port/supervised/ping
:header

{"mustketball": 0}

# Get balls seen by supervised
GET http://localhost::port/supervised/ping


# Clustered
# master node
POST http://localhost::port/clustered/ping
:header

{"pingpongball": 0}

# worker node
POST http://localhost::port2/clustered/ping
:header

{"pingpongball": 0}

# Get balls seen by one member in cluster
GET http://localhost::port/clustered/ping

# Get balls seen by all members incluster
GET http://localhost::port/clustered/all
