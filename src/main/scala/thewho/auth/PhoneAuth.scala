package thewho.auth

// TODO #7 do we need these case classes ?
case class PhoneAuth(id: AuthId, secret: AuthSecret) extends AuthInfo
