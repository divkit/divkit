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
    for url in validURLs {
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
        for (div, itemContext) in itemBuilder.makeItemDivAndContexts(context: divContext) {
          traverse(div: div, divContext: itemContext)
        }
      }

      for child in div.children {
        let childContext = child.value.modifiedContextParentPath(divContext)
        traverse(div: child, divContext: childContext)
      }
    }
    for state in states.map(\.div) {
      let stateContext = state.value.modifiedContextParentPath(context)
      state.value.setupContextWithVariablesAndFunctions(context: stateContext)
      traverse(div: state, divContext: stateContext)
    }
    return result
  }
}
