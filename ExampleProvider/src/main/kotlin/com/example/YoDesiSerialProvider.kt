package com.example

import com.lagradost.cloudstream3.*
import org.jsoup.Jsoup
import org.jsoup.nodes.Element
import java.util.ArrayList

class YoDesiSerialProvider : MainAPI() { // all providers must be an instance of MainAPI
    override var mainUrl = "https://yodesiserial.su/"
    override var name = "YoDesiSerial.su"
    override val supportedTypes = setOf(TvType.Movie)


    override var lang = "en"

    // enable this when your provider has a main page
    override val hasMainPage = true

    override suspend fun getMainPage(page: Int, request: MainPageRequest): HomePageResponse? {
        val html = app.get("$mainUrl").text
        val document = Jsoup.parse(html)
        val all = ArrayList<HomePageList>()

        val map = mapOf(
            "Trending Movies" to "div.mag-box-container.clearfix",
        )
        map.forEach {
            all.add(HomePageList(
                it.key,
                document.select(it.value).select("posts-items.posts-list-container").map { element ->
                    element.toSearchResult()
                }
            ))
        }
        return HomePageResponse(all)
    }
    private fun Element.toSearchResult(): SearchResponse {
        val innerLinkItem = this.selectFirst("a")
        val title = innerLinkItem!!.attr("aria-label")
        val href = innerLinkItem!!.attr("href")
        val imgItem = innerLinkItem!!.selectFirst("img")
        val posterUrl = imgItem!!.attr("src")
        var year: Int? = null
        var quality: SearchQuality? = null

        return MovieSearchResponse(
            title,
            href,
            this@YoDesiSerialProvider.name,
            TvType.Movie,
            posterUrl = posterUrl,
            year = year,
            quality = quality,
        )
    }

    // this function gets called when you search for something
    override suspend fun search(query: String): List<SearchResponse> {
        return listOf<SearchResponse>()
    }

//    override suspend fun search(query: String): List<SearchResponse> {
//        val url = "$mainUrl/search/${query.replace(" ", "-")}"
//        val html = app.get(url).text
//        val document = Jsoup.parse(html)
//
//        return document.select("div.flw-item").map {
//            val title = it.select("h2.film-name").text()
//            val href = fixUrl(it.select("a").attr("href"))
//            val year = it.select("span.fdi-item").text().toIntOrNull()
//            val image = it.select("img").attr("data-src")
//            val isMovie = href.contains("/movie/")
//
//            val metaInfo = it.select("div.fd-infor > span.fdi-item")
//            // val rating = metaInfo[0].text()
//            val quality = getQualityFromString(metaInfo.getOrNull(1)?.text())
//
//            if (isMovie) {
//                MovieSearchResponse(
//                    title,
//                    href,
//                    this.name,
//                    TvType.Movie,
//                    image,
//                    year,
//                    quality = quality
//                )
//            } else {
//                TvSeriesSearchResponse(
//                    title,
//                    href,
//                    this.name,
//                    TvType.TvSeries,
//                    image,
//                    year,
//                    null,
//                    quality = quality
//                )
//            }
//        }
//    }

}

//suspend fun main() {
//    val yoDesiSerialProvider = YoDesiSerialProvider()
//    println(yoDesiSerialProvider.getMainPage(0, MainPageRequest("", "")))
//}