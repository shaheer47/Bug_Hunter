package com.bytesbridge.app.bughunter.activities.utils

class HtmlAligner {
    companion object {
        fun alignHTML(textInHtml: String): String {

            var alignTextForResponse = textInHtml

            alignTextForResponse =
                alignTextForResponse.replace("<p>", " <h5> <font color=\"#000000\">  ")
            alignTextForResponse = alignTextForResponse.replace("</p>", " </font> </h5> ")
            alignTextForResponse =
                alignTextForResponse.replace("<code>", " <code> <font color=\"f4f4f4\"> ")
            alignTextForResponse = alignTextForResponse.replace("</code>", " </font> </code>")

            alignTextForResponse = alignTextForResponse.replace("\\n", " <br> ")

            return alignTextForResponse
        }
    }
}