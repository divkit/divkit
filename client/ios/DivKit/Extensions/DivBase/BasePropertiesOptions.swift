struct BasePropertiesOptions: OptionSet {
  static let noPaddings = BasePropertiesOptions(rawValue: 1)

  let rawValue: Int
  init(rawValue: Int) {
    self.rawValue = rawValue
  }
}
