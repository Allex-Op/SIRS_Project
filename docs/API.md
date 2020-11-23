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
     "diseases": "Covid-19, Pneumonia..."
 }
```

## `GET /patient/{id}/testresults`

### Description:
- Returns the results of the tests. 

### Parameters:
- Id -  integer

### Authorization Scope:
- Doctor.

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


## `GET /gettestresults/{id}`

### Description:
- Requests the results of the test results associated with id.

### Parameters:
- Id , Integer. 

### Authorization Scope:
- Doctor.

### E.g. Request:
```
GET /gettestresults/1
Authorization: r6C6xEEZSKYrHX8i...
```

### E.g. Response:
```
200 OK

{
    results: "25/05/2020 Covid19:True,Pneumonia:True...",
    digitalSignature: "YWJjZGVmZ2hpamtsbW5vcHFyc3R1dnd4eXpBQkNERUZHSElKS0xNTk9QUVJTVFVWV1hZWjAxMjM0NTY3ODkK=="
}
```

# Partner Lab API

## `POST /teststoanalyze/{id}`

### Description:
- Sends a bunch of random data to the Partner lab for analysis.

### Parameters:
- None

### Authorization Scope:
- Hospital API (with shared secret)

### E.g. Request:
```
POST /teststoanalyze/1
Authorization: r6C6xEEZSKYrHX8i...
Content-Type: application/json

{
    "data": "vWmkAcHlWb..."
}
```

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

# Authorization API (PDP, PAP...)

## `POST /pdp`

### Description:
- Evaluates a XACML request (JSON profile) and decides if the request should be accepted or denied.
- The group of policies that should be respected are not sent in the request, but already present in the machine.
- Only the associated API can send authorization requests, for that a shared secret will be included in the request, similar to the authorization header access token. Example, the hospital API and the authorization API of the hospital will share the secret string "69i57j69i59j69i60l3j69i65l3.1158j0j7". This secret will be hardcoded and there are no mechanisms to generate another one in case this secret gets compromised.


### Parameters:
- None

### Authorization Scope:
- Associated API (Hospital API or Lab API)

### E.g. Request:
```
POST /pdp
Content-Type: application/json

{
       "Request": {
              "AccessSubject": {
                     "Attribute": [
                           {
                                "AttributeId": "subject-role",
                                "Value": "Doctor"
                           }
              },
              "Action": {
                     "Attribute":
                           {
                                  "AttributeId": "action-id",
                                  "Value": "GET"
                           }
              },
              "Resource": {
                     "Attribute": [
                           {
                                "AttributeId": "resource-id",
                                "Value": "/patients/{id}/diseases"
                           }
              },
              "Environment": {
                  "Attribute": [
                      {
                          "AttributeId": "Context",
                          "Value": "Pandemic Mode"
                      }
                  ]
              }
       }
}
```

### E.g. Response:
```
200 OK

{
    "Response": [{"Decision": "Permit"}]
}
```

or

```
200 OK

{
    "Response": [{"Decision": "Deny"}]
}
```

- More info on the JSON Profile of XACML:
http://docs.oasis-open.org/xacml/xacml-json-http/v1.0/cos01/xacml-json-http-v1.0-cos01.html#_Toc497727120


Example of the policy set for reference:

```
{
    "PolicySetId": "HospitalRules",
    "Policies": [
        {
            "PolicyId": "NormalPolicy", 
            "Rules": [
                {
                    "RuleId": "DiseasesRule",
                    "Effect": "Permit",
                    "Target": {
                        "Subjects": [{
                            "AttributeId": "Subject-Role",
                            "Value": "Doctor"
                        },
                        {
                            "AttributeId": "Subject-Role",
                            "Value": "Nurse"
                        }],
                        "Resources": [{
                            "AttributeId": "resource-id",
                            "Value": "/patients/{id}/diseases"
                        }],
                        "Actions": [{
                            "AttributeId": "action-id",
                            "Value": "GET"
                        }]
                    }
                },
                {
                    "RuleId": "Default",
                    "Effect": "Deny"
                }
            ]
        },
        {
            "PolicyId": "PandemicPolicy", 
            "Rules": [
                ...
            ]
        }
    ]
}
```

- PS: The rules for normal mode and pandemic mode are separated in two different policies. The policy that is evaluated depends on the mode the API is operating (normal or pandemic).