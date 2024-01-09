import Foundation

public struct BlockError: Error {
  public let message: String

  init(_ message: String) {
    self.message = message
  }
}

// Workaround for swift compiler bug, when protocol is not conforming parent protocol
@inlinable
public func modifyError<R>(
  _ modificator: (BlockError) -> some Error,
  _ block: () throws -> R
) throws -> R {
  do {
    return try block()
  } catch let e as BlockError {
    throw modificator(e)
  }
}
