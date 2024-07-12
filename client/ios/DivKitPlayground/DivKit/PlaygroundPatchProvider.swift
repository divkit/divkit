import Foundation

import DivKit
import VGSL

struct PlaygroundPatchProvider: DivPatchProvider {
  private let downloader: DivPatchDownloader

  init(requestPerformer: URLRequestPerforming) {
    downloader = DivPatchDownloader(requestPerformer: requestPerformer)
  }

  func getPatch(url: URL, completion: @escaping DivPatchProviderCompletion) {
    if url.scheme == "local" {
      completion(parsePatch(fileName: url.host ?? ""))
    } else {
      downloader.getPatch(url: url, completion: completion)
    }
  }

  func cancelRequests() {
    downloader.cancelRequests()
  }

  private func parsePatch(fileName: String) -> Result<DivPatch, Error> {
    let url = Bundle.main.url(
      forResource: fileName,
      withExtension: "json",
      subdirectory: TestData.patchesPath
    )!
    do {
      let data = try Data(contentsOf: url)
      let patch = try parseDivPatch(data)
      return .success(patch)
    } catch {
      return .failure(error)
    }
  }
}
