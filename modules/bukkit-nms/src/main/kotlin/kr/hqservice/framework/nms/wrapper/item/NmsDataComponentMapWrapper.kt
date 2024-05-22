package kr.hqservice.framework.nms.wrapper.item

import kr.hqservice.framework.nms.Version
import kr.hqservice.framework.nms.service.NmsService
import kr.hqservice.framework.nms.wrapper.NmsReflectionWrapper
import kr.hqservice.framework.nms.wrapper.NmsWrapper
import kr.hqservice.framework.nms.wrapper.getFunction

class NmsDataComponentMapWrapper(
    private val dataComponentMap: Any,
    private val customDataService: NmsService<Any?, NmsCustomDataWrapper>,
    reflectionWrapper: NmsReflectionWrapper
) : NmsWrapper {

    private val dataComponentMapClass = reflectionWrapper.getNmsClass("DataComponentMap",
        Version.V_20_6.handle("core.component")
    )

    private val dataComponentType = reflectionWrapper.getNmsClass("DataComponentType",
        Version.V_20_6.handle("core.component")
    )
    private val dataComponentsClass = reflectionWrapper.getNmsClass("DataComponents",
        Version.V_20_6.handle("core.component")
    )
    private val customDataComponentType = reflectionWrapper.getStaticField(dataComponentsClass, "CUSTOM_DATA").call()!!

    private val getCustomDataFunction = reflectionWrapper.getFunction(dataComponentMapClass, "get", listOf(dataComponentType),
        Version.V_20_6.handleFunction("get") { setParameterClasses(dataComponentType) }
    )

    fun getNmsCustomDataWrapper(): NmsCustomDataWrapper {
        return getCustomDataFunction.call(dataComponentMap, customDataComponentType).run(customDataService::wrap)
    }

    override fun getUnwrappedInstance(): Any {
        return dataComponentMap
    }
}