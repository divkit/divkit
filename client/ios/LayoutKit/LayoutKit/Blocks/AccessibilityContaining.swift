// Copyright 2021 Yandex LLC. All rights reserved.

import BaseUI

public protocol AccessibilityContaining {
  var accessibilityElement: AccessibilityElement? { get }
}

extension AccessibilityContaining {
  public var accessibilityElement: AccessibilityElement? { nil }
}
