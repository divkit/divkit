import XCTest

import CommonCore
import Networking

final class MordaProductionTests: DivKitSnapshotTestCase {
  override func setUp() {
    super.setUp()
    rootDirectory = "production_data"
    subdirectory = "morda"
  }

  func test_districtCardIsValid() {
    testDivs("district_day.json", imageHolderFactory: imageHolderFactory)
  }

  func test_newsCardIsValid() {
    testDivs("news.json", imageHolderFactory: imageHolderFactory)
  }

  func test_poiCardIsValid() {
    testDivs("poi.json", imageHolderFactory: imageHolderFactory)
  }

  func test_trainsCardIsValid() {
    // if this test fails with hanging quotation mark,
    // you need to change your simulator language to English
    testDivs("trains.json", imageHolderFactory: imageHolderFactory)
  }

  func test_nhlNewsCardIsValid() {
    testDivs("nhl_news.json", imageHolderFactory: imageHolderFactory)
  }

  func test_nhlNewsItemsIsValid() {
    testDivs("nhl_news_tests.json", imageHolderFactory: imageHolderFactory)
  }

  func test_nhlStandingTablesCardIsValid() {
    testDivs("nhl_standing_tables.json", imageHolderFactory: imageHolderFactory)
  }

  func test_nhlLiveBroarcastCardIsValid() {
    testDivs("nhl.json", imageHolderFactory: imageHolderFactory)
  }

  func test_nhlLiveBroarcastItemsIsValid() {
    testDivs("nhl_tests.json", imageHolderFactory: imageHolderFactory)
  }

  func test_nhlGamesTodayTablesCardIsValid() {
    testDivs("nhl_games_today_tables.json", imageHolderFactory: imageHolderFactory)
  }
}

private let resourceNameByURLString = [
  "https://dev/null/": "chess",
  "https://yastatic.net/s3/home/yandex-app/services_div/redesign/zen_menu_dark.3.png": "zen_menu_dark.3",
  "https://api.yastatic.net/morda-logo/i/yandex-app/district/district_day.3.png": "district_day",
  "https://api.yastatic.net/morda-logo/i/yandex-app/district/images_day.1.png": "image_icon",
  "https://avatars.mds.yandex.net/get-yapic/40841/520495100-1548277370/islands-200": "avatar_200",
  "https://alicekit.s3.yandex.net/images_for_divs/chess.png": "chess",
  "https://s3.mds.yandex.net/mobile-yandex-client-android/divkit/images/icons/exclamation_mark.png": "exclamation_mark",
  "https://avatars.mds.yandex.net/get-bass/787408/poi_48x48_ee9550bc195fdc5d7c1d281ea5d8d776320345e0a67b0663c4fdde14e194393b.png/orig": "rating_star_1",
  "https://avatars.mds.yandex.net/get-bass/469429/poi_48x48_188933e7030027690ed55b5614b60fa77e0e4b50b86dde48d166714096ed0b0e.png/orig": "rating_star_05",
  "https://avatars.mds.yandex.net/get-bass/397492/poi_48x48_4ce4cec5ea8f8336bc3792a4899c1e9958531fcf9f8aabc4dd319ddaf5deafa0.png/orig": "rating_star_0",
  "https://avatars.mds.yandex.net/get-pdb/1340633/88a085e7-7254-43ff-805a-660b96f0e6ce/s1200?webp=false": "karavaevy",
  "https://s3.mds.yandex.net/mobile-yandex-client-android/divkit/images/trains/express-3x.png": "train_icon",
  "https://s3.mds.yandex.net/mobile-yandex-client-android/divkit/images/trains/route.png": "route_icon",
  "https://s3.mds.yandex.net/mobile-yandex-client-android/divkit/images/trains/Schedule.png": "schedule_icon",
  "https://yastatic.net/s3/home/yandex-app/services_div/general/menu_points.3.png": "menu_icon2",
  "https://yastatic.net/s3/home/yandex-app/services_div/general/2menu_points.2.png": "menu_icon3",
  "https://s3.mds.yandex.net/mobile-yandex-client-android/divkit/images/music_promo/jazz.png": "radio_jazz",
  "https://s3.mds.yandex.net/mobile-yandex-client-android/divkit/images/music_promo/pop.png": "radio_pop",
  "https://s3.mds.yandex.net/mobile-yandex-client-android/divkit/images/music_promo/rock.png": "radio_rock",
  "https://s3.mds.yandex.net/mobile-yandex-client-android/divkit/images/music_promo/yours.png": "radio_yours",
]

private let testBundle = Bundle(for: DivKitSnapshotTestCase.self)

private let imageHolderFactory = ImageHolderFactory(make: { url, _ in
  guard let absoluteString = url?.absoluteString else {
    XCTFail("Predefined images not supported in tests")
    return UIImage()
  }
  guard let imageName = resourceNameByURLString[absoluteString],
        let image = UIImage(named: imageName, in: testBundle, compatibleWith: nil) else {
    XCTFail(
      "Loading images from network is prohibited in tests. You need to load image from "
        + absoluteString + " and add it to Images.xcassets in testing bundle"
    )
    return UIImage()
  }
  return image
})
