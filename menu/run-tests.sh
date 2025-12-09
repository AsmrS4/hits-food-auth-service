#!/bin/bash

echo "Running unit tests..."
mvn -q -Dtest=com.example.demo.unit.* test

echo "Running API tests..."
mvn -q -Dtest=com.example.demo.api.* test

echo "All tests completed."
