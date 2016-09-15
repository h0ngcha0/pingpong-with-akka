# Basic
POST http://localhost:9000/basic/ping
CONTENT-TYPE: application/json

{"pingpongball": 0}

# Get balls seen by basic
GET http://localhost:9000/basic/ping

# Supervised restart
POST http://localhost:9000/supervised/ping
CONTENT-TYPE: application/json

{"mustketball": 0}

# Get balls seen by supervised
GET http://localhost:9000/supervised/ping
