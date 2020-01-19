# Spring Boot Security + JWT Hello World Example

In this tutorial we will be developing a Spring Boot Application that makes use of JWT authentication for securing an exposed REST API. In this example we will be making use of hard coded user values for User Authentication. In next tutorial we will be implementing Spring Boot + JWT + MYSQL JPA for storing and fetching user credentials (https://www.javainuse.com/spring/boot-jwt-mysql), Any user will be able to consume this API only if it has a valid JSON Web Token(JWT). In a previous tutorial we have seen what is JWT, when and how to use it.

# Lets Begin?

For better understanding we will be developing the project in stages
- Develop a Spring Boot Application to expose a Simple REST GET API with mapping /hello.
- Configure Spring Security for JWT. Expose REST POST API with mapping /authenticate using which User will get a valid JSON Web Token. And then allow the user access to the api /hello only if it has a valid token.


![Alt text](images/jwt.jpg?raw=true "Optional Title")

# Spring Security and JWT Configuration

We will be configuring Spring Security and JWT for performing 2 operations-
- Generating JWT - Expose a POST API with mapping /authenticate. On passing correct username and password it will generate a JSON Web Token(JWT)
- Validating JWT - If user tries to access GET API with mapping /hello. It will allow access only if request has a valid JSON Web Token(JWT)


The sequence flow for these operations will be as follows-

# Generating JWT

![Alt text](images/jwt2.jpg?raw=true "Optional Title")

![Alt text](images/jwt3.jpg?raw=true "Optional Title")


# Validating JWT

![Alt text](images/jwt4.jpg?raw=true "Optional Title")

#ref

https://www.javainuse.com/spring/boot-jwt

```
curl -X POST \
  http://localhost:8080/authenticate \
  -H 'content-type: application/json' \
  -d '{
  "username" : "javainuse",
  "password": "password"
}'
```

Response:

```
{
    "token": "eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJqYXZhaW51c2UiLCJleHAiOjE1Nzk0MjYwNDIsImlhdCI6MTU3OTQwODA0Mn0.GXswxB6cBIHxqx2pUI_Iw9BTC5lClYOU690YVhuK8CKdKs7gTD1uBcJi_J8sOvv01YY_NdwzwXu0zCyZGOrCOw"
}
```

Call the actual endpoint - 

```
curl -X GET \
  http://localhost:8080/hello \
  -H 'authorization: Bearer eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJqYXZhaW51c2UiLCJleHAiOjE1Nzk0MjYwNDIsImlhdCI6MTU3OTQwODA0Mn0.GXswxB6cBIHxqx2pUI_Iw9BTC5lClYOU690YVhuK8CKdKs7gTD1uBcJi_J8sOvv01YY_NdwzwXu0zCyZGOrCOw'
  
```  

Response

```
Hello World
```