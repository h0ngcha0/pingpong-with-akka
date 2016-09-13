# Basic
POST http://localhost:9000/basic/ping
CONTENT-TYPE: application/json

{"pingpongball": 0}

# Supervised restart
POST http://localhost:9000/supervised/ping
CONTENT-TYPE: application/json

{"pingpongball": 0}
