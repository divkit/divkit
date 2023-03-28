import BaseTinyPublic

@frozen
public enum CommonHandler {
  case action(UserInterfaceAction)
  case block(Action)
}

extension CommonHandler: Equatable {
  public static func ==(
    lhs: CommonHandler,
    rhs: CommonHandler
  ) -> Bool {
    switch (lhs, rhs) {
    case let (.action(lAction), .action(rAction)):
      return lAction == rAction
    case (.action, _), (.block, _):
      return false
    }
  }
}
