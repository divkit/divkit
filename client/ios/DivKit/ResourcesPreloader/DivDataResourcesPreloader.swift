import Foundation
import VGSL

public enum ResourcePreloadFilter {
  case all, onlyRequired
}

public final class DivDataResourcesPreloader {
  private let resourceRequester: URLResourceRequesting

  public init(
    resourceRequester: URLResourceRequesting
  ) {
    self.resourceRequester = resourceRequester
  }

  public func downloadResources(
    for divData: DivData,
    filter: ResourcePreloadFilter,
    context: DivBlockModelingContext,
    completion: @escaping (Bool) -> Void
  ) {
    let extensionHandlers = context.extensionHandlers
    let validURLs = divData.flatMap(
      {
        let extensionURLs = $0.makeExtensionPreloadURLs(
          extensionHandlers: extensionHandlers,
          expressionResolver: $1.expressionResolver
        )
        let imageURLs = $0.makeImageURLs(with: $1.expressionResolver, filter: filter)
        let videoURLs = $0.makeVideoURLs(with: $1.expressionResolver, filter: filter)
        return extensionURLs + imageURLs + videoURLs
      },
      context: context
    )
    .flatMap { $0 }
    .filter { $0.host != nil }

    if validURLs.isEmpty {
      completion(true)
      return
    }

    var successes = 0
    var failures = 0
    validURLs.forEach { url in
      _ = resourceRequester.getData(from: url) { result in
        switch result {
        case .success: successes += 1
        case .failure: failures += 1
        }
        if successes + failures == validURLs.count {
          onMainThread { [failures] in
            completion(failures == 0)
          }
        }
      }
    }
  }
}

extension DivData {
  fileprivate func flatMap<T>(
    _ transform: (Div, DivBlockModelingContext) -> T,
    context: DivBlockModelingContext
  ) -> [T] {
    var result: [T] = []

    context.functionsStorage?.setIfNeeded(
      path: context.path,
      functions: functions ?? []
    )

    context.variablesStorage.initializeIfNeeded(
      path: context.path,
      variables: variables?.extractDivVariableValues(context.expressionResolver) ?? [:]
    )

    func traverse(div: Div, divContext: DivBlockModelingContext) {
      div.value.setupContextWithVariablesAndFunctions(context: divContext)
      result.append(transform(div, divContext))

      if let container = div.value as? DivContainer, let itemBuilder = container.itemBuilder {
        itemBuilder.makeItemDivAndContexts(context: divContext).forEach { div, itemContext in
          traverse(div: div, divContext: itemContext)
        }
      }

      div.children.forEach {
        let childContext = $0.value.modifiedContextParentPath(divContext)
        traverse(div: $0, divContext: childContext)
      }
    }
    states.map(\.div).forEach {
      let stateContext = $0.value.modifiedContextParentPath(context)
      $0.value.setupContextWithVariablesAndFunctions(context: stateContext)
      traverse(div: $0, divContext: stateContext)
    }
    return result
  }
}
