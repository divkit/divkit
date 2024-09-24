import Foundation

final class CustomFunction: SimpleFunction {
  var signature: FunctionSignature

  struct Signature: Hashable {
    var name: String
    var arguments: [DivEvaluableType]
  }

  struct Argument {
    let name: String
    let type: DivEvaluableType
  }

  let name: String
  private let arguments: [Argument]
  private let body: String

  init(
    name: String,
    arguments: [Argument],
    body: String,
    returnType: Any.Type
  ) {
    self.signature = FunctionSignature(
      arguments: arguments.map { ArgumentSignature(type: $0.type.systemType) },
      resultType: returnType
    )
    self.name = name
    self.arguments = arguments
    self.body = body
  }

  func invoke(_ args: [Any], context: ExpressionContext) throws -> Any {
    let matchedArguments: [String: Any] = Dictionary(
      arguments.map { $0.name },
      args
    )
    let resolver = ExpressionResolver(
      variableValueProvider: { [weak self] name in
        if let argument = matchedArguments[name] {
          return argument
        }
        context.errorTracker(
          ExpressionError(
            "Argument with name: \(name) is not found",
            expression: self?.body
          )
        )
        return nil
      },
      persistentValuesStorage: DivPersistentValuesStorage(),
      errorTracker: context.errorTracker
    )

    guard let result = resolver.resolve(body) else {
      throw ExpressionError(
        makeErrorMessage(name: name, arguments: arguments.map { $0.type.rawValue }, body: body)
      )
    }
    return result
  }
}

private func makeErrorMessage(name: String, arguments: [String], body: String) -> String {
  "Failed to evaluate custom function \(name)() with arguments: [\(arguments.joined(separator: ", "))] and body: \(body)."
}
