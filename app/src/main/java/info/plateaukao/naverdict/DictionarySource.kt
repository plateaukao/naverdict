package info.plateaukao.naverdict

enum class DictionarySource(
    val javascriptString: String,
    val searchString: String,
    val homeString: String,
) {
    Naver(
        "javascript:(function() { " +
                // thick header
                "document.getElementById(\"header\").remove();" +
                // wordbook button
                "document.getElementsByClassName(\"nav_wordbook _nav_wordbook\")[0].style.display = \"none\";" +
                // all / vocab / examples bar
                "document.getElementsByClassName(\"Nlnb_menu_list\")[0].style.display = \"none\";" +
                "})();",
        "https://zh.dict.naver.com/#/search?query=",
        "https://zh.dict.naver.com/",

        ),
    Goo(
        "javascript:(function() { " +
                // thick header
                "document.getElementById(\"NR-onegoo\").remove();" +
                // wordbook button
                "document.getElementsByClassName(\"NR-ad\")[0].remove();" +
                // all / vocab / examples bar
                "document.getElementsByClassName(\"basic_title nolink\")[0].remove();" +
                "document.getElementByClassName(\"meaning\").scrollIntoView();" +
                "})();",
        "https://dictionary.goo.ne.jp/search.php?kind=all&SH=1&IE=UTF-8&MT=",
        "https://dictionary.goo.ne.jp/",
    )
}