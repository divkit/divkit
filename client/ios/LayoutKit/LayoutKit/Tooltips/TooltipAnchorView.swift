import VGSL

public protocol TooltipAnchorView: ViewType {
  var tooltips: [BlockTooltip] { get }
  var path: UIElementPath? { get }
}

extension TooltipAnchorView {
  public var path: UIElementPath? { nil }
}
