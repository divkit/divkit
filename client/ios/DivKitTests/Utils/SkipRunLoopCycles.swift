@MainActor
func skipMainRunLoopCycles(_ count: Int) async {
  for _ in 0..<count+1 {
    await Task.yield()
  }
}
