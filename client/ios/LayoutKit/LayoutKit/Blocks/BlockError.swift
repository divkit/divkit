import Foundation

import CommonCorePublic

public protocol BlockError: Error {
  var errorMessage: NonEmptyString { get }
  var userInfo: [String: String] { get }
}

// Workaround for swift compiler bug, when protocol is not conforming parent protocol
@inlinable
public func modifyError<T: Error, R>(
  _ modificator: (BlockError) -> T,
  _ block: () throws -> R
) throws -> R {
  do {
    return try block()
  } catch let e as BlockError {
    throw modificator(e)
  }
}

extension BlockError {
  public var userInfo: [String: String] { [:] }
}
