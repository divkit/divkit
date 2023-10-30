import BaseTinyPublic
import Serialization

extension DeserializationError {
  func asCardError(cardId: DivCardID) -> DeserializationError {
    .nestedObjectError(field: cardId.rawValue, error: self)
  }
}

extension DeserializationResult {
  public func asCardResult(cardId: DivCardID) -> Self {
    switch self {
    case let .partialSuccess(value, warnings):
      return .partialSuccess(value, warnings: warnings.map { $0.asCardError(cardId: cardId) })
    case let .failure(errors):
      return .failure(errors.map { $0.asCardError(cardId: cardId) })
    default:
      return self
    }
  }
}
