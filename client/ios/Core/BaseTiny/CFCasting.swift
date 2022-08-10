// Copyright 2019 Yandex LLC. All rights reserved.

import CoreGraphics

public func safeCFCast(_ value: CFTypeRef) -> CFString? {
  if CFGetTypeID(value) == CFStringGetTypeID() {
    // swiftlint:disable unsafe_cf_cast
    return (value as! CFString)
    // swiftlint:enable unsafe_cf_cast
  } else {
    return nil
  }
}

public func safeCFCast(_ value: CFTypeRef) -> CGColor? {
  if CFGetTypeID(value) == CGColor.typeID {
    // swiftlint:disable unsafe_cf_cast
    return (value as! CGColor)
    // swiftlint:enable unsafe_cf_cast
  } else {
    return nil
  }
}
