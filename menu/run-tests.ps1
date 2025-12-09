Write-Host "Running unit tests..."
mvn -q -Dtest=com.example.demo.unit.* test

Write-Host "Running API tests..."
mvn -q -Dtest=com.example.demo.api.* test

Write-Host "All tests completed."