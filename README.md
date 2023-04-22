# Required
 - java
 - maven
 - docker
# Run
```
make dev
```
# Publish
```
curl --location 'http://localhost:8085/v1/projects/my-gcp-project/topics/legacy-topic:publish' \
--header 'Content-Type: application/json' \
--data '{
    "messages": [
        {
            "attributes": {
                "test": "test"
            },
            "data": "am9obg=="
        }
    ]
}'
```

# Pull

```
curl --location 'http://localhost:8085/v1/projects/my-gcp-project/subscriptions/main-subscription:pull' \
--header 'Content-Type: application/json' \
--data '{
  "maxMessages": 100
}'
```