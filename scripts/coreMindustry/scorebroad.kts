package coreMindustry
//WayZer 版权所有(请勿删除版权注解)
import arc.util.Align
import coreLibrary.lib.util.loop
import mindustry.gen.Groups
import java.time.Duration

name = "扩展功能: 积分榜"
//建议只修改下面一段,其他地方代码请勿乱动
val msg = """
[magenta]欢迎[goldenrod]{player.name}[magenta]来到[accent]New Horizon Mod官服
[violet]当前地图为: [yellow][{map.id}][orange]{map.name}
[violet]本局游戏时间: []{state.gameTime:分钟}
{scoreBroad.ext.*:${"\n"}}
[royal]输入/broad可以开关该显示
""".trimIndent()

val disabled = mutableSetOf<String>()

command("broad", "开关积分板显示") {
    this.type = CommandType.Client
    body {
        if (!disabled.remove(player!!.uuid()))
            disabled.add(player!!.uuid())
        reply("[green]切换成功".with())
    }
}

//避免找不到 scoreBroad.ext.* 变量
registerVar("scoreBroad.ext.null", "空占位", null)

onEnable {
    loop(Dispatchers.game) {
        delay(Duration.ofSeconds(2).toMillis())
        Groups.player.forEach {
            if (disabled.contains(it.uuid())) return@forEach
            val mobile = it.con?.mobile == true
            Call.infoPopup(
                it.con, msg.with().toPlayer(it), 2.013f,
                Align.topLeft, if (mobile) 210 else 155, 0, 0, 0
            )
        }
    }
}
