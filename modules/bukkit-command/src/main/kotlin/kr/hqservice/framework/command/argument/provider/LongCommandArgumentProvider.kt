package kr.hqservice.framework.command.argument.provider

import kr.hqservice.framework.command.CommandArgumentProvider
import kr.hqservice.framework.command.CommandContext
import kr.hqservice.framework.command.argument.exception.ArgumentFeedback
import kr.hqservice.framework.global.core.component.Component
import org.bukkit.Location

@Component
class LongCommandArgumentProvider : CommandArgumentProvider<Long> {
    override suspend fun getTabComplete(context: CommandContext, location: Location?): List<String> {
        return listOf(context.getArgumentLabel())
    }

    override suspend fun cast(context: CommandContext, argument: String?): Long {
        argument ?: throw ArgumentFeedback.RequireArgument
        return argument.toLongOrNull() ?: throw ArgumentFeedback.NotNumber
    }
}