import 'snapshot_test_model.dart';

abstract class GoldenTestFile {}

class GoldenTestCase implements GoldenTestFile {
  final String name;
  final SnapshotTestModel testCase;

  const GoldenTestCase(this.name, this.testCase);
}

class GoldenTestGroup implements GoldenTestFile {
  final String name;
  final List<GoldenTestFile> testCases;

  const GoldenTestGroup(this.name, this.testCases);
}
