package coreMindustry.lib

import arc.func.Cons
import arc.util.Log
import cf.wayzer.script_agent.IContentScript
import cf.wayzer.script_agent.content.ContentScript
import cf.wayzer.script_agent.util.DSLBuilder.Companion.dataKeyWithDefault
import coreMindustry.lib.ContentExt.allCommands
import coreMindustry.lib.ContentExt.listener
import mindustry.entities.type.Player

object ContentExt {
    val IContentScript.allCommands by dataKeyWithDefault { mutableListOf<CommandInfo>() }
    val IContentScript.listener by dataKeyWithDefault { mutableListOf<Listener<*>>() }

    data class CommandInfo(
            val name: String,
            val description: String,
            val param: String = "",
            val type: CommandType = CommandType.Both,
            val runner: (arg: Array<String>, player: Player?) -> Unit
    )

    data class Listener<T : Any>(
        val script: IContentScript?,
        val cls: Class<T>,
        val handler: (T) -> Unit
    ) : Cons<T> {
        override fun get(p0: T) {
            try {
                if (script?.enabled != false) handler(p0)
            } catch (e: Exception) {
                Log.err("Error when handle event $cls in ${script?.clsName ?: "Unknown"}", e)
            }
        }
    }
}

enum class CommandType {
    Client, Server, Both;

    fun client() = this == Client || this == Both
    fun server() = this == Server || this == Both
}

inline fun <reified T : Any> ContentScript.listen(noinline handler: (T) -> Unit) {
    listener.add(ContentExt.Listener(this, T::class.java, handler))
}

fun ContentScript.command(
        name: String,
        description: String,
        param: String = "",
        type: CommandType = CommandType.Both,
        runner: (arg: Array<String>, player: Player?) -> Unit
) {
    allCommands.add(ContentExt.CommandInfo(name, description, param, type, runner))
}