package kr.hqservice.framework.nms.wrapper

interface NmsNBTTagCompoundWrapper : NmsWrapper {

    fun getString(key: String, def: String = ""): String

    fun setString(key: String, value: String)
}