#if os(iOS)
@_spi(Internal) @testable import DivKit
import LayoutKit
import VGSL

/// Models the host, then the displayed tooltip, then a host re-model through
/// `DivBlockProvider.update`, which calls `resetIdToPath(viewId:)` the same way production does.
///
/// `anchorPath` must be the path the tooltip's anchor gets while modeling `host`, since that is
/// what production feeds into `DivViewId.Tooltip` (see `cloneForTooltip`). Getting it wrong
/// would give the tooltip provider an identity no real modeling pass ever produces, and the
/// reset this helper exists to exercise would silently match nothing.
@MainActor
public func remodelHostAndTooltipThroughProviders(
  components: DivKitComponents,
  cardId: DivCardID,
  host: Div,
  tooltipContent: Div,
  tooltipId: String,
  anchorPath: UIElementPath
) {
  let tooltip = DivViewId.Tooltip(id: tooltipId, anchorPath: anchorPath)
  let hostProvider = DivBlockProvider(
    id: DivViewId(cardId: cardId),
    divKitComponents: components,
    onCardSizeChanged: { _, _ in }
  )
  let tooltipProvider = DivBlockProvider(
    id: DivViewId(cardId: cardId, tooltip: tooltip),
    divKitComponents: components,
    onCardSizeChanged: { _, _ in }
  )

  hostProvider.setSource(
    DivViewSource(kind: .divData(divData(host)), cardId: cardId),
    debugParams: DebugParams()
  )
  tooltipProvider.setSource(
    DivViewSource(
      kind: .divData(divData(tooltipContent, logId: tooltipId)),
      cardId: cardId,
      tooltip: tooltip
    ),
    debugParams: DebugParams()
  )
  hostProvider.update(reasons: [.external])
}
#endif
