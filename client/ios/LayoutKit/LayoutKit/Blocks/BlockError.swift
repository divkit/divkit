import Foundation

public struct BlockError: Error {
  public let message: String

  init(_ message: String) {
    self.message = message
  }
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
