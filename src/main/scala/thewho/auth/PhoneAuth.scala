package thewho.auth

case class PhoneAuth(id: AuthId, secret: AuthSecret) extends AuthInfo
