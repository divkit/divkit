import DivKit

extension Dictionary where Key == String {
  func makeDivActions(for key: String) -> [DivAction]? {
    let actions = (self[key] as? [[String: Any]])?.map {
      DivTemplates.empty.parseValue(type: DivActionTemplate.self, from: $0)
    }
    actions?.compactMap(\.warnings).reduce([], +).forEach { DivKitLogger.warning($0.errorMessage) }
    actions?.compactMap(\.errors).reduce([], +).forEach { DivKitLogger.error($0.errorMessage) }
    return actions?.compactMap(\.value)
  }
}
