#!/bin/bash

curl -sb -X POST -H "Content-Type: application/json" localhost:9000/$1/ping -d '{"type": "'$2'"}'