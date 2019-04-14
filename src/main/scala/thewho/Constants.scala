package thewho

object Constants {

  val PRIVATE_KEY_TEST =
    """
      |-----BEGIN RSA PRIVATE KEY-----
      |MIICXQIBAAKBgQDKf5R8afDTeQm/UfSScSJNXtll6Kr0sU4J2iSTpGHCp3dYca3D
      |vPLZlcl+KbPSsd8kA01hbexz21ia91X2wIJEJDfxBLcUPwHH6zVO+w1CemRMDijy
      |gs1BMgKCwYD3vbrlewwdTXSlSH8oP5Sw1EP3Wai5YIoD726HbwvDHuer4QIDAQAB
      |AoGAURoX1lx337qydLXWRnCZVHOQjohJMAdzQe7kh11m6hc4bWvaNHMQsKSVNrpt
      |Ew26WSoZa9Qix6QqMKau/0zK3tjR3v4+NNx/p/fYe+a37KzaqPq5OKf+YDwJR6Ul
      |Lp89u7bhftDjkRsSXPulhWcGdEeWbMFVS2bD/xP6mvbUUYECQQD2QEskmsRDg6sA
      |UpzbrLT+JILCQ0YJUzlpg93Unr837wQ9f0fI0/sBKl7PZhZ6XGTudAOK30zlCd/a
      |TgcaRkU9AkEA0oPcVfSlPMFnq5pUgBdBDWHTixNxAfOrTM9xNco6+e7iXaVHQVjI
      |4LHKyLdK4J26YvylX6xOrnxRNSTkz6iTdQJBAILdMBHH3b0zX9DQFmP3qEfXB1ZN
      |gg66l6wnU+AGbQBAmTRFS9TCMoYwCiqVyiREra67IJbQjIC7eqb9CHFCHtECQQCp
      |5Oc+q6lrRf8/29aUZbAHd8r9M9yBIcE+xS/tpF9CdEtWHlxKXbgItxQcNukEK+dP
      |mexxMSNy6Du1syfCe589AkAXqZ5eS9URlqW3ah3yV3CY9ET19un6DmPgaB8IDc0w
      |YBnuxEVoO+q8ZF1tKeTvN9O3fJd8F1ideZBdXJxQ5nBF
      |-----END RSA PRIVATE KEY-----
    """.stripMargin

  val PUBLIC_KEY_TEST =
    """
      |-----BEGIN PUBLIC KEY-----
      |MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQDKf5R8afDTeQm/UfSScSJNXtll
      |6Kr0sU4J2iSTpGHCp3dYca3DvPLZlcl+KbPSsd8kA01hbexz21ia91X2wIJEJDfx
      |BLcUPwHH6zVO+w1CemRMDijygs1BMgKCwYD3vbrlewwdTXSlSH8oP5Sw1EP3Wai5
      |YIoD726HbwvDHuer4QIDAQAB
      |-----END PUBLIC KEY-----
    """.stripMargin

  val ANOTHER_PRIVATE_KEY_TEST =
    """
      |-----BEGIN RSA PRIVATE KEY-----
      |MIICXAIBAAKBgQDCJZkZiLAZTOr9nHyTMbX94dO85zGSsY8lWA71poXKuT7c0EV4
      |t3eKm8XCc2D1vefYayUm3PnkqypRCIod/iTPfv7gkOS4zLnhN5x5z1xFp5lXD6TM
      |3rAvrs2gezncnn/p49h/w/RRIjZUXBL8dsh8b1rX4djJ2D3zF+svFZWyIQIDAQAB
      |AoGAVvMNwTDFZV9cct6tsCJ4Vk7YTGbifcdFy7zHXl8I1oTmUXB5XJoWUJ55ECAq
      |UYSOnA9IYUJgLHg4CHihmtMiWn8Gy5SdpLKIHBLLW+bK+30I7KxMnEad/bNplpYV
      |hYLUkyK/S73WCBwnLK9JEenraf+ggGD3hLb0ZaSLgFFm3x0CQQD0WsNjxEbcd9q/
      |6my8/PUeUt5RQtbhXY8UIX+/ELp2/6NpFbePz2jUNSXUbpISJ0xJ9YfvDcCIM6sa
      |ArcHAtuHAkEAy2ZHPS6ZIjycwezC+hUfZW7SgAJPahe+AuXilbIU1SxVu0YdLCXm
      |9/fXhhDR4UOyjHNl96kwYhgBJLPOfjR/FwJBALRt8KjSXaCdv6DGcNR71GM3vYEz
      |YYe3lda/hZyHtaf4y6jkqE5AUJ+hPvXFT4aoDeTusBkMhoYL4OOr7Yn3yjECQBow
      |Iv/3nfPQhgZZ64iza4VtzWB566unPum0m4XWleQUfz1Le7oRbzmCCZfTVpKAWt7G
      |hNxsRjR7DrqhA/cv3yUCQEp+LBWwboKdFL0WSDPscCNzeLFrMQqupaPHd2qCgFrw
      |j0TKfHG9rP6NbfFb1lYBcgwaJthPwlt4XjCi45SHRV0=
      |-----END RSA PRIVATE KEY-----
    """.stripMargin

  val ANOTHER_PUBLIC_KEY_TEST =
    """
      |-----BEGIN PUBLIC KEY-----
      |MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQDCJZkZiLAZTOr9nHyTMbX94dO8
      |5zGSsY8lWA71poXKuT7c0EV4t3eKm8XCc2D1vefYayUm3PnkqypRCIod/iTPfv7g
      |kOS4zLnhN5x5z1xFp5lXD6TM3rAvrs2gezncnn/p49h/w/RRIjZUXBL8dsh8b1rX
      |4djJ2D3zF+svFZWyIQIDAQAB
      |-----END PUBLIC KEY-----
    """.stripMargin

}
