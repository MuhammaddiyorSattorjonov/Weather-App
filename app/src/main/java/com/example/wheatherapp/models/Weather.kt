package com.example.wheatherapp.models

import android.security.identity.AccessControlProfileId

class Weather {
     var description: String? = null
     var icon: String? = null
     var id: Int? = null
     var main: String? = null

     constructor()

     constructor(description:String?,icon:String?,id: Int?,main:String?){
         this.description = description
         this.icon = icon
         this.id = id
         this.main = main
     }
 }