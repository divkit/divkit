import Combine
import Foundation

final class AppRouter: ObservableObject {
  @Published var pendingRegressionTest: RegressionTestModel?
  @Published var pendingPlaygroundURL: URL?

  private let tests: [RegressionTestModel]

  init(tests: [RegressionTestModel] = []) {
    self.tests = tests
  }

  func handle(_ url: URL) {
    guard url.scheme == scheme else { return }
    switch url.host {
    case openTestHost:
      openTest(url)
    case openJsonHost:
      openJson(url)
    default:
      AppLogger.error("Unknown deep link host: \(url)")
    }
  }

  private func openTest(_ url: URL) {
    let items = URLComponents(url: url, resolvingAgainstBaseURL: false)?.queryItems ?? []
    if let idValue = items.first(where: { $0.name == idParam })?.value,
       let id = Int(idValue) {
      pendingRegressionTest = tests.first { $0.caseId == id }
    } else if let title = items.first(where: { $0.name == titleParam })?.value {
      pendingRegressionTest = tests.first {
        $0.title.caseInsensitiveCompare(title) == .orderedSame
      }
    }
    if pendingRegressionTest == nil {
      AppLogger.error("Test not found for deep link: \(url)")
    }
  }

  private func openJson(_ url: URL) {
    guard
      let urlValue = URLComponents(url: url, resolvingAgainstBaseURL: false)?
      .queryItems?.first(where: { $0.name == urlParam })?.value,
      let jsonURL = URL(string: urlValue)
    else {
      AppLogger.error("Invalid JSON URL in deep link: \(url)")
      return
    }
    pendingPlaygroundURL = jsonURL
  }
}

private let scheme = "playground"
private let openTestHost = "test"
private let openJsonHost = "json"
private let idParam = "id"
private let titleParam = "title"
private let urlParam = "url"
