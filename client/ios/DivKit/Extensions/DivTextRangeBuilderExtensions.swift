import Foundation
import LayoutKit
import VGSL

extension DivText {
  func makeRanges(
    _ ranges: [Range]?,
    rangeBuilder: RangeBuilder?,
    context: DivBlockModelingContext
  ) -> [(Range, DivBlockModelingContext)] {
    if let rangeBuilder {
      return rangeBuilder.makeRanges(context: context)
    }
    return (ranges ?? []).map { ($0, context) }
  }
}

extension DivText.RangeBuilder {
  func makeRanges(
    context: DivBlockModelingContext
  ) -> [(DivText.Range, DivBlockModelingContext)] {
    let items = resolveData(context.expressionResolver) ?? []
    return items.enumerated().compactMap { index, item in
      let itemDict = item as? DivDictionary ?? [:]
      let itemContext = context.modifying(
        pathSuffix: String(index),
        prototypeParams: PrototypeParams(
          index: index,
          variableName: dataElementName,
          value: itemDict
        )
      )
      itemContext.variablesStorage.initializeIfNeeded(path: itemContext.path, variables: [
        DivVariableName(rawValue: dataElementName): .dict(itemDict),
        "index": .integer(index),
      ])
      let prototype = prototypes.first(where: {
        $0.resolveSelector(itemContext.expressionResolver)
      })
      return if let prototype {
        (prototype.range, itemContext)
      } else {
        nil
      }
    }
  }
}
