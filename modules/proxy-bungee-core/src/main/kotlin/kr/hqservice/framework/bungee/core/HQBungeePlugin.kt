package kr.hqservice.framework.bungee.core

import kr.hqservice.framework.global.core.HQPlugin
import kr.hqservice.framework.global.core.component.registry.ComponentRegistry
import net.md_5.bungee.api.plugin.Plugin
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import org.koin.core.parameter.parametersOf
import java.io.File
import java.util.logging.Logger

abstract class HQBungeePlugin : Plugin(), HQPlugin, KoinComponent {
    protected open val componentRegistry: ComponentRegistry by inject { parametersOf(this, Plugin::class) }

    final override fun onLoad() {
        onPreLoad()
        onPostLoad()
    }

    final override fun onEnable() {
        onPreEnable()
        val stream = getResourceAsStream("config.yml")
        if(!dataFolder.exists()) dataFolder.mkdirs()
        val file = File(dataFolder, "config.yml")
        file.bufferedWriter().use {  writer ->
            stream?.reader()?.readLines()?.forEach {
                writer.appendLine(it)
            }
        }
        componentRegistry.setup()
        onPostEnable()
    }

    final override fun onDisable() {
        onPreDisable()
        componentRegistry.teardown()
        onPostDisable()
    }

    final override fun getJar(): File {
        return this.file
    }

    final override fun getLogger(): Logger {
        return super.getLogger()
    }
}