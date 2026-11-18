import LayoutKit
import VGSL

@_spi(Internal)
public final class DivFunctionsStorage {
  let outerStorage: DivFunctionsStorage?

  private var storages: [DivCardID: [UIElementPath: DivFunctionsStorage]] = [:]
  private var functionNamesByCard: [DivCardID: Set<String>] = [:]

  private var functionSignatures: Set<CustomFunction.Signature> = []
  private var functionsByName: [String: [CustomFunction]] = [:]
  private let reporter: DivReporter

  private let lock = AllocatedUnfairLock()

  init(
    outerStorage: DivFunctionsStorage? = DivFunctionsStorage(outerStorage: nil),
    reporter: DivReporter = DefaultDivReporter()
  ) {
    self.outerStorage = outerStorage
    self.reporter = reporter
  }

  /// Replaces the host's function storages for new card data. Storages of tooltips that are open
  /// at this moment are left as they are, together with their link to the host storages they were
  /// modeled with: an open tooltip keeps evaluating against the functions of the DivData its
  /// content came from, and picks up the new ones when it is shown again (its storages are dropped
  /// on close by `DivTooltipViewRegistry`).
  func set(cardId: DivCardID, functions: [DivFunction]) {
    reset(viewId: DivViewId(cardId: cardId))
    setIfNeeded(path: cardId.path, functions: functions)
  }

  func setIfNeeded(path: UIElementPath, functions: [DivFunction]) {
    lock.withLock {
      let cardId = path.cardId
      if storages[cardId]?[path] != nil {
        return
      }

      let nearestStorage = getNearestStorage(path.parent)
      if functions.isEmpty {
        if let nearestStorage {
          storages[cardId, default: [:]][path] = nearestStorage
        }
      } else {
        let storage = DivFunctionsStorage(outerStorage: nearestStorage)
        let expressionErrorTracker = reporter.asExpressionErrorTracker(cardId: cardId)
        for function in functions {
          functionNamesByCard[cardId, default: []].insert(function.name)
          storage.addFunction(
            function,
            reporter: expressionErrorTracker
          )
        }
        storages[cardId, default: [:]][path] = storage
      }
    }
  }

  func getStorage(
    path: UIElementPath,
    contains name: String
  ) -> DivFunctionsStorage? {
    lock.withLock {
      guard functionNamesByCard[path.cardId]?.contains(name) == true else { return nil }

      var storage: DivFunctionsStorage? = getNearestStorage(path)
      while storage != nil {
        if let functions = storage?.getFunctions(with: name), !functions.isEmpty {
          return storage
        }
        storage = storage?.outerStorage
      }
      return nil
    }
  }

  func getFunctions(with name: String) -> [CustomFunction] {
    lock.withLock {
      functionsByName[name] ?? []
    }
  }

  func reset() {
    lock.withLock {
      storages.removeAll()
      functionNamesByCard.removeAll()
    }
  }

  func reset(cardId: DivCardID) {
    lock.withLock {
      storages[cardId] = nil
      functionNamesByCard[cardId] = nil
    }
  }

  /// Drops the storages registered by one view. `functionNamesByCard` is deliberately left
  /// alone: it is only a pre-filter in `getStorage`, so a stale name is harmless, while clearing
  /// it would make another view's functions of the same card unfindable - `setIfNeeded` skips
  /// existing paths and would never add their names back.
  func reset(viewId: DivViewId) {
    lock.withLock {
      let cardId = viewId.cardId
      guard let paths = storages[cardId]?.keys.filter({ $0.viewId == viewId }) else {
        return
      }
      for path in paths {
        storages[cardId]?[path] = nil
      }
      if storages[cardId]?.isEmpty == true {
        storages[cardId] = nil
      }
    }
  }

  private func getNearestStorage(_ path: UIElementPath?) -> DivFunctionsStorage? {
    guard let path, let cardStorages = storages[path.cardId] else {
      return outerStorage
    }
    var currentPath: UIElementPath? = path
    while let path = currentPath {
      if let storage = cardStorages[path] {
        return storage
      }
      currentPath = path.parent
    }
    return outerStorage
  }

  private func addFunction(_ function: DivFunction, reporter: ExpressionErrorTracker) {
    let signature = CustomFunction.Signature(
      name: function.name,
      arguments: function.arguments.map(\.type)
    )
    if functionSignatures.contains(signature) {
      reporter(
        ExpressionError(
          makeErrorMessage(
            name: function.name,
            arguments: function.arguments.map(\.type.rawValue)
          )
        )
      )
    } else {
      let customFunction = CustomFunction(
        name: function.name,
        arguments: function.arguments.compactMap { arg in
          CustomFunction.Argument(name: arg.name, type: arg.type)
        },
        body: function.body,
        returnType: function.returnType.systemType
      )
      functionsByName[function.name, default: []].append(customFunction)
      functionSignatures.insert(signature)
    }
  }
}

private func makeErrorMessage(name: String, arguments: [String]) -> String {
  "Function \(name)(\(arguments.joined(separator: ", "))) declared multiple times."
}
