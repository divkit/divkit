// Copyright 2018 Yandex LLC. All rights reserved.

public class HTMLEntitiesResolver {
  private let symbolsToEscapeForXML: [String: String]
  private let entityNameToSymbol: [String: String]

  public init(
    customEscapeMap: [String: String] = [:],
    customSubstitutionMap: [String: String] = [:]
  ) {
    self.symbolsToEscapeForXML = HTMLEntities.symbolsToEscapeForXML
      .merging(customEscapeMap, uniquingKeysWith: { $1 })
    self.entityNameToSymbol = HTMLEntities.entityNameToSymbol
      .merging(customSubstitutionMap, uniquingKeysWith: { $1 })
  }

  public func resolveHTMLEntities(in string: String) -> String {
    var isSearchingHTMLEntity = false
    var htmlEntityName = ""
    var resultString = ""
    for character in string {
      if character == "&" {
        if isSearchingHTMLEntity {
          resultString.append(symbolsToEscapeForXML["&"]!)
          resultString.append(htmlEntityName)
          htmlEntityName = ""
        } else {
          isSearchingHTMLEntity = true
        }
      } else if character == ";", isSearchingHTMLEntity {
        if let resolvedEntity = resolveHTMLEntity(htmlEntityName) {
          resultString.append(resolvedEntity)
        } else {
          resultString.append(symbolsToEscapeForXML["&"]!)
          resultString.append(htmlEntityName)
          resultString.append(";")
        }
        htmlEntityName = ""
        isSearchingHTMLEntity = false
      } else if isSearchingHTMLEntity {
        htmlEntityName += String(character)
      } else {
        resultString += String(character)
      }
    }

    if isSearchingHTMLEntity {
      resultString.append(symbolsToEscapeForXML["&"]!)
      resultString.append(htmlEntityName)
    }

    return resultString
  }

  private func resolveHTMLEntity(_ entity: String) -> String? {
    if entity.hasPrefix("#x") {
      let index = entity.index(entity.startIndex, offsetBy: 2)
      let unicodeNumber = entity[index...]
      return Int(unicodeNumber, safeRadix: .hex).flatMap(makeUnicodeFromNumericEntity)
    } else if entity.hasPrefix("#") {
      let index = entity.index(entity.startIndex, offsetBy: 1)
      let unicodeNumber = entity[index...]
      return Int(unicodeNumber, safeRadix: .decimal).flatMap(makeUnicodeFromNumericEntity)
    }

    return entityNameToSymbol[entity]
  }

  private func makeUnicodeFromNumericEntity(_ entity: Int) -> String? {
    guard let unicode = UnicodeScalar(entity).map({ "\($0)" }) else {
      return nil
    }
    return symbolsToEscapeForXML[unicode] ?? unicode
  }
}
