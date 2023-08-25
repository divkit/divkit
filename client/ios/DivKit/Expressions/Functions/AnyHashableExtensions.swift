extension AnyHashable {
  internal var isBool: Bool {
    Bool(description) != nil && !(self is String)
  }

  internal var actualType: String {
    if self.isBool {
      return "boolean"
    }
    switch self {
    case is [String: AnyHashable]:
      return "dict"
    case is [Any]:
      return "array"
    case is String:
      return "string"
    case is Int, is Double:
      return "number"
    default:
      return "null"
    }
  }
}
