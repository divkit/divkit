package divkit.dsl

import divkit.dsl.scope.DivScope

@Deprecated(message = "Use method `color(argb: String)` without DivScope receiver", replaceWith = ReplaceWith("color"))
fun DivScope.color(argb: String): Color = divkit.dsl.color(argb)

@Deprecated(message = "Use method `url(url: String)` without DivScope receiver", replaceWith = ReplaceWith("url"))
fun DivScope.url(url: String): Url = divkit.dsl.url(url)
