// Copyright 2018 Yandex LLC. All rights reserved.

struct BasePropertiesOptions: OptionSet {
  static let noPaddings = BasePropertiesOptions(rawValue: 1)

  let rawValue: Int
  init(rawValue: Int) {
    self.rawValue = rawValue
  }
}
