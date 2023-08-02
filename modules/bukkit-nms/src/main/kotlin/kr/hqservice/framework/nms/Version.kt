package kr.hqservice.framework.nms

import kr.hqservice.framework.nms.handler.FunctionType
import kr.hqservice.framework.nms.handler.VersionHandler
import kr.hqservice.framework.nms.handler.impl.CallableVersionHandler
import kr.hqservice.framework.nms.handler.impl.NameVersionHandler

enum class Version {
    V_7,
    V_8,
    V_9,
    V_10,
    V_11,
    V_12,
    V_13,
    V_14,
    V_15,
    V_16,
    V_17,
    V_18,
    V_19,
    V_19_1,
    V_19_3,
    V_19_4,
    V_20,

    // forge
    V_20_FORGE;

    fun support(version: Version, minor: Int = 0, forgeSupport: Boolean = false): Boolean {
        return if (minor != 0) try {
            support(Version.valueOf("${version.name}_$minor"), forgeSupport = forgeSupport)
        } catch (_: Exception) {
            support(version, forgeSupport = forgeSupport)
        } else {
            val ver = if (forgeSupport) try {
                Version.valueOf(version.name + "_FORGE")
            } catch (_: Exception) {
                version
            } else version
            ordinal <= ver.ordinal
        }
    }

    fun handle(name: String, changedName: Boolean = false): VersionHandler {
        return NameVersionHandler(this, name, changedName)
    }

    fun handleFunction(name: String, block: FunctionType.() -> Unit = {}): VersionHandler {
        if (name.isEmpty()) throw IllegalArgumentException("method without name")

        val type = FunctionType(name)
        block(type)
        return CallableVersionHandler(this, type)
    }
}