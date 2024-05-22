package kr.hqservice.framework.nms.service.item

import kr.hqservice.framework.global.core.component.Qualifier
import kr.hqservice.framework.global.core.component.Service
import kr.hqservice.framework.nms.Version
import kr.hqservice.framework.nms.service.NmsService
import kr.hqservice.framework.nms.wrapper.NmsReflectionWrapper
import kr.hqservice.framework.nms.wrapper.item.NmsCustomDataWrapper
import kr.hqservice.framework.nms.wrapper.item.NmsDataComponentMapWrapper
import kotlin.reflect.KClass

@Service
@Qualifier("data-component-map")
class NmsDataComponentMapService(
    private val reflectionWrapper: NmsReflectionWrapper,
    @Qualifier("custom-data") private val customDataService: NmsService<Any?, NmsCustomDataWrapper>,
) : NmsService<Any?, NmsDataComponentMapWrapper> {

    private val dataComponentMapClass = reflectionWrapper.getNmsClass("DataComponentMap",
        Version.V_20_6.handle("core.component")
    )

    override fun wrap(target: Any?): NmsDataComponentMapWrapper {
        return NmsDataComponentMapWrapper(
            target ?: throw IllegalArgumentException(),
            customDataService,
            reflectionWrapper
        )
    }

    override fun unwrap(wrapper: NmsDataComponentMapWrapper): Any {
        return wrapper.getUnwrappedInstance()
    }

    override fun getOriginalClass(): KClass<*> {
        return dataComponentMapClass
    }

    override fun getTargetClass(): KClass<*> {
        return dataComponentMapClass
    }
}