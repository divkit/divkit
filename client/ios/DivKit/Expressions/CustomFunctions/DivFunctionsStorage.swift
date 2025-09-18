import LayoutKit
import VGSL

@_spi(Internal)
public final class DivFunctionsStorage {
  let outerStorage: DivFunctionsStorage?

  private var storages: [UIElementPath: DivFunctionsStorage] = [:]
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

  func set(cardId: DivCardID, functions: [DivFunction]) {
    reset(cardId: cardId)
    setIfNeeded(path: cardId.path, functions: functions)
  }

  func setIfNeeded(path: UIElementPath, functions: [DivFunction]) {
    lock.withLock {
      if storages[path] != nil {
        return
      }

      let nearestStorage = getNearestStorage(path.parent)
      if functions.isEmpty {
        storages[path] = nearestStorage
      } else {
        let storage = DivFunctionsStorage(outerStorage: nearestStorage)
        let cardId = path.cardId
        let expressionErrorTracker = reporter.asExpressionErrorTracker(cardId: cardId)
        for function in functions {
          functionNamesByCard[cardId, default: []].insert(function.name)
          storage.addFunction(
            function,
            reporter: expressionErrorTracker
          )
        }
        storages[path] = storage
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
      storages.keys
        .filter { $0.root == cardId.rawValue }
        .forEach { storages[$0] = nil }
      functionNamesByCard[cardId] = nil
    }
  }

  private func getNearestStorage(_ path: UIElementPath?) -> DivFunctionsStorage? {
    var currentPath: UIElementPath? = path
    while let path = currentPath {
      if let storage = storages[path] {
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
