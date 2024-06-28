import CommonCorePublic
import LayoutKit

public enum CardIDTag {}
public typealias DivCardID = Tagged<CardIDTag, String>

extension DivCardID {
  var path: UIElementPath {
    UIElementPath(rawValue)
  }
}

extension UIElementPath {
  var cardId: DivCardID {
    DivCardID(rawValue: root)
  }
}
