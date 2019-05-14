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
- sbt 1.2.8
- scala 2.12.8
- Docker Desktop 2.0.0.0-mac82


Run
===

```bash
sbt
```

and once inside the `sbt` shell:

```bash
~reStart
```

it will listen for code changes and restart the server automatically.


Contribute
==========

1) Branch off from master
2) Code
3) `sbt scalafmt` to format the code
4) Write [good commit messages](https://github.com/erlang/otp/wiki/writing-good-commit-messages).
    Please reference the issue number in the commit message description
5) PR to master
6) Push force to your branch if you have to make changes
7) Rebase merge into master
