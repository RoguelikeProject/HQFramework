package kr.hqservice.framework.nms.wrapper.item

import kr.hqservice.framework.nms.wrapper.NmsWrapper

class NmsCustomDataWrapper(
    private val nbtTagCompoundWrapper: NmsNBTTagCompoundWrapperImpl
) : NmsWrapper {



    override fun getUnwrappedInstance(): Any {
        return nbtTagCompoundWrapper
    }
}