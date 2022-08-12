// Copyright 2019 Yandex LLC. All rights reserved.

import CoreGraphics
import Foundation

import CommonCore

public struct TabViewState: ElementState, Equatable {
  public let selectedPageIndex: CGFloat

  public static let `default` = TabViewState(selectedPageIndex: 0)

  public init(selectedPageIndex: CGFloat) {
    self.selectedPageIndex = selectedPageIndex
  }
}
