import Serialization
import BaseTinyPublic

extension DeserializationError {
  func asCardError(cardId: DivCardID) -> DeserializationError {
    return .nestedObjectError(field: cardId.rawValue, error: self)
  }
}

extension DeserializationResult {
  public func asCardResult(cardId: DivCardID) -> Self {
    switch self {
    case .partialSuccess(let value, let warnings):
      return .partialSuccess(value, warnings: warnings.map { $0.asCardError(cardId: cardId) })
    case .failure(let errors):
      return .failure(errors.map { $0.asCardError(cardId: cardId) })
    default:
      return self
    }
  }
}
