import Foundation

extension URL {
  public static func makeFromNonEncodedString(_ string: String) -> URL? {
    URL(string: string.percentEncodedExceptSpecialAndLatin)
  }
}
