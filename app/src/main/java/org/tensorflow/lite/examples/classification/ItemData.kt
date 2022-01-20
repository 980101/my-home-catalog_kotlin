package org.tensorflow.lite.examples.classification

class ItemData {
    var image: String? = null
    var name: String? = null
    var price: String? = null
    var link: String? = null

    // 생성자
    // 기본
    constructor() {}

    // 사용자 지정
    constructor(image: String?, name: String?, price: String?, link: String?) {
        this.image = image
        this.name = name
        this.price = price
        this.link = link
    }
}