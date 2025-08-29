import Foundation

@objc public protocol DivKitKMPErrorReporter {
  func report(cardId: String, message: String) -> Void
}
