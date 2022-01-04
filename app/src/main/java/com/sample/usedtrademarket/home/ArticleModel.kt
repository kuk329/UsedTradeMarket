package com.sample.usedtrademarket.home

data class ArticleModel(
    val sellerId: String, //  판매자 아이디
    val title : String, // 판매 물품
    val createdAt : Long, // 글 작성한 시간
    val price : String, // 가격
    val imageUrl : String // 사진 이미지
){
    constructor():this("","",0,"","")
}