// Copyright 2019 Yandex LLC. All rights reserved.

import CoreGraphics
import Foundation

protocol TabListSelectionDataSource: AnyObject {
  var numberOfTabs: Int { get }
  func modelForItemSelection(_ selection: CGFloat) -> TabTitlesViewModel
}
