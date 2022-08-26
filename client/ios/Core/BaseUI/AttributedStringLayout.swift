// Copyright 2021 Yandex LLC. All rights reserved.

import CoreGraphics
import CoreText
import Foundation

public struct AttributedStringLayout<Action> {
  public struct Run {
    public let rect: CGRect
    public let action: Action
  }

  public let firstLineOriginX: CGFloat?
  public let runsWithAction: [Run]
  public let lines: [AttributedStringLineLayout]
}

public struct AttributedStringLineLayout: Equatable {
  public let line: CTLine
  public let verticalOffset: CGFloat
  public let horizontalOffset: CGFloat
  public let range: NSRange
  public let isTruncated: Bool
}
