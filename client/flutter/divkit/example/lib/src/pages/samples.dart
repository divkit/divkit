import 'dart:convert';
import 'dart:math';

import 'package:divkit/divkit.dart';
import 'package:flutter/material.dart';
import 'package:flutter/services.dart';

class SamplesPage extends StatefulWidget {
  const SamplesPage({super.key});

  @override
  State<StatefulWidget> createState() => _SamplesPage();
}

class _SamplesPage extends State<SamplesPage> {
  final _controller = ScrollController(keepScrollOffset: true);

  Future<List<Map<String, dynamic>>> loadAssetsList() async {
    final Map<String, dynamic> manifestMap = json.decode(
      await rootBundle.loadString('AssetManifest.json'),
    );
    manifestMap.removeWhere(
      (key, value) =>
          !key.startsWith('assets/test_data/samples') || !key.endsWith('json'),
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
        backgroundColor: const Color(0xFFA09DE5),
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
                return CustomScrollView(
                  controller: _controller,
                  slivers: [
                    SliverAppBar(
                      foregroundColor: Colors.black,
                      floating: false,
                      pinned: true,
                      expandedHeight: 220,
                      flexibleSpace: LayoutBuilder(
                        builder: (context, constraints) {
                          final percent =
                              (constraints.maxHeight - kToolbarHeight) *
                                  (100 / (220 - kToolbarHeight));

                          final dx = max(100 - percent, 16.0);

                          return ClipPath(
                            child: Column(
                              crossAxisAlignment: CrossAxisAlignment.start,
                              mainAxisSize: MainAxisSize.min,
                              children: [
                                Padding(
                                  padding: EdgeInsets.only(
                                    left: dx,
                                    top: kToolbarHeight / 4 +
                                        min(constraints.maxHeight, 160) -
                                        kToolbarHeight,
                                  ),
                                  child: const Text(
                                    'Samples',
                                    style: TextStyle(
                                      fontSize: 24,
                                      fontWeight: FontWeight.w500,
                                    ),
                                  ),
                                ),
                                Flexible(
                                  child: Container(
                                    height: 100,
                                    padding:
                                        const EdgeInsets.fromLTRB(16, 8, 16, 0),
                                    child: const Text(
                                      'Here you can check, how Div technology might be useful for your projects',
                                      style: TextStyle(
                                        fontSize: 20,
                                      ),
                                    ),
                                  ),
                                ),
                              ],
                            ),
                          );
                        },
                      ),
                      backgroundColor: const Color(0xFFA09DE5),
                    ),
                    SliverToBoxAdapter(
                      child: Column(
                        children: List.generate(data.length, (i) {
                          try {
                            return Container(
                              decoration: BoxDecoration(
                                borderRadius: i == 0
                                    ? const BorderRadius.only(
                                        topLeft: Radius.circular(30),
                                        topRight: Radius.circular(30),
                                      )
                                    : null,
                                color: Colors.white,
                              ),
                              child: DivKitView(
                                showUnsupportedDivs: true,
                                data: DefaultDivKitData.fromJson(data[i]),
                              ),
                            );
                          } catch (e) {
                            return const SizedBox.shrink();
                          }
                        }),
                      ),
                    ),
                  ],
                );
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
