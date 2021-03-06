package io.github.t3r1jj.pbmap.model

import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.SmallTest
import io.github.t3r1jj.pbmap.model.dictionary.Dictionary
import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
open class DictionaryTest {

    @Test
    @SmallTest
    fun getLanguages() {
        val languages = Dictionary().getLanguages()
        assertEquals(listOf("default", "en_GB", "en_US", "ja", "pl", "ru"), languages.toList())
    }

    @Test
    @SmallTest
    fun getUnitSystems() {
        val unitSystems = Dictionary().getUnitSystems()
        assertEquals(listOf("SI", "US"), unitSystems.toList())
    }
}