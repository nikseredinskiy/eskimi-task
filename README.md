# eskimi-task

`sbt run` to run the project

POST request to `localhost:8080/check` with the following JSON body:
```json
{
    "id": "jjkk",
    "site": {
        "id": "0006a522ce0f4bbbbaa6b3c38cafaa0f",
        "domain": "fake.tld"
    },
    "device": {
        "id": "440579f4b408831516ebd02f6e1c31b4",
        "geo": {
            "country": "LT"
        }
    },
    "imp": [
        {
            "id": "1",
            "wmin": 50,
            "wmax": 300,
            "hmin": 100,
            "hmax": 300,
            "h": 250,
            "w": 300,
            "bidFloor": 3.12123
        }
    ],
    "user": {
        "geo": {
            "country": "LT"
        },
        "id": "USARIO1"
    }
}
```

Expected response: 
```json
{
    "adid": "1",
    "banner": {
        "height": 250,
        "id": 1,
        "src": "https://business.eskimi.com/wp-content/uploads/2020/06/openGraph.jpeg",
        "width": 300
    },
    "bidRequestId": "jjkk",
    "id": "response1",
    "price": 3.12123
}
```
