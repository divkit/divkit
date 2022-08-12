// Copyright (C) 2018 Yandex LLC. All rights reserved.
// Author: Denis A. Malykh <mrdekk@yandex-team.ru>

import CoreGraphics
import Foundation

public struct SwitchableContainerBlockState: ElementState, Equatable {
  public let selectedItem: SwitchableContainerBlock.Selection

  public init(selectedItem: SwitchableContainerBlock.Selection) {
    self.selectedItem = selectedItem
  }
}
