import CommonCorePublic

public protocol DivStateInterceptor {
  var id: String { get }

  func getAppropriateState(
    divState: DivState,
    context: DivBlockModelingContext
  ) -> DivState.State?
}

public final class DivStateInterceptorImpl: DivStateInterceptor {
  public let id: String
  private let stateId: Variable<DivStateID>

  public init(
    id: String,
    stateId: Variable<DivStateID>
  ) {
    self.id = id
    self.stateId = stateId
  }

  public func getAppropriateState(
    divState: DivState,
    context _: DivBlockModelingContext
  ) -> DivState.State? {
    guard
      let extensions = divState.extensions,
      let statesExtension = extensions.first(where: { $0.id == id }),
      let params = statesExtension.params,
      let statePath = statePath(
        stateParams: params,
        stateId: stateId.value
      )
    else {
      return nil
    }

    let stateId = statePath.split()?.stateId.rawValue
    return divState.states.first(where: { $0.stateId == stateId })
  }

  private func statePath(
    stateParams: [String: Any],
    stateId: DivStateID
  ) -> DivStatePath? {
    guard
      let stateArray = stateParams[stateId.rawValue] as? [String],
      let statePath = stateArray.first
    else {
      return nil
    }
    return DivStatePath.makeDivStatePath(from: statePath)
  }
}
