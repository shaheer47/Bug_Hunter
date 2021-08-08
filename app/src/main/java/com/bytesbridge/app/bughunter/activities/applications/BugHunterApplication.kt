package com.bytesbridge.app.bughunter.activities.applications

import android.app.Application
import androidx.multidex.MultiDex
import androidx.multidex.MultiDexApplication
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class BugHunterApplication : MultiDexApplication(){
    ;

}