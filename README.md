the-who
=======

Who are you ?

Who do you claim you are ?

...

Ok, here's your access token:

![the who](https://pbs.twimg.com/profile_images/836889785528631297/g4iwfxBE.jpg)

----

Prerequisites
=============

- java 8
- Docker Desktop 2.0.0.0-mac82 (not needed if running in memory db)


Run
===

In memory DB and Finagle server
-------------------------------

```shell script
./sbt app/run
```

Http4s server
-------------
Add the dependency in the `build.sbt` file and follow the instructions in the `http4s.Server` trait.

Postgres DB
-----------

Add the dependency in the `build.sbt` file and change it in the `Main` function.

Add this line to your `/etc/hosts` file:

```
127.0.0.1   postgres
```

Inside the root directory, run the postgres database on docker:

```bash
docker-compose up
```

then:

```bash
./sbt
```

and once inside the `sbt` shell:

```bash
app/run
```


Alternatively you can use the revolver plugin to listen for code changes and restart the server automatically.


Debug
=====

To manually query the postgres database:

```bash
docker exec -it postgres bash
```

then, login to the database:

```bash
psql -d postgres -U postgres
```

now, you can run any query. Start with `\dt` 


Contribute
==========

1) Branch off from master
2) Code
3) `sbt scalafmtAll` to format the code before pushing.
4) Write [good commit messages](https://github.com/erlang/otp/wiki/writing-good-commit-messages).
    Please reference the issue number in the commit message description
5) PR to master
6) Push force to your branch if you have to make changes
7) If you have to update your branch with master, please `git rebase master` from your branch, and force push.
    Please do not merge master to your branch.
8) Rebase merge into master

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
