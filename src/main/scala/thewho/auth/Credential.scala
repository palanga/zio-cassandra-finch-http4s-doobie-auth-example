package thewho.auth

// TODO #15 encrypt password
// TODO #16 add validation to phone numbers
/**
 * A simple class representing a pair of phone number and password used for user authentication.
 * In the future we will implement different methods like email + password, facebook auth, etc.
 *
 * @param id the phone number
 * @param secret the plain text password
 */
case class Credential(id: CredentialId, secret: CredentialSecret)
