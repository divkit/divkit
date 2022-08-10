import CoreGraphics
import Foundation

public struct SwitchableContainerBlockState: ElementState, Equatable {
  public let selectedItem: SwitchableContainerBlock.Selection

  public init(selectedItem: SwitchableContainerBlock.Selection) {
    self.selectedItem = selectedItem
  }
}
