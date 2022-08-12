import Foundation

func makeURLWithFileNames(
  for subdirectory: String,
  withExtension: String
) -> [(URL, String)] {
  let urls = Bundle.main.urls(
    forResourcesWithExtension: withExtension,
    subdirectory: subdirectory
  )!

  return urls
    .map { url in (url, String(url.lastPathComponent.split(separator: ".").first!)) }
    .sorted { $0.1 < $1.1 }
}
