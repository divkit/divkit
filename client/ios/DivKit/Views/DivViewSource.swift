import Foundation
import LayoutKit

/// A representation of the source from which a ``DivView`` can load content.
public struct DivViewSource {
  /// Specifies the type of the source for the `DivView`.
  public enum Kind {
    case json([String: Any])
    case data(Data)
    case divData(DivData)
  }

  let id: DivViewId
  let kind: Kind

  /// Initializes a new ``DivViewSource``.
  ///
  /// - Parameters:
  ///   - kind: The kind of data source, represented by the ``Kind`` enum.
  ///   - cardId: A unique identifier for the associated `DivKit` card.
  public init(
    kind: Kind,
    cardId: DivCardID
  ) {
    self.init(kind: kind, cardId: cardId, tooltip: nil)
  }

  init(
    kind: Kind,
    cardId: DivCardID,
    tooltip: DivViewId.Tooltip?
  ) {
    id = DivViewId(cardId: cardId, tooltip: tooltip)
    self.kind = kind
  }
}

/// Identity of one rendered view of a card: the host, or a single tooltip, which DivKit models
/// in a `DivView` of its own.
///
/// Distinct from `DivCardID`. The card is the logical entity shared by every view that presents
/// it: variables, states, timers, the pending-action queue, and addressing an element by id.
/// Tooltip state belongs there too, in the `tooltip#<id>` namespace, which carries the tooltip
/// id and not the anchor. `DivViewId` names one rendering of that card, and is used for three
/// things:
///
/// - which view this is — `DivBlockProvider` and `DivVariableTracker`;
/// - the modeling context (`DivBlockModelingContext.viewId`);
/// - who owns the registrations one view may drop without touching a sibling: `IdToPath`,
///   triggers, animators, and `DivTooltipViewRegistry`.
///
/// The view an action came from is recovered from the action path by `UIElementPath.viewId`.
///
/// `path` hangs a tooltip's elements off its anchor, so `cardId` is a prefix of a view's root
/// rather than the root itself.
struct DivViewId: Hashable {
  /// A tooltip's identity within its card.
  ///
  /// `div_tooltip_id` is only required to be unique among one element's tooltips, so two
  /// anchors may declare the same `id`. The anchor is therefore part of the identity: without
  /// it two same-named tooltips would share one `IdToPath` bucket, one trigger set and one
  /// animator subtree. Pairing the two in a type makes "a tooltip always knows its anchor"
  /// hold by construction.
  struct Tooltip: Hashable {
    let id: String
    let anchorPath: UIElementPath
  }

  /// Prefix of a tooltip's structural root (`tooltip#<id>`, appended after the anchor path).
  ///
  /// Reserved in element ids. Nested tooltips are not supported, so a path contains this
  /// marker at most once; `UIElementPath.viewId` recovers the tooltip from that segment.
  static let tooltipMarker = "tooltip#"

  let cardId: DivCardID
  let tooltip: Tooltip?

  /// The tooltip's own id, as authored. Stays the plain id for logs and for deriving the
  /// `tooltip#<id>` root of its `DivStatePath`s; neither use knows about anchors.
  var additionalId: String? {
    tooltip?.id
  }

  var isTooltip: Bool {
    tooltip != nil
  }

  var path: UIElementPath {
    guard let tooltip else {
      return cardId.path
    }
    // anchorPath already starts at cardId.
    return tooltip.anchorPath + "\(Self.tooltipMarker)\(tooltip.id)"
  }

  init(cardId: DivCardID, tooltip: Tooltip? = nil) {
    self.cardId = cardId
    self.tooltip = tooltip
  }

  /// Host card, or an additional card view without an element anchor
  /// (`makeContext(additionalId:)`).
  init(cardId: DivCardID, additionalId: String?) {
    self.init(
      cardId: cardId,
      tooltip: additionalId.map { Tooltip(id: $0, anchorPath: cardId.path) }
    )
  }
}

extension UIElementPath {
  /// The view this path belongs to.
  ///
  /// The `tooltip#` prefix in element ids is reserved. DivKit inserts one `tooltip#<id>`
  /// segment after a tooltip's anchor path, and nested tooltips are not supported, so a path
  /// contains that marker at most once. Walking from the leaf toward the root, the first
  /// segment with the prefix names the tooltip: `id` is the rest of the segment and
  /// `anchorPath` is the segment's parent. A path with no marker belongs to the host card.
  var viewId: DivViewId {
    var current: UIElementPath? = self
    while let path = current {
      if path.leaf.hasPrefix(DivViewId.tooltipMarker), let anchorPath = path.parent {
        let id = String(path.leaf.dropFirst(DivViewId.tooltipMarker.count))
        return DivViewId(
          cardId: cardId,
          tooltip: DivViewId.Tooltip(id: id, anchorPath: anchorPath)
        )
      }
      current = path.parent
    }
    return DivViewId(cardId: cardId)
  }
}
