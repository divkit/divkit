import LayoutKit

@MainActor
final class DivTooltipViewRegistry {
  private let idToPath: IdToPath
  private let animatorController: DivAnimatorController
  private let triggersStorage: DivTriggersStorage
  private let functionsStorage: DivFunctionsStorage

  private var openViewCounts = [DivViewId: Int]()

  init(
    idToPath: IdToPath,
    animatorController: DivAnimatorController,
    triggersStorage: DivTriggersStorage,
    functionsStorage: DivFunctionsStorage
  ) {
    self.idToPath = idToPath
    self.animatorController = animatorController
    self.triggersStorage = triggersStorage
    self.functionsStorage = functionsStorage
  }

  func open(viewId: DivViewId) {
    openViewCounts[viewId, default: 0] += 1
  }

  func close(viewId: DivViewId) {
    guard let count = openViewCounts[viewId] else {
      DivKitLogger.error("Closing an unregistered tooltip view: \(viewId)")
      return
    }

    if count > 1 {
      openViewCounts[viewId] = count - 1
      return
    }

    openViewCounts[viewId] = nil
    idToPath.reset(viewId: viewId)
    animatorController.reset(viewId: viewId)
    triggersStorage.reset(viewId: viewId)
    functionsStorage.reset(viewId: viewId)
  }
}
