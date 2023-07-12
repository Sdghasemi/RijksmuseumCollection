package com.hirno.museum

import android.app.Application
import com.hirno.museum.data.source.CollectionsRepository

class MainApplication : Application() {
    /**
     * Collections repository used for accessing collections inside the app itself
     */
    val collectionsRepository: CollectionsRepository
        get() = ServiceLocator.provideCollectionsRepository(this)
}