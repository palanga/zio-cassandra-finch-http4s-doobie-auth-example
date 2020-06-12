the-who
=======
A Scala and ZIO playground which happens to be an auth service.

----

Run
===

Finch server and In memory DB 
-------------------------------
* `./sbt app/run`

Cassandra DB
------------
* `brew cask install java11`
* `brew install cassandra` (will also install `cqlsh`)
* Cassandra doesn't work with java 14 (we are using java 11)
* Some jvm options doesn't work so we have to edit `/usr/local/etc/cassandra/jvm.options`
* Launch with `cassandra -f`
* Stop with `ctrl c` or `ps` to get the PID and then `kill <PID>`
* Default host and port is `127.0.0.1:9042`
* `cqlsh` will connect there by default
* `CREATE KEYSPACE IF NOT EXISTS thewho_dummy WITH REPLICATION = { 'class' : 'SimpleStrategy', 'replication_factor' : 1 };`
* Edit `app/Main` and `build.sbt` to use cassandra instead of in memory db


Http4s server
-------------
* Edit `app/Main` and `build.sbt` to use http4s instead of finch. See `server/http4s/Server`

Postgres DB
-----------
* Edit `app/Main` and `build.sbt` to use postgres instead of in memory db
* Add `127.0.0.1   postgres` to your `/etc/hosts` file
* `docker-compose up`
* `docker exec -it postgres bash`
* `psql -d postgres -U postgres`


Endpoints
=========

| ENDPOINT            | METHOD     | INPUT                           | OUTPUT                     | STATUS CODES                |
|---------------------|------------|------------------------------   | ---------------------------|-----------------------------|
| /signup             | POST       | `Credential`                    | `TokenResponse`            | 201, 209, 422, 500          |
| /login              | GET, POST  | `Credential`                    | `TokenResponse`            | 200, 401 403, 404, 422, 500 |
| /change-password    | POST       | `CredentialSecretUpdateRequest` | `TokenResponse`            | 200, 401 403, 404, 422, 500 |
| /signout            | POST       | `Credential`                    | `empty`                    | 204, 401 403, 404, 422, 500 |
| /find-credential-id | POST       | `UserCredentialIdRequest`       | `UserCredentialIdResponse` | 200, 401 403, 404, 422, 500 |

```
Credential:                    { id: String, secret: String }
CredentialSecretUpdateRequest: { oldCredential: Credential, newSecret: String }
TokenResponse:                 { token: String }
UserCredentialIdRequest:       { token: String }
UserCredentialIdResponse:      { credentialId: String }
```

Test
====

Load test (Gatling)
-------------------
* `./sbt gatling/gatling:test`

Other tests
-----------
* `./sbt test`

Troubleshooting
===============
* To generate key pairs:
```shell script
openssl genrsa -out private.pem 1024
openssl rsa -in private.pem -pubout -outform PEM -out public_key.pem
openssl pkcs8 -topk8 -inform PEM -in private.pem -out private_key.pem -nocrypt
```
the las line is needed because of the format java uses.

* Sending json thru httpie:
```shell script
echo '{ "id": "Salvador", "secret": "Dalí" }' | http POST http://127.0.0.1:8080/signup -v
echo '{ "oldCredential": { "id": "Salvador", "secret": "Dalí" }, "newSecret": "lanadanisman" }' | http POST http://127.0.0.1:8080/change-password -v  
``` 
