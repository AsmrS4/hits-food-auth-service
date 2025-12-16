#!/bin/bash

echo "Running unit tests..."
mvn -q -Dtest=com.example.user_service.units.services.* test

echo "Running API tests..."
mvn -q -Dtest=com.example.user_service.api.* test

echo "All tests completed."

read -p "Press any key to continue..." key