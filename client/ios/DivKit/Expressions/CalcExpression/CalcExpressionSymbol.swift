import Foundation

import VGSL

extension CalcExpression {
  enum Symbol: Hashable {
    case variable(String)
    case infix(String)
    case prefix(String)
    case postfix(String)
    case function(String)
    case method(String)

    var type: String {
      switch self {
      case .variable:
        "variable"
      case .prefix, .infix, .postfix:
        "operator"
      case .function:
        "function"
      case .method:
        "method"
      }
    }

    var name: String {
      switch self {
      case let .variable(name),
           let .infix(name),
           let .prefix(name),
           let .postfix(name),
           let .function(name),
           let .method(name):
        name
      }
    }

    func formatExpression(_ args: [Any]) -> String {
      switch self {
      case .prefix:
        "\(name)\(formatArgForError(args[0]))"
      case .infix, .postfix, .variable:
        name
      case .function:
        formatFunction(args)
      case .method:
        formatFunction(Array(args.dropFirst()))
      }
    }

    private func formatFunction(_ args: [Any]) -> String {
      let argsString = args
        .map { formatArgForError($0) }
        .joined(separator: ", ")
      return "\(name)(\(argsString))"
    }
  }
}
