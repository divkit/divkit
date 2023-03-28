// Copyright 2022 Yandex LLC. All rights reserved.

import Foundation

#if os(iOS)
extension NSKeyedUnarchiver {
  public class func legacyUnarchiveObject(with data: Data) -> Any? {
    unarchiveObject(with: data)
  }
}
#endif
