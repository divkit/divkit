import BasePublic
import Serialization
import LayoutKitInterface
import LayoutKit

public final class DivErrorsStorage {
  public private(set) var errors: [DivError] = []

  public init(errors: [DivError]) {
    self.errors = errors
  }

  public func add(_ error: DivError) {
    errors.append(error)
  }

  public func add(contentsOf errorStorage: DivErrorsStorage) {
    self.errors.append(contentsOf: errorStorage.errors)
  }
}
