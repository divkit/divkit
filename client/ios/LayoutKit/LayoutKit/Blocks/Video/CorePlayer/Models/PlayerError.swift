import Foundation

protocol PlayerError: Error {}

struct BasePlayerError: PlayerError {
  let error: NSError

  init(_ error: NSError) {
    self.error = error
  }
}

struct UnknownPlayerError: PlayerError {
  var description: String {
    "Something went wrong"
  }
}
