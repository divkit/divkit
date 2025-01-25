import Foundation

public typealias DivArray = [AnyHashable]

extension DivArray {
  static func fromAny(_ value: [Any]) -> DivArray? {
    NSArray(array: value) as? DivArray
  }
}

public typealias DivDictionary = [String: AnyHashable]

extension DivDictionary {
  static func fromAny(_ value: [String: Any]) -> DivDictionary? {
    NSDictionary(dictionary: value) as? DivDictionary
  }
}
