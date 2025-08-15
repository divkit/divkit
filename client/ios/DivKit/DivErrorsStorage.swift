public final class DivErrorsStorage {
  public private(set) var errors: [DivError] = []

  init(errors: [DivError]) {
    self.errors = errors
  }

  func add(_ error: DivError) {
    errors.append(error)
  }

  func add(contentsOf errorStorage: DivErrorsStorage) {
    self.errors.append(contentsOf: errorStorage.errors)
  }
}
