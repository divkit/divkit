import Foundation

extension URL {
  public init?(stringToEncode: String) {
    self.init(string: stringToEncode.percentEncodedExceptSpecialAndLatin)
  }
}
