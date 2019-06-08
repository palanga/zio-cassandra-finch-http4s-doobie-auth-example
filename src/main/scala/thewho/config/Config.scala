package thewho.config

case class Config(db: DBConfig, server: ServerConfig)

case class DBConfig(
  driverName: DriverName,
  url: URL,
  username: Username,
  password: Password,
  connectionThreadPoolSize: ThreadPoolSize
)

case class ServerConfig(host: Hostname, port: PortNumber)
