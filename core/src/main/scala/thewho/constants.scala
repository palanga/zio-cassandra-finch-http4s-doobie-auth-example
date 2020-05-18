package thewho

object constants {

  val PRIVATE_KEY_TEST =
    """
      |-----BEGIN PRIVATE KEY-----
      |MIICdQIBADANBgkqhkiG9w0BAQEFAASCAl8wggJbAgEAAoGBAMJeRP5akvFp4yQL
      |3NPv6kWo6GrYuhGuaB+Tjtyh3dJ+ANmf8G4k/dFxZ3G2ztyR+t/t2gTQ0+ulXqyr
      |2Mza5g+Frvnz9OOJSQMogJcomxX6Y7Wmg5P1UsGWNbToNC8N7/fl9jH5bJht1+lp
      |wWPyathHBW+v+HKBHaZa1V0BYqaDAgMBAAECgYBidHgEU4LtcJKTFsM0Q+nqgUXB
      |oaiW/j5WCFusXP3M+vCZTA/w6yH67rPUgSJFzlhDB+uQ1usSCYFse9vk4Nj5jrUt
      |DyRtS3ALAaoGFvgP4UkDwf4tguJYuQc2W08GgGHjuKZcy+AX2lWjshABGC4QVKma
      |GYaQWzjzGuoGPGoBuQJBAPcb6NT/Li/vg+yWkzpGCCd3h8ueBmOkNTMe1pUSR8fx
      |PgZayD1AHeCE64KalKXqY48yEUd6cIDoymFAzxm2zOcCQQDJXJI7mMIBND8xJA/g
      |T73t/6SJHcYh+zRBtttYFI5jFnAxeN6d9fXRVg+OlEQijL6DDGPGQHCWADPNfg8A
      |H2oFAkA+28OjgvId0YCuizMSbMQgPgC2JBGeASRjR6HwM8E8tDpB5Y/wPMIc3dMG
      |2wmbFv+SansIwrWhpRXuHU1RTjXbAkAWSngpvsxjQ7xnHT3gsBMvgKhfQXuIkX2E
      |dNCtXIEyuO4sm9vREsRmqydHumQciwYgDcQR01pjUJxreDQC6HddAkARkdF7zJQh
      |b5vUHqzDc1y//RXkESwUw/31EMon1zELh2PrKNX0MJCOnwQC1yvnI/5g2qhthirc
      |8DcHqBOO4XjG
      |-----END PRIVATE KEY-----
    """.stripMargin

  val PUBLIC_KEY_TEST =
    """
      |-----BEGIN PUBLIC KEY-----
      |MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQDCXkT+WpLxaeMkC9zT7+pFqOhq
      |2LoRrmgfk47cod3SfgDZn/BuJP3RcWdxts7ckfrf7doE0NPrpV6sq9jM2uYPha75
      |8/TjiUkDKICXKJsV+mO1poOT9VLBljW06DQvDe/35fYx+WyYbdfpacFj8mrYRwVv
      |r/hygR2mWtVdAWKmgwIDAQAB
      |-----END PUBLIC KEY-----
    """.stripMargin

  val ANOTHER_PRIVATE_KEY_TEST =
    """
      |-----BEGIN PRIVATE KEY-----
      |MIICeAIBADANBgkqhkiG9w0BAQEFAASCAmIwggJeAgEAAoGBANeQ9nQ+VXpqX6kx
      |Tfe+j1oHC+rb0vWgz1+2R0sCJPJRrAZEyPkVzEBJ7AKqGNCaUeyYQYmjmUanOMe/
      |6FSUK/vJmNXA2PgwRlywcmV0c+Y2VC4xXh82gj6cXgGdxY2gir0qBqLp/nEN2AC5
      |o1yN6aGayazpMXeB01Z0JKhRag/LAgMBAAECgYEAuo1Ne9PmDYa5OqbcnqZfUL4A
      |ZkPPEZsVW/NXR2aXqs1bXiFw+38NwCU5BiVGTGEAvQwWy5K1NxvIW7WMzoVceXeg
      |zilRlM8bcdDG+Zl08RW9p+jA/HAdvveIKtm85GYmu0psjMyl5w/oRXLjL0QUzVij
      |mQg3mJ4uX77YS4B1R+ECQQDtxxB+v6uNnYVhtVvsGLanNlkze3lRdeHevBxZpC78
      |WVxtKaHU15rS3e9hWWGgNXv/6csOVWxcgySGlwBI1KCHAkEA6BYi4ZQilR4DUwgr
      |vqK+akWwkG7EONAipwgX62ROuKcpTdg5IzjpO+aP96ytK2WaDWkOPBBqvP8slATh
      |UXe7nQJBAI8HVVbI+OrgvCEANOEaJJUEzjd3qIxluo5+3RbW+iR1pHFNv7kGUG1T
      |bvFCEMWMJqDUA38Fx38Gq+wB3PvyWvUCQQDg9TPyO0gaCfFm9jCQo+a1078FAJiq
      |CPTNuoaU37F+QmlCzybzASLxsNYzV+iye9UK0p29kpwjfaOUBfbwpIHxAkB3w0J4
      |RT3sI2kyvxTvCtr0xFosLW4VehW7hmsjkX06TTm1SBENyivp0i83Hfu1DL40Ot3y
      |MzdMcGr7PDqZo48P
      |-----END PRIVATE KEY-----
    """.stripMargin

  val ANOTHER_PUBLIC_KEY_TEST =
    """
      |-----BEGIN PUBLIC KEY-----
      |MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQDXkPZ0PlV6al+pMU33vo9aBwvq
      |29L1oM9ftkdLAiTyUawGRMj5FcxASewCqhjQmlHsmEGJo5lGpzjHv+hUlCv7yZjV
      |wNj4MEZcsHJldHPmNlQuMV4fNoI+nF4BncWNoIq9Kgai6f5xDdgAuaNcjemhmsms
      |6TF3gdNWdCSoUWoPywIDAQAB
      |-----END PUBLIC KEY-----
    """.stripMargin

}
