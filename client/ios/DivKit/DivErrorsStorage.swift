import BasePublic
import Serialization

public typealias DivError = CustomStringConvertible

public enum DivErrorLevel {
  case warning
  case error
}

public final class DivErrorsStorage {
  public private(set) var errors: [DivError] = []

  public init(errors: [DivError]) {
    self.errors = errors
  }

  public func add(_ error: DivError) {
    errors.append(error)
  }
}

extension DivErrorsStorage {
  public convenience init<T>(from deserializationResult: DeserializationResult<T>) {
    self.init(errors: deserializationResult.errorsOrWarnings?.asArray() ?? [])
  }
}
