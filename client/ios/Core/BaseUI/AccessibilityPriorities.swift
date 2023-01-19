// Copyright 2022 Yandex LLC. All rights reserved.

import SwiftUI

public typealias AccessibilityPriorities<Tag> = (Tag) -> Double

@available(iOS 14.0, macOS 11.0, *)
extension View {
  public func accessibilityPriority<Tag>(
    of tag: Tag, in priorities: AccessibilityPriorities<Tag>
  ) -> some View {
    self.accessibilitySortPriority(priorities(tag))
  }

  public func accessibilityPriority<Tag: RawRepresentable<BinaryInteger>>(
    for tag: Tag
  ) -> some View {
    self.accessibilitySortPriority(Double(tag.rawValue))
  }
}
