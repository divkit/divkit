// Copyright 2021 Yandex LLC. All rights reserved.

import Foundation

extension Encodable {
  public func toJSONString() throws -> String {
    let data = try JSONEncoder().encode(self)
    return String(data: data, encoding: .utf8)!
  }
}
