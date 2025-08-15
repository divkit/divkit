import Flutter
import DivKit
#if canImport(VGSLUI)
import VGSLUI
#elseif canImport(CommonCorePublic)
import CommonCorePublic
#endif
import os

private class ApiImplementation: NativeDivExpressionsResolver {
    func resolve(expression: String, context: [String: Any], completion: @escaping (Result<Any?, Error>) -> Void) {
        var resolverVariables = DivVariables()
        context.forEach { element in
            if let parsedValue = parseVariableValue(value: element.value) {
                resolverVariables[DivVariableName(rawValue: element.key)] = parsedValue
            } else {
                completion(.failure(FlutterError(code: "types", message: "Unsupported variable type: \(type(of: element.value))", details: nil)))
                return
            }
        }

        var errorMessage = ""
        let resolver = DivKit.ExpressionResolver(
            variables: resolverVariables,
            persistentValuesStorage: DivPersistentValuesStorage(),
            errorTracker: { errorMessage = $0.description }
        )

        let res = resolver.resolveString(expression)
        if errorMessage != "" {
            completion(.failure(FlutterError(code: "error", message: errorMessage, details: nil)))
            return
        }

        completion(.success(clearOut(res)))
    }

    func clearVariables(completion: @escaping (Result<Void, Error>) -> Void) {
        completion(.success(()))
    }

    func clearOut(_ output: String?) -> String? {
        if output != nil {
            let pattern = "AnyHashable\\(([^)]+)\\)"
            let regex = try! NSRegularExpression(pattern: pattern, options: [])
            let range = NSRange(output!.startIndex..<output!.endIndex, in: output!)
            
            // This will replace each match with the captured group in the parentheses.
            return regex.stringByReplacingMatches(in: output!, options: [], range: range, withTemplate: "$1")
        }
        return nil
    }

    func parseVariableValue(value: Any) -> DivVariableValue? {
        switch value {
            case let value as NSNumber:
                // https://developer.apple.com/library/archive/documentation/Cocoa/Conceptual/ObjCRuntimeGuide/Articles/ocrtTypeEncodings.html
                switch UnicodeScalar(UInt8(value.objCType.pointee)) {
                    case "c", "B":
                        return DivVariableValue.bool(value as! Bool)
                    case "f", "d":
                        return DivVariableValue.number(value as! Double)
                    default:
                        return DivVariableValue.integer(value as! Int)
                }
            case let value as IntColor:
                return DivVariableValue.color(intToColor(UInt32(value.value)))
            case let value as String:
                return DivVariableValue.string(value)
            case let value as StringUrl:
                return DivVariableValue.url(URL(string: value.value)!)
            case let value as [AnyHashable]:
                return DivVariableValue.array(convertArray(value))
            case let value as [String: AnyHashable]:
                return DivVariableValue.dict(convertDictionary(value))
            default:
                return nil
        }
    }

    func convertType(_ value: AnyHashable) -> AnyHashable {
            switch value {
                case let value as NSNumber:
                    // https://developer.apple.com/library/archive/documentation/Cocoa/Conceptual/ObjCRuntimeGuide/Articles/ocrtTypeEncodings.html
                    switch UnicodeScalar(UInt8(value.objCType.pointee)) {
                        case "c", "B":
                            return value as! Bool
                        case "f", "d":
                            return value as! Double
                        default:
                            return value as! Int
                    }
                case let value as IntColor:
                    return intToColor(UInt32(value.value))
                case let value as String:
                    return value
                case let value as StringUrl:
                    return URL(string: value.value)!
                case let value as [AnyHashable]:
                    return convertArray(value)
                case let value as [String: AnyHashable]:
                    return convertDictionary(value)
                default:
                    return value
            }
   }

   func convertDictionary(_ dict: [String: AnyHashable]) -> [String: AnyHashable] {
       var newDict = [String: AnyHashable]()
       for (key, value) in dict {
           newDict[key] = convertType(value)
       }
       return newDict
   }

   func convertArray(_ array: [AnyHashable]) -> [AnyHashable] {
       return array.map { convertType($0) }
   }

   func intToColor(_ hex: UInt32) -> RGBAColor {
       let alpha = UInt8(hex >> 24 )
       let red = UInt8((hex & 0x00_FF_00_00) >> 16)
       let green = UInt8((hex & 0x00_00_FF_00) >> 8)
       let blue = UInt8(hex & 0x00_00_00_FF)

       return RGBAColor(
         red: CGFloat(red) / 255,
         green: CGFloat(green) / 255,
         blue: CGFloat(blue) / 255,
         alpha: CGFloat(alpha) / 255
       )
     }
}

public class DivExpressionsResolverPlugin: NSObject, FlutterPlugin {
    public static func register(with registrar: FlutterPluginRegistrar) {
        let messenger : FlutterBinaryMessenger = registrar.messenger()
        let api = ApiImplementation()
        NativeDivExpressionsResolverSetup.setUp(binaryMessenger: messenger, api: api)
    }
}
