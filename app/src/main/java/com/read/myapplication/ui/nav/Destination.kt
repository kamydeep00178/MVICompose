package com.read.myapplication.ui.nav

sealed class Destination(val route: String) {
    object List : Destination("featureList")
    object Detail : Destination("detail/{itemId}") {
        fun createRoute(itemId: Int) = "detail/$itemId"
    }
}