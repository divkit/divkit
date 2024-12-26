import VGSL

public protocol TooltipAnchorView: ViewType {
  var tooltips: [BlockTooltip] { get }
}
