import Foundation

final class UpdateStructureActionHandler {
  func handle(_ action: DivActionUpdateStructure, context: DivActionHandlingContext) {
    let expressionResolver = context.expressionResolver
    guard let variableName = action.resolveVariableName(expressionResolver),
          let newValue = action.value.resolve(expressionResolver),
          let path = action.resolvePath(expressionResolver) else {
      return
    }
    let divVariableName = DivVariableName(rawValue: variableName)
    let pathComponents = path.split(separator: "/").map(String.init)
    guard !pathComponents.isEmpty else {
      DivKitLogger.error("Path is empty")
      return
    }

    let updatedValue: DivVariableValue?
    if let dict: DivDictionary = context.variablesStorage.getVariableValue(
      path: context.path,
      name: divVariableName
    ) {
      updatedValue = updateDictionary(dict, newValue: newValue, path: pathComponents)
    } else if let array: DivArray = context.variablesStorage.getVariableValue(
      path: context.path,
      name: divVariableName
    ) {
      updatedValue = updateArray(array, newValue: newValue, path: pathComponents)
    } else {
      DivKitLogger.error("Action requires array or dictionary variable")
      return
    }

    guard let updatedValue else { return }
    context.variablesStorage.update(
      path: context.path,
      name: divVariableName,
      value: updatedValue
    )
  }
}

private func updateArray(
  _ array: DivArray,
  newValue: AnyHashable,
  path: [String],
  pathIndex: Int = 0
) -> DivVariableValue? {
  guard let index = Int(path[pathIndex]) else {
    DivKitLogger.error("Unable to use '\(path[pathIndex])' as array index")
    return nil
  }

  let updatedElement: AnyHashable?
  if pathIndex == path.count - 1 {
    updatedElement = newValue
  } else {
    guard index >= 0, index < array.count else {
      logElementNotFound(path: path, pathIndex: pathIndex)
      return nil
    }

    updatedElement = updateElement(
      currentElement: array[index],
      path: path,
      pathIndex: pathIndex + 1,
      newValue: newValue
    )
  }
  guard let updatedElement else { return nil }

  var mutableArray = array
  mutableArray[insert: index] = updatedElement
  return .array(mutableArray)
}

private func updateDictionary(
  _ dict: DivDictionary,
  newValue: AnyHashable,
  path: [String],
  pathIndex: Int = 0
) -> DivVariableValue? {
  let key = path[pathIndex]

  let updatedElement: AnyHashable?
  if pathIndex == path.count - 1 {
    updatedElement = newValue
  } else {
    guard let currentElement = dict[key] else {
      logElementNotFound(path: path, pathIndex: pathIndex)
      return nil
    }

    updatedElement = updateElement(
      currentElement: currentElement,
      path: path,
      pathIndex: pathIndex + 1,
      newValue: newValue
    )
  }
  guard let updatedElement else { return nil }

  var mutableDict = dict
  mutableDict[key] = updatedElement
  return .dict(mutableDict)
}

private func updateElement(
  currentElement: AnyHashable,
  path: [String],
  pathIndex: Int,
  newValue: AnyHashable
) -> AnyHashable? {
  if let nestedArray = currentElement as? DivArray {
    return updateArray(nestedArray, newValue: newValue, path: path, pathIndex: pathIndex)?
      .arrayValue
  } else if let nestedDict = currentElement as? DivDictionary {
    return updateDictionary(nestedDict, newValue: newValue, path: path, pathIndex: pathIndex)?
      .dictValue
  } else {
    DivKitLogger.error(
      "Element with path '\(path[0..<pathIndex].joined(separator: "/"))' is not a structure"
    )
    return nil
  }
}

extension Array {
  fileprivate subscript(insert index: Int) -> Element? {
    get {
      (0..<count).contains(index) ? self[index] : nil
    }
    set {
      guard let newValue else { return }
      guard index >= 0, index <= count else {
        DivKitLogger.error("Position '\(index)' is out of array bounds")
        return
      }

      if index == count {
        append(newValue)
      } else {
        self[index] = newValue
      }
    }
  }
}

extension DivVariableValue {
  fileprivate var arrayValue: DivArray? {
    if case let .array(array) = self {
      return array
    }
    return nil
  }

  fileprivate var dictValue: DivDictionary? {
    if case let .dict(dict) = self {
      return dict
    }
    return nil
  }
}

private func logElementNotFound(path: [String], pathIndex: Int) {
  DivKitLogger
    .error("Element with path '\(path[0...pathIndex].joined(separator: "/"))' is not found")
}
