import Foundation
import LayoutKit
import VGSL

extension DivText {
  func makeImages(
    _ images: [Image]?,
    imageBuilder: ImageBuilder?,
    context: DivBlockModelingContext
  ) -> [(Image, DivBlockModelingContext)] {
    if let imageBuilder {
      return imageBuilder.makeImages(context: context)
    }
    return (images ?? []).map { ($0, context) }
  }
}

extension DivText.ImageBuilder {
  func makeImages(
    context: DivBlockModelingContext
  ) -> [(DivText.Image, DivBlockModelingContext)] {
    let items = resolveData(context.expressionResolver) ?? []
    return items.enumerated().compactMap { index, item -> (
      DivText.Image,
      DivBlockModelingContext
    )? in
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
      guard let prototype = prototypes.first(where: {
        $0.resolveSelector(itemContext.expressionResolver)
      }) else {
        return nil
      }
      return (prototype.image, itemContext)
    }
  }
}
