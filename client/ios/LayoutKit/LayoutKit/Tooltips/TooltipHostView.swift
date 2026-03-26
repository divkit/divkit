import Foundation

/// A view that can host tooltip display, serving as an alternative to the window-based approach.
///
/// Conforming to this protocol allows a view to act as a
/// container for tooltip presentation. When set on `DefaultTooltipManager`, tooltips will be
/// displayed within the host view's hierarchy instead of a separate `UIWindow`.
public protocol TooltipHostView {
  /// The bounds available for tooltip positioning, typically inset by safe area.
  var tooltipContainerBounds: CGRect { get }

  /// Space for converting tooltip coordinates into
  var coordinateSpace: ViewType { get }

  /// Adds a tooltip view to the host's view hierarchy.
  func addTooltipView(_ view: ViewType)
}
