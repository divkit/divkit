import catapulse
from catapulse_tests.common import instrumented


class DivKitPerfTestSuite(instrumented.InstrumentedTestSuite):

    def __init__(self):
        super(DivKitPerfTestSuite, self).__init__(
            instrumented_app='com.yandex.divkit.demo',
            test_app='com.yandex.divkit.perftests.test',
            mock_app='com.yandex.divkit.perftests',
            instrumentation_timeout=240
        )
