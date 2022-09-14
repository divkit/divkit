final class ExpressionErrorsStorage {
  private(set) var errors: [ExpressionError] = []

  func add(error: ExpressionError) {
    errors.append(error)
  }
}
