# Hospital API

## `GET /patient/{id}/name`

### Description:
- Returns the patient name

### Parameters:
- Id -  integer

### Authorization Scope:
- Doctor
- Nurse
- Janitor

### E.g. Request:
```
GET /patient/1/name
Authorization: r6C6xEEZSKYrHX8i...
Accept: application/json
```

### E.g. Response:
```
200 OK
Content-Type: application/json
 
 {
     "name": "Alberto"
 }
```


## `GET /patient/{id}/diseases`

### Description:
- Returns the diseases associated to a patient. 

### Parameters:
- Id -  integer

### Authorization Scope:
- Doctor during normal mode.
- Anyone during pandemic mode.

### E.g. Request:
```
GET /patient/1/diseases
Authorization: r6C6xEEZSKYrHX8i...
Accept: application/json
```

### E.g. Response:
```
200 OK
Content-Type: application/json
 
 {
     "name": "Alberto",
     "diseases": ["Covid-19", "Pneumonia"...]
 }
```

## `GET /patient/{id}/testresults`

### Description:
- Returns the results of the tests. 

### Parameters:
- Id -  integer

### Authorization Scope:
- Doctor during normal mode.
- Anyone during pandemic mode.

### E.g. Request:
```
GET /patient/1/testresults
Authorization: r6C6xEEZSKYrHX8i...
Accept: application/json
```

### E.g. Response:
```
200 OK
Content-Type: application/json
 
 {
     "name": "Alberto",
     "results": "Covid-19: True, HIV: False, ..."
 }
```

## `GET /patient/{id}/treatment`

### Description:
- Returns the necessary treatment a patient must take. 

### Parameters:
- Id -  integer

### Authorization Scope:
- Doctor.
- Nurse.

### E.g. Request:
```
GET /patient/1/treatment
Authorization: r6C6xEEZSKYrHX8i...
Accept: application/json
```

### E.g. Response:
```
200 OK
Content-Type: application/json
 
 {
     "name": "Alberto",
     "treatment": "500mg of Vicodin"
 }
```


## `POST /sendtestresults/{id}`

### Description:
- Sends the test results back to the hospital from the lab. 

### Parameters:
- Id -  integer

### Authorization Scope:
- Lab employee.

### E.g. Request:
```
POST /sendtestresults/1
Authorization: r6C6xEEZSKYrHX8i...
Content-Type: application/json

{
    results: "25/05/2020 Covid19:True,Pneumonia:True...",
    digitalSignature: "YWJjZGVmZ2hpamtsbW5vcHFyc3R1dnd4eXpBQkNERUZHSElKS0xNTk9QUVJTVFVWV1hZWjAxMjM0NTY3ODkK=="
}
```

- Digital signature only protects the 'results' field of the request. Signature must then be stored by the hospital for authenticity check when needed.

- The id used in the URI is the one received 

### E.g. Response:
```
200 OK
```

## `GET /checkauthenticity/{id}`

### Description:
- Checks if the received test results data is authentic.

### Parameters:
- Id -  integer

### Authorization Scope:
- Doctor

### E.g. Request:
```
POST /sendtestresults/1
Authorization: r6C6xEEZSKYrHX8i...
Accept: application/json
```

### E.g. Response:
```
200 OK

{
    "check": "Data is valid."
}
```

or

```
200 OK

{
    "check": "Error, the integrity of the results of this test have been compromised."
}
```

# Partner Lab API

## `POST /teststoanalyze`

### Description:
- Sends a bunch of random data to the Partner lab for analysis.

### Parameters:
- None

### Authorization Scope:
- Doctor

### E.g. Request:
```
POST /sendtestresults/1
Authorization: r6C6xEEZSKYrHX8i...
Content-Type: application/json

{
    "test_id": 1,
    "data": "vWmkAcHlWb..."
}
```
- The test_id is a random INTEGER that will allow to associate the test results with a patient. It's not used the patient id to maintain privacy.

### E.g. Response:
```
200 OK
```
# Common API for Hospital & Lab

## `POST /login`

### Description:
- Attemps a login.

### Parameters:
- None

### Authorization Scope:
- None

### E.g. Request:
```
POST /login
Content-Type: x-form-www-urlencoded

username=mrdoctor&password=doctor
```

### E.g. Response:
```
200 OK

{
    "token": "r6C6xEEZSKYrHX8i..."
}
```