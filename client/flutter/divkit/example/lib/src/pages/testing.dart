import 'dart:convert';

import 'package:divkit/divkit.dart';
import 'package:flutter/material.dart';
import 'package:flutter/services.dart';

class TestingPage extends StatefulWidget {
  const TestingPage({super.key});

  @override
  State<StatefulWidget> createState() => _TestingPage();
}

class _TestingPage extends State<TestingPage> {
  int index = 0;

  final _controller = ScrollController(keepScrollOffset: true);

  Future<List<Map<String, dynamic>>> loadAssetsList() async {
    final Map<String, dynamic> manifestMap = json.decode(
      await rootBundle.loadString('AssetManifest.json'),
    );
    manifestMap.removeWhere(
      (key, value) =>
          !key.startsWith('assets/test_data/regression_test_data') ||
          !key.endsWith('json'),
    );
    List<Map<String, dynamic>> testCases = [];
    for (var el in manifestMap.keys) {
      final testCaseData = await jsonDecode(
        await rootBundle.loadString(
          el.replaceAll('../', ''),
        ),
      );
      testCases.add(testCaseData);
    }

    return testCases;
  }

  @override
  Widget build(BuildContext context) => Scaffold(
        appBar: AppBar(
          title: Text('Test #$index'),
          centerTitle: true,
        ),
        body: FutureBuilder<List<Map<String, dynamic>>>(
          future: loadAssetsList(),
          builder: (_, snapshot) {
            if (snapshot.hasError) {
              return Center(
                child: Text(snapshot.error.toString()),
              );
            }
            if (snapshot.hasData) {
              final data = snapshot.data;
              if (data != null) {
                try {
                  return Column(
                    crossAxisAlignment: CrossAxisAlignment.center,
                    children: [
                      Expanded(
                        child: SingleChildScrollView(
                          child: DivKitView(
                            showUnsupportedDivs: true,
                            data: DefaultDivKitData.fromJson(data[index]),
                          ),
                        ),
                      ),
                      const Divider(),
                      Padding(
                        padding: const EdgeInsets.symmetric(
                            vertical: 8.0, horizontal: 16),
                        child: Row(
                          children: [
                            if (index - 1 >= 0)
                              ElevatedButton(
                                onPressed: () {
                                  setState(() {
                                    index = index - 1;
                                  });
                                },
                                child: const Text('Back'),
                              ),
                            const Spacer(),
                            if (index + 1 < data.length)
                              ElevatedButton(
                                onPressed: () {
                                  setState(() {
                                    index = index + 1;
                                  });
                                },
                                child: const Text('Next'),
                              ),
                          ],
                        ),
                      ),
                    ],
                  );
                } catch (e, st) {
                  return Column(
                    crossAxisAlignment: CrossAxisAlignment.center,
                    children: [
                      Expanded(child: Center(child: Text("$e\n$st"))),
                      const Divider(),
                      Padding(
                        padding: const EdgeInsets.symmetric(
                            vertical: 8.0, horizontal: 16),
                        child: Row(
                          children: [
                            if (index - 1 >= 0)
                              ElevatedButton(
                                onPressed: () {
                                  setState(() {
                                    index = index - 1;
                                  });
                                },
                                child: const Text('Back'),
                              ),
                            const Spacer(),
                            if (index + 1 < data.length)
                              ElevatedButton(
                                onPressed: () {
                                  setState(() {
                                    index = index + 1;
                                  });
                                },
                                child: const Text('Next'),
                              ),
                          ],
                        ),
                      ),
                    ],
                  );
                }
              }
            }
            return const Center(
              child: CircularProgressIndicator(),
            );
          },
        ),
      );

  @override
  void dispose() {
    _controller.dispose();
    super.dispose();
  }
}
