class SnapshotTestModel {
  final String caseDescription;
  final Map<String, dynamic> testData;
  final Map<String, dynamic>? testTemplates;
  final List<String>? availablePlatforms;

  const SnapshotTestModel({
    required this.caseDescription,
    required this.testData,
    this.testTemplates,
    this.availablePlatforms,
  });

  factory SnapshotTestModel.fromJson(Map<String, dynamic> json) =>
      SnapshotTestModel(
        caseDescription: json['description'] as String,
        testData: json['card'] as Map<String, dynamic>,
        testTemplates: json['templates'] as Map<String, dynamic>?,
        availablePlatforms: (json['platforms'] as List<dynamic>?)
            ?.map((e) => e as String)
            .toList(),
      );
}
