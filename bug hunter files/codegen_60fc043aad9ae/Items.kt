/* 
Copyright (c) 2021 Kotlin Data Classes Generated from JSON powered by http://www.json2kotlin.com

Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.

For support, please feel free to contact me at https://www.linkedin.com/in/syedabsar */


data class Items (

	@SerializedName("tags") val tags : List<String>,
	@SerializedName("owner") val owner : Owner,
	@SerializedName("is_answered") val is_answered : Boolean,
	@SerializedName("view_count") val view_count : Int,
	@SerializedName("closed_date") val closed_date : Int,
	@SerializedName("answer_count") val answer_count : Int,
	@SerializedName("score") val score : Int,
	@SerializedName("last_activity_date") val last_activity_date : Int,
	@SerializedName("creation_date") val creation_date : Int,
	@SerializedName("last_edit_date") val last_edit_date : Int,
	@SerializedName("question_id") val question_id : Int,
	@SerializedName("link") val link : String,
	@SerializedName("closed_reason") val closed_reason : String,
	@SerializedName("title") val title : String
)