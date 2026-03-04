import Foundation

public protocol PlayerError: Error {
  var errorDescription: String { get }
}

struct BasePlayerError: PlayerError {
  private let error: NSError

  var errorDescription: String {
    error.localizedDescription
  }

  init(_ error: NSError) {
    self.error = error
  }
}

struct CustomPlayerError: PlayerError {
  let errorDescription: String

  init(errorDescription: String? = nil) {
    self.errorDescription = errorDescription ?? "Something went wrong"
  }
}
