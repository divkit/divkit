abstract class DivTimerManager {
  const DivTimerManager();

  /// Start timer by id.
  void start(String id);

  /// Stop timer by id.
  void stop(String id);

  /// Resume timer by id.
  void resume(String id);

  /// Pause timer by id.
  void pause(String id);

  /// Cancel timer by id.
  void cancel(String id);

  /// Reset timer by id.
  void reset(String id);

  /// Safely destroy manager.
  Future<void> dispose();
}
