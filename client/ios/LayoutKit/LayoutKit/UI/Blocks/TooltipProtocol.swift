import UIKit

public protocol TooltipProtocol {
  func makeTooltipEvent(with info: TooltipInfo) -> TooltipEvent?
}

public func makeTooltip(with info: TooltipInfo, rootView: UIView) -> TooltipEvent? {
  if let tooltip = (rootView as? TooltipProtocol)?
    .makeTooltipEvent(with: info) {
    return tooltip
  }

  for subview in rootView.subviews {
    if let tooltip = makeTooltip(with: info, rootView: subview) {
      return tooltip
    }
  }

  return nil
}
