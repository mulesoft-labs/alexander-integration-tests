# Alexander Integration Tests

These are the integration tests for Alexander Services. 

To install all the modules without running the tests
```
mvn clean install
```

To run just the tests without installing anything else
```
mvn clean install -Pintegration-tests -Dmozart.environment=devx
```
