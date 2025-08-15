public protocol PathIdentifiable {
  var path: UIElementPath? { get }
}

extension PathIdentifiable {
  public var path: UIElementPath? {
    nil
  }
}
