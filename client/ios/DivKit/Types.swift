import Foundation

public typealias DivArray = [AnyHashable]

extension DivArray {
  static func fromAny(_ value: [Any]) -> DivArray? {
    NSArray(array: value) as? DivArray
  }
}

extension DivArray {
  func isEqualUnordered(_ other: DivArray?) -> Bool {
    guard let other,
      self.count == other.count else {
      return false
    }
    
    return self.countElements() == other.countElements()
  }
}

public typealias DivDictionary = [String: AnyHashable]

extension DivDictionary {
  static func fromAny(_ value: [String: Any]) -> DivDictionary? {
    NSDictionary(dictionary: value) as? DivDictionary
  }
}

