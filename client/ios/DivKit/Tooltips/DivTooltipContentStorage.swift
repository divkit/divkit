@_spi(Internal) import LayoutKit
import VGSL

/// One `TooltipContentHolder` per tooltip `DivViewId`, mutated in place across remodels so a
/// tooltip's content refreshing never makes `DecoratingBlock.equals` see a change.
///
/// Thread-safe, and shared by every modeling pass of a card. `holder(for:)` keeps a single
/// holder per `viewId`; when two passes model that view at once, the last write wins.
///
/// `DivViewId` is enough to key on because it already carries the anchor - see
/// `DivViewId.Tooltip`, which exists precisely because `div_tooltip_id` is only unique within
/// one anchor.
///
/// Holders are weak: the live block tree is what keeps one alive. A strong reference here would
/// leak `DivKitComponents`, since a `.factory` holder's closure captures `tooltipViewFactory`,
/// which holds it back.
final class DivTooltipContentStorage {
  private struct WeakHolder {
    weak var holder: TooltipContentHolder?
  }

  private var holders = [DivViewId: WeakHolder]()
  private let lock = AllocatedUnfairLock()

  func holder(
    for viewId: DivViewId,
    makeTooltip: () -> BlockTooltip
  ) -> TooltipContentHolder {
    let tooltip = makeTooltip()
    let resolution = lock.withLock { () -> (holder: TooltipContentHolder, isNew: Bool) in
      if let existing = holders[viewId]?.holder {
        return (existing, false)
      }
      holders = holders.filter { $0.value.holder != nil }
      let created = TooltipContentHolder(tooltip: tooltip)
      holders[viewId] = WeakHolder(holder: created)
      return (created, true)
    }
    if !resolution.isNew {
      resolution.holder.update(tooltip)
    }
    return resolution.holder
  }

  func reset() {
    lock.withLock {
      holders.removeAll()
    }
  }

  /// Drops every holder belonging to `cardId`, including all of its tooltips'.
  func reset(cardId: DivCardID) {
    lock.withLock {
      holders = holders.filter { $0.key.cardId != cardId }
    }
  }
}
